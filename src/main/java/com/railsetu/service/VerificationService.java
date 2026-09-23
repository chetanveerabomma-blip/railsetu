package com.railsetu.service;

import com.railsetu.domain.Booking;
import com.railsetu.domain.BookingPassenger;
import com.railsetu.domain.DigitalPass;
import com.railsetu.domain.QrVerification;
import com.railsetu.dto.ConductorVerifyRequest;
import com.railsetu.dto.ConductorVerifyResponse;
import com.railsetu.repository.BookingRepository;
import com.railsetu.repository.DigitalPassRepository;
import com.railsetu.repository.QrVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationService {

    private final BookingRepository bookingRepository;
    private final DigitalPassRepository digitalPassRepository;
    private final QrVerificationRepository verificationRepository;
    private final AuditLogService auditLogService;

    public VerificationService(BookingRepository bookingRepository,
                               DigitalPassRepository digitalPassRepository,
                               QrVerificationRepository verificationRepository,
                               AuditLogService auditLogService) {
        this.bookingRepository = bookingRepository;
        this.digitalPassRepository = digitalPassRepository;
        this.verificationRepository = verificationRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ConductorVerifyResponse verifyTicket(ConductorVerifyRequest req, String verifierUsername) {
        String query = req.getPnrOrToken().trim();
        ConductorVerifyResponse resp = new ConductorVerifyResponse();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        resp.setVerificationTime(LocalDateTime.now().format(dtf));

        Optional<Booking> bookingOpt = bookingRepository.findByPnrNumber(query);
        if (!bookingOpt.isPresent()) {
            // Check if it was a QR token
            Optional<DigitalPass> passOpt = digitalPassRepository.findByQrToken(query);
            if (passOpt.isPresent()) {
                bookingOpt = bookingRepository.findByPnrNumber(passOpt.get().getPnr());
            }
        }

        if (bookingOpt.isPresent()) {
            Booking b = bookingOpt.get();
            resp.setPnr(b.getPnrNumber());
            resp.setTrainNumber(b.getTrain().getTrainNumber());
            resp.setTrainName(b.getTrain().getTrainName());
            resp.setTravelClass(b.getCoachClass().getCode());
            resp.setJourneyDate(b.getJourneyDate());
            resp.setBookingStatus(b.getBookingStatus().name());

            List<String> passNames = new ArrayList<>();
            for (BookingPassenger bp : b.getPassengers()) {
                passNames.add(bp.getPassengerName() + " (" + bp.getPassengerStatus() + " - Coach: " +
                        (bp.getCoachCode() != null ? bp.getCoachCode() : "N/A") + ", Seat: " +
                        (bp.getSeatNumber() != null ? bp.getSeatNumber() : "N/A") + ")");
            }
            resp.setPassengers(passNames);

            if (b.getBookingStatus() == com.railsetu.domain.BookingStatus.CANCELLED) {
                resp.setValid(false);
                resp.setMessage("TICKET CANCELLED. Travel pass invalid!");
            } else {
                resp.setValid(true);
                resp.setMessage("AUTHENTIC TICKET VERIFIED. Passenger cleared for travel.");
            }

            // Record verification audit
            QrVerification qv = new QrVerification(
                    b.getPnrNumber(), verifierUsername != null ? verifierUsername : "CONDUCTOR_MAS",
                    req.getStationCode() != null ? req.getStationCode() : "MAS",
                    resp.isValid() ? "VALID" : "INVALID", req.getRemarks()
            );
            verificationRepository.save(qv);

            auditLogService.logActivity(
                    verifierUsername, "VERIFY_TICKET", "BOOKING", b.getId(),
                    "UNVERIFIED", resp.isValid() ? "VERIFIED_VALID" : "VERIFIED_INVALID",
                    "Conductor checked QR/PNR at station " + req.getStationCode()
            );

        } else {
            resp.setValid(false);
            resp.setMessage("INVALID QR TOKEN OR PNR. No matching booking found in RailSetu operations database.");
        }

        return resp;
    }
}
