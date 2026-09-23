package com.railsetu.service;

import com.railsetu.domain.*;
import com.railsetu.repository.BookingPassengerRepository;
import com.railsetu.repository.RacQueueRepository;
import com.railsetu.repository.SeatInventoryRepository;
import com.railsetu.repository.WaitlistQueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class RacWaitlistService {

    private final RacQueueRepository racQueueRepository;
    private final WaitlistQueueRepository waitlistQueueRepository;
    private final BookingPassengerRepository passengerRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public RacWaitlistService(RacQueueRepository racQueueRepository,
                              WaitlistQueueRepository waitlistQueueRepository,
                              BookingPassengerRepository passengerRepository,
                              SeatInventoryRepository seatInventoryRepository,
                              NotificationService notificationService,
                              AuditLogService auditLogService) {
        this.racQueueRepository = racQueueRepository;
        this.waitlistQueueRepository = waitlistQueueRepository;
        this.passengerRepository = passengerRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public RacQueue enqueueRac(Train train, LocalDate journeyDate, CoachClass coachClass, BookingPassenger passenger) {
        long currentCount = racQueueRepository.countByTrainIdAndJourneyDateAndCoachClassAndActiveTrue(train.getId(), journeyDate, coachClass);
        int nextPos = (int) (currentCount + 1);

        passenger.setPassengerStatus(BookingStatus.RAC);
        passenger.setRacPosition(nextPos);
        passengerRepository.save(passenger);

        RacQueue item = new RacQueue(train, journeyDate, coachClass, passenger, nextPos, nextPos);
        return racQueueRepository.save(item);
    }

    @Transactional
    public WaitlistQueue enqueueWaitlist(Train train, LocalDate journeyDate, CoachClass coachClass, BookingPassenger passenger) {
        long currentCount = waitlistQueueRepository.countByTrainIdAndJourneyDateAndCoachClassAndActiveTrue(train.getId(), journeyDate, coachClass);
        int nextPos = (int) (currentCount + 1);

        passenger.setPassengerStatus(BookingStatus.WAITING);
        passenger.setWlPosition(nextPos);
        passengerRepository.save(passenger);

        WaitlistQueue item = new WaitlistQueue(train, journeyDate, coachClass, passenger, nextPos, nextPos);
        return waitlistQueueRepository.save(item);
    }

    /**
     * Section 26: Live Cascading Promotion Flow
     * Cancellation -> Seat Released -> RAC Promotion -> WL Promotion -> Notification
     * All changes are transactional.
     */
    @Transactional
    public void processPromotionOnSeatRelease(Train train, LocalDate journeyDate, CoachClass coachClass, SeatInventory freedSeat) {
        // Step 1: Check if there is an active RAC passenger for this train/date/class
        Optional<RacQueue> topRacOpt = racQueueRepository.findFirstByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
                train.getId(), journeyDate, coachClass
        );

        if (topRacOpt.isPresent()) {
            RacQueue topRac = topRacOpt.get();
            topRac.setActive(false);
            racQueueRepository.save(topRac);

            BookingPassenger promotedPassenger = topRac.getBookingPassenger();
            promotedPassenger.setPassengerStatus(BookingStatus.CONFIRMED);
            promotedPassenger.setCoachCode(freedSeat.getCoachSeat().getCoach().getCoachCode());
            promotedPassenger.setSeatNumber(freedSeat.getCoachSeat().getSeatNumber());
            promotedPassenger.setBerthType(freedSeat.getCoachSeat().getBerthType());
            promotedPassenger.setRacPosition(null);
            passengerRepository.save(promotedPassenger);

            // Re-assign released seat to this confirmed passenger
            freedSeat.setStatus(SeatStatus.BOOKED);
            freedSeat.setHeldBySessionId(null);
            freedSeat.setHoldExpiresAt(null);
            seatInventoryRepository.save(freedSeat);

            // Audit
            auditLogService.logActivity(
                    "RAC_WL_ENGINE", "PROMOTE_RAC_TO_CONFIRMED", "PASSENGER", promotedPassenger.getId(),
                    "RAC " + topRac.getRacNumber(),
                    "CONFIRMED Coach " + promotedPassenger.getCoachCode() + " Seat " + promotedPassenger.getSeatNumber(),
                    "Promoted automatically due to ticket cancellation"
            );

            // Notify passenger
            notificationService.sendNotification(
                    promotedPassenger.getBooking().getBookedByUsername(),
                    "RAC Ticket Confirmed! 🎉",
                    "Good news! Passenger " + promotedPassenger.getPassengerName() + " (PNR: " + promotedPassenger.getBooking().getPnrNumber() +
                            ") has been promoted to CONFIRMED berth: " + promotedPassenger.getCoachCode() + "-" + promotedPassenger.getSeatNumber() +
                            " (" + promotedPassenger.getBerthType() + ").",
                    "AUDIT_ALERT", "INFO"
            );

            // Step 2: Now that an RAC spot opened up, check if there is an active Waiting List passenger to promote to RAC!
            Optional<WaitlistQueue> topWlOpt = waitlistQueueRepository.findFirstByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
                    train.getId(), journeyDate, coachClass
            );

            if (topWlOpt.isPresent()) {
                WaitlistQueue topWl = topWlOpt.get();
                topWl.setActive(false);
                waitlistQueueRepository.save(topWl);

                BookingPassenger wlPassenger = topWl.getBookingPassenger();
                long activeRacCount = racQueueRepository.countByTrainIdAndJourneyDateAndCoachClassAndActiveTrue(train.getId(), journeyDate, coachClass);
                int newRacPos = (int) (activeRacCount + 1);

                wlPassenger.setPassengerStatus(BookingStatus.RAC);
                wlPassenger.setWlPosition(null);
                wlPassenger.setRacPosition(newRacPos);
                passengerRepository.save(wlPassenger);

                // Add to RAC queue
                RacQueue newRacEntry = new RacQueue(train, journeyDate, coachClass, wlPassenger, newRacPos, newRacPos);
                racQueueRepository.save(newRacEntry);

                // Audit
                auditLogService.logActivity(
                        "RAC_WL_ENGINE", "PROMOTE_WL_TO_RAC", "PASSENGER", wlPassenger.getId(),
                        "WL " + topWl.getWaitlistNumber(), "RAC " + newRacPos,
                        "Promoted automatically from Waiting List to RAC"
                );

                // Notify passenger
                notificationService.sendNotification(
                        wlPassenger.getBooking().getBookedByUsername(),
                        "Waiting List Promoted to RAC",
                        "Passenger " + wlPassenger.getPassengerName() + " (PNR: " + wlPassenger.getBooking().getPnrNumber() +
                                ") was upgraded from Waitlist to RAC " + newRacPos + ".",
                        "AUDIT_ALERT", "INFO"
                );
            }
        } else {
            // No RAC passengers waiting; return seat to available inventory
            freedSeat.setStatus(SeatStatus.AVAILABLE);
            freedSeat.setHeldBySessionId(null);
            freedSeat.setHoldExpiresAt(null);
            seatInventoryRepository.save(freedSeat);
        }
    }
}
