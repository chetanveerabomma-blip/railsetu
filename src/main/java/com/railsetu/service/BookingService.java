package com.railsetu.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.railsetu.domain.*;
import com.railsetu.dto.*;
import com.railsetu.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository passengerRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final CoachSeatRepository coachSeatRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final DigitalPassRepository digitalPassRepository;
    private final FareService fareService;
    private final RacWaitlistService racWaitlistService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    private final SecureRandom random = new SecureRandom();

    public BookingService(BookingRepository bookingRepository,
                          BookingPassengerRepository passengerRepository,
                          TrainRepository trainRepository,
                          StationRepository stationRepository,
                          SeatInventoryRepository seatInventoryRepository,
                          CoachSeatRepository coachSeatRepository,
                          TicketRepository ticketRepository,
                          PaymentRepository paymentRepository,
                          RefundRepository refundRepository,
                          DigitalPassRepository digitalPassRepository,
                          FareService fareService,
                          RacWaitlistService racWaitlistService,
                          AuditLogService auditLogService,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.coachSeatRepository = coachSeatRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.digitalPassRepository = digitalPassRepository;
        this.fareService = fareService;
        this.racWaitlistService = racWaitlistService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    /**
     * Section 21 & 22: Safe Transactional Booking with Authoritative Backend Fare Calculation
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest req, String username) {
        Train train = trainRepository.findById(req.getTrainId())
                .orElseThrow(() -> new IllegalArgumentException("Train not found: " + req.getTrainId()));

        if (train.getStatus() == TrainStatus.SUSPENDED || train.getStatus() == TrainStatus.CANCELLED) {
            throw new IllegalStateException("Train " + train.getTrainNumber() + " is currently " + train.getStatus() + ". Bookings are disabled.");
        }

        Station srcStation = stationRepository.findById(req.getSourceStationId())
                .orElseThrow(() -> new IllegalArgumentException("Source station not found: " + req.getSourceStationId()));
        Station dstStation = stationRepository.findById(req.getDestinationStationId())
                .orElseThrow(() -> new IllegalArgumentException("Destination station not found: " + req.getDestinationStationId()));

        CoachClass coachClass = CoachClass.fromCode(req.getCoachClass());
        LocalDate journeyDate = req.getJourneyDate() != null ? req.getJourneyDate() : LocalDate.now().plusDays(1);
        int passengerCount = req.getPassengers().size();

        // 1. Authoritative Backend Fare Recalculation (Never trust client prices)
        FareCalculationRequest fareReq = new FareCalculationRequest();
        fareReq.setTrainId(train.getId());
        fareReq.setSourceStationId(srcStation.getId());
        fareReq.setDestinationStationId(dstStation.getId());
        fareReq.setTravelClass(coachClass.getCode());
        fareReq.setJourneyDate(journeyDate);
        fareReq.setPassengerCount(passengerCount);

        FareCalculationResponse fareResult = fareService.calculateFare(fareReq);
        double totalAuthoritativeFare = fareResult.getTotalFare();

        // 2. Generate 10-Digit PNR
        String pnr = generateUniquePnr();

        // 3. Create Booking Record
        Booking booking = new Booking(
                pnr, train, srcStation, dstStation, journeyDate, coachClass,
                passengerCount, totalAuthoritativeFare, BookingStatus.CONFIRMED
        );
        booking.setContactEmail(req.getContactEmail());
        booking.setContactPhone(req.getContactPhone());
        booking.setBookedByUsername(username != null ? username : "GUEST_" + pnr.substring(5));
        booking.setFareVersion(train.getActiveFareVersion());
        booking = bookingRepository.save(booking);

        // 4. Allocate Seats with Concurrency Protection
        List<BookingResponse.PassengerOutputDto> passengerOutputs = new ArrayList<>();
        boolean hasAnyWaitingOrRac = false;

        for (BookingRequest.PassengerInputDto pInput : req.getPassengers()) {
            BookingPassenger bp = new BookingPassenger(pInput.getName(), pInput.getAge(), pInput.getGender(), pInput.getBerthPreference());
            bp.setBooking(booking);

            // Attempt to obtain an available seat with pessimistic write lock
            List<SeatInventory> availSeats = seatInventoryRepository.findAvailableSeatsForUpdate(
                    train.getId(), journeyDate, coachClass, org.springframework.data.domain.PageRequest.of(0, 1)
            );

            if (!availSeats.isEmpty()) {
                SeatInventory seat = availSeats.get(0);
                seat.setStatus(SeatStatus.BOOKED);
                seatInventoryRepository.save(seat);

                bp.setCoachCode(seat.getCoachSeat().getCoach().getCoachCode());
                bp.setSeatNumber(seat.getCoachSeat().getSeatNumber());
                bp.setBerthType(seat.getCoachSeat().getBerthType());
                bp.setPassengerStatus(BookingStatus.CONFIRMED);
                passengerRepository.save(bp);

                passengerOutputs.add(new BookingResponse.PassengerOutputDto(
                        bp.getPassengerName(), bp.getAge(), bp.getGender(),
                        bp.getCoachCode(), bp.getSeatNumber(),
                        bp.getBerthType().name(), "CONFIRMED"
                ));
            } else {
                // No available seats -> Check RAC Queue (max 20 per train/date/class)
                long racCount = racWaitlistService.enqueueRac(train, journeyDate, coachClass, bp).getPriorityOrder();
                hasAnyWaitingOrRac = true;

                if (racCount <= 20) {
                    BookingResponse.PassengerOutputDto pod = new BookingResponse.PassengerOutputDto(
                            bp.getPassengerName(), bp.getAge(), bp.getGender(),
                            "RAC", null, "RAC", "RAC " + bp.getRacPosition()
                    );
                    pod.setRacPosition(bp.getRacPosition());
                    passengerOutputs.add(pod);
                } else {
                    // RAC full -> Put in Waitlist
                    racWaitlistService.enqueueWaitlist(train, journeyDate, coachClass, bp);
                    BookingResponse.PassengerOutputDto pod = new BookingResponse.PassengerOutputDto(
                            bp.getPassengerName(), bp.getAge(), bp.getGender(),
                            "WL", null, "WAITING", "WL " + bp.getWlPosition()
                    );
                    pod.setWlPosition(bp.getWlPosition());
                    passengerOutputs.add(pod);
                }
            }
        }

        if (hasAnyWaitingOrRac) {
            booking.setBookingStatus(BookingStatus.RAC);
            bookingRepository.save(booking);
        }

        // 5. Payment Simulation Record
        String payRef = "PAY_TXN_" + System.currentTimeMillis() + "_" + random.nextInt(1000);
        Payment payment = new Payment(booking, payRef, totalAuthoritativeFare, req.getPaymentMethod(), PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // 6. Generate QR Code and Digital Travel Pass
        String qrToken = UUID.randomUUID().toString();
        String ticketNumber = "TKT" + pnr;
        Ticket ticket = new Ticket(booking, ticketNumber, qrToken);
        ticketRepository.save(ticket);

        String qrBase64 = generateQrBase64("RAILSETU|PNR:" + pnr + "|TRAIN:" + train.getTrainNumber() + "|DATE:" + journeyDate + "|TOKEN:" + qrToken);
        DigitalPass digitalPass = new DigitalPass(pnr, qrToken, "{\"pnr\":\"" + pnr + "\",\"train\":\"" + train.getTrainNumber() + "\",\"passengers\":" + passengerCount + "}", qrBase64);
        digitalPassRepository.save(digitalPass);

        // 7. Audit Log & Notifications
        auditLogService.logActivity(
                booking.getBookedByUsername(), "CREATE_BOOKING", "BOOKING", booking.getId(),
                "NONE", "PNR " + pnr + " (" + booking.getBookingStatus() + ")",
                "Passenger reservation created. Fare: ₹" + totalAuthoritativeFare
        );

        BookingResponse resp = new BookingResponse();
        resp.setPnrNumber(pnr);
        resp.setBookingId(booking.getId());
        resp.setTrainNumber(train.getTrainNumber());
        resp.setTrainName(train.getTrainName());
        resp.setSourceStation(srcStation.getName() + " (" + srcStation.getCode() + ")");
        resp.setDestinationStation(dstStation.getName() + " (" + dstStation.getCode() + ")");
        resp.setJourneyDate(journeyDate);
        resp.setTravelClass(coachClass.getCode());
        resp.setBookingStatus(booking.getBookingStatus().name());
        resp.setTotalAmount(totalAuthoritativeFare);
        resp.setFareVersion(train.getActiveFareVersion());
        resp.setTicketNumber(ticketNumber);
        resp.setQrToken(qrToken);
        resp.setQrBase64(qrBase64);
        resp.setPassengers(passengerOutputs);

        return resp;
    }

    /**
     * Section 25 & 26: Booking Cancellation with Transactional RAC/WL Promotion
     */
    @Transactional
    public Refund cancelBooking(String pnr, String reason, String adminOrUsername) {
        Booking booking = bookingRepository.findByPnrNumber(pnr)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found for PNR: " + pnr));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED || booking.getBookingStatus() == BookingStatus.REFUNDED) {
            throw new IllegalStateException("Booking " + pnr + " is already cancelled or refunded.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        double totalFare = booking.getTotalFare();
        double cancellationCharge = 60.0 * booking.getTotalPassengers();
        double refundAmt = Math.max(0.0, totalFare - cancellationCharge);

        String refId = "REF_" + System.currentTimeMillis();
        Refund refund = new Refund(booking, refId, refundAmt, cancellationCharge, reason != null ? reason : "Passenger requested cancellation");
        refundRepository.save(refund);

        // For each confirmed passenger, release their physical seat and trigger RAC/WL cascading promotion!
        for (BookingPassenger bp : booking.getPassengers()) {
            if (bp.getPassengerStatus() == BookingStatus.CONFIRMED && bp.getCoachCode() != null && bp.getSeatNumber() != null) {
                // Find physical seat
                Optional<CoachSeat> csOpt = coachSeatRepository.findByCoachTrainId(booking.getTrain().getId()).stream()
                        .filter(cs -> cs.getCoach().getCoachCode().equalsIgnoreCase(bp.getCoachCode()) && cs.getSeatNumber().equals(bp.getSeatNumber()))
                        .findFirst();

                if (csOpt.isPresent()) {
                    Optional<SeatInventory> invOpt = seatInventoryRepository.findByTrainIdAndJourneyDateAndCoachSeatId(
                            booking.getTrain().getId(), booking.getJourneyDate(), csOpt.get().getId()
                    );
                    if (invOpt.isPresent()) {
                        SeatInventory freedSeat = invOpt.get();
                        // Trigger RAC / Waitlist promotion
                        racWaitlistService.processPromotionOnSeatRelease(
                                booking.getTrain(), booking.getJourneyDate(), booking.getCoachClass(), freedSeat
                        );
                    }
                }
            }
            bp.setPassengerStatus(BookingStatus.CANCELLED);
            passengerRepository.save(bp);
        }

        auditLogService.logActivity(
                adminOrUsername, "CANCEL_BOOKING", "BOOKING", booking.getId(),
                "CONFIRMED", "CANCELLED",
                "Booking cancelled. Deducted charge: ₹" + cancellationCharge + ", Refund: ₹" + refundAmt
        );

        return refund;
    }

    @Transactional(readOnly = true)
    public Page<Booking> searchBookings(String search, BookingStatus status, Long trainId, LocalDate date, Pageable pageable) {
        return bookingRepository.searchBookings(search, status, trainId, date, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Booking> getBookingByPnr(String pnr) {
        return bookingRepository.findByPnrNumber(pnr);
    }

    private String generateUniquePnr() {
        while (true) {
            long num = 1000000000L + (long)(random.nextDouble() * 8999999999L);
            String pnr = String.valueOf(num);
            if (!bookingRepository.findByPnrNumber(pnr).isPresent()) {
                return pnr;
            }
        }
    }

    private String generateQrBase64(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 220, 220);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}
