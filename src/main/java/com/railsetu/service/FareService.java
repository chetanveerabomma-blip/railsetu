package com.railsetu.service;

import com.railsetu.domain.*;
import com.railsetu.dto.*;
import com.railsetu.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FareService {

    private final FareRepository fareRepository;
    private final FareVersionRepository fareVersionRepository;
    private final FareApprovalRequestRepository approvalRequestRepository;
    private final TrainRepository trainRepository;
    private final RouteStopRepository routeStopRepository;
    private final BookingRepository bookingRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final FareRuleEngineService fareRuleEngine;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public FareService(FareRepository fareRepository,
                       FareVersionRepository fareVersionRepository,
                       FareApprovalRequestRepository approvalRequestRepository,
                       TrainRepository trainRepository,
                       RouteStopRepository routeStopRepository,
                       BookingRepository bookingRepository,
                       SeatInventoryRepository seatInventoryRepository,
                       FareRuleEngineService fareRuleEngine,
                       AuditLogService auditLogService,
                       NotificationService notificationService) {
        this.fareRepository = fareRepository;
        this.fareVersionRepository = fareVersionRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.trainRepository = trainRepository;
        this.routeStopRepository = routeStopRepository;
        this.bookingRepository = bookingRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.fareRuleEngine = fareRuleEngine;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public FareCalculationResponse calculateFare(FareCalculationRequest req) {
        Train train = trainRepository.findById(req.getTrainId())
                .orElseThrow(() -> new IllegalArgumentException("Train not found: " + req.getTrainId()));

        CoachClass coachClass = CoachClass.fromCode(req.getTravelClass());

        // Calculate distance between stops
        int distance = 495; // default fallback MAS -> TPJ
        if (req.getSourceStationId() != null && req.getDestinationStationId() != null) {
            var srcStop = routeStopRepository.findByTrainIdAndStationId(train.getId(), req.getSourceStationId());
            var dstStop = routeStopRepository.findByTrainIdAndStationId(train.getId(), req.getDestinationStationId());
            if (srcStop.isPresent() && dstStop.isPresent()) {
                distance = Math.abs(dstStop.get().getDistanceKm() - srcStop.get().getDistanceKm());
            }
        }

        // Calculate current occupancy %
        LocalDate journeyDate = req.getJourneyDate() != null ? req.getJourneyDate() : LocalDate.now().plusDays(1);
        long totalSeats = seatInventoryRepository.countByTrainIdAndJourneyDate(train.getId(), journeyDate);
        long bookedSeats = seatInventoryRepository.countByTrainIdAndJourneyDateAndStatus(train.getId(), journeyDate, SeatStatus.BOOKED);
        double occupancy = (totalSeats > 0) ? ((double) bookedSeats / totalSeats * 100.0) : 45.0;

        FareRuleEngineService.FareCalculationResult calc = fareRuleEngine.evaluateFare(
                train.getTrainType(), coachClass, distance, journeyDate, occupancy, req.getPassengerCount()
        );

        FareCalculationResponse resp = new FareCalculationResponse();
        resp.setBaseFare(calc.baseFare);
        resp.setReservationCharge(calc.reservationCharge);
        resp.setServiceCharge(calc.serviceCharge);
        resp.setDynamicComponent(calc.dynamicComponent);
        resp.setDiscount(calc.discount);
        resp.setTotalFare(calc.finalFare);
        resp.setFareVersion(train.getActiveFareVersion());
        resp.setOccupancyPercent(Math.round(occupancy * 10.0) / 10.0);
        resp.setSimulationNote(calc.simulationNote);

        return resp;
    }

    @Transactional(readOnly = true)
    public FareImpactAnalysisResponse calculateFareImpact(FareImpactAnalysisRequest req) {
        Fare fare = fareRepository.findById(req.getFareId())
                .orElseThrow(() -> new IllegalArgumentException("Fare not found: " + req.getFareId()));

        double currentFare = fare.getTotalFare();
        double proposedFare = (req.getProposedTotalFare() != null) ? req.getProposedTotalFare() : (req.getProposedBaseFare() + fare.getReservationCharge() + fare.getServiceCharge());
        double diff = proposedFare - currentFare;

        // Count future bookings for this train & class
        long futureBookings = bookingRepository.countByTrainIdAndJourneyDateGreaterThanEqualAndBookingStatusIn(
                fare.getTrain().getId(), LocalDate.now(), List.of(BookingStatus.CONFIRMED, BookingStatus.RAC)
        );

        double projectedRev = diff * futureBookings;

        FareImpactAnalysisResponse resp = new FareImpactAnalysisResponse();
        resp.setFareId(fare.getId());
        resp.setTrainNumber(fare.getTrain().getTrainNumber());
        resp.setTrainName(fare.getTrain().getTrainName());
        resp.setCoachClass(fare.getCoachClass().getCode());
        resp.setCurrentFare(currentFare);
        resp.setProposedFare(proposedFare);
        resp.setDifference(diff);
        resp.setAffectedFutureBookings(futureBookings);
        resp.setProjectedRevenueImpact(Math.round(projectedRev * 100.0) / 100.0);
        resp.setRequiresSuperAdminApproval(true);

        return resp;
    }

    @Transactional
    public FareApprovalRequest proposeFareChange(FareImpactAnalysisRequest req, String fareAdminUsername) {
        Fare fare = fareRepository.findById(req.getFareId())
                .orElseThrow(() -> new IllegalArgumentException("Fare not found: " + req.getFareId()));

        FareImpactAnalysisResponse impact = calculateFareImpact(req);

        FareApprovalRequest approval = new FareApprovalRequest();
        approval.setFare(fare);
        approval.setCurrentFare(impact.getCurrentFare());
        approval.setProposedFare(impact.getProposedFare());
        approval.setReason(req.getReason() != null ? req.getReason() : "Regular operational tariff revision");
        approval.setAffectedFutureBookings((int) impact.getAffectedFutureBookings());
        approval.setProjectedRevenueImpact(impact.getProjectedRevenueImpact());
        approval.setEffectiveDate(LocalDateTime.now().plusDays(1));
        approval.setProposedBy(fareAdminUsername);
        approval.setApprovalStatus(FareStatus.PENDING_APPROVAL);

        fare.setStatus(FareStatus.PENDING_APPROVAL);
        fareRepository.save(fare);

        approval = approvalRequestRepository.save(approval);

        notificationService.notifyAdmins(
                "New Fare Change Proposal Pending Approval",
                "FARE_ADMIN " + fareAdminUsername + " proposed fare revision for Train " + fare.getTrain().getTrainNumber() + " (" + fare.getCoachClass() + ") to ₹" + impact.getProposedFare(),
                "SCHEDULED_FARE_PENDING",
                "WARNING"
        );

        auditLogService.logActivity(
                fareAdminUsername, "PROPOSE_FARE_REVISION", "FARE", fare.getId(),
                "₹" + impact.getCurrentFare(), "₹" + impact.getProposedFare(),
                "Submitted for Super Admin approval"
        );

        return approval;
    }

    @Transactional
    public void reviewFareApproval(Long approvalId, boolean approve, String remarks, String superAdminUsername) {
        FareApprovalRequest req = approvalRequestRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found: " + approvalId));

        Fare fare = req.getFare();
        double oldFare = fare.getTotalFare();

        if (approve) {
            req.setApprovalStatus(FareStatus.APPROVED);
            req.setReviewedBy(superAdminUsername);
            req.setReviewedAt(LocalDateTime.now());
            req.setReviewNotes(remarks);

            // Increment Fare Version
            int newVersion = fare.getFareVersion() + 1;
            fare.setFareVersion(newVersion);
            fare.setTotalFare(req.getProposedFare());
            fare.setBaseFare(req.getProposedFare() - fare.getReservationCharge() - fare.getServiceCharge());
            fare.setStatus(FareStatus.ACTIVE);
            fare.setApprovedBy(superAdminUsername);
            fare.setApprovalRemarks(remarks);
            fare.setEffectiveDate(LocalDateTime.now());
            fare.setUpdatedAt(LocalDateTime.now());
            fareRepository.save(fare);

            // Update Train active fare version
            fare.getTrain().setActiveFareVersion(newVersion);
            trainRepository.save(fare.getTrain());

            // Save immutable FareVersion
            FareVersion fv = new FareVersion(
                    fare.getId(), fare.getTrain().getId(), fare.getTrain().getTrainNumber(),
                    fare.getCoachClass().getCode(), newVersion, oldFare, req.getProposedFare(),
                    LocalDateTime.now(), req.getProposedBy(), superAdminUsername, req.getReason()
            );
            fareVersionRepository.save(fv);

            auditLogService.logActivity(
                    superAdminUsername, "APPROVE_AND_PUBLISH_FARE", "FARE", fare.getId(),
                    "₹" + oldFare, "₹" + req.getProposedFare(),
                    "Approved by Super Admin. Version updated to v" + newVersion + ". " + remarks
            );

            notificationService.sendNotification(
                    req.getProposedBy(),
                    "Fare Revision Approved & Published",
                    "Your proposed fare change for Train " + fare.getTrain().getTrainNumber() + " was APPROVED by " + superAdminUsername + ". Version is now v" + newVersion,
                    "AUDIT_ALERT", "INFO"
            );
        } else {
            req.setApprovalStatus(FareStatus.REJECTED);
            req.setReviewedBy(superAdminUsername);
            req.setReviewedAt(LocalDateTime.now());
            req.setReviewNotes(remarks);

            fare.setStatus(FareStatus.ACTIVE); // revert to previous active
            fareRepository.save(fare);

            auditLogService.logActivity(
                    superAdminUsername, "REJECT_FARE_REVISION", "FARE", fare.getId(),
                    "Proposed: ₹" + req.getProposedFare(), "Retained: ₹" + oldFare,
                    "Rejected by Super Admin: " + remarks
            );
        }

        approvalRequestRepository.save(req);
    }

    /**
     * Section 15: Scheduled Fare Changes (Spring @Scheduled background job)
     * Automatically activates future approved fares when effective time is reached.
     */
    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void activateScheduledApprovedFares() {
        List<Fare> dueFares = fareRepository.findApprovedFaresReadyToActivate(LocalDateTime.now());
        for (Fare f : dueFares) {
            f.setStatus(FareStatus.ACTIVE);
            fareRepository.save(f);

            auditLogService.logActivity(
                    "SYSTEM_SCHEDULER", "AUTO_ACTIVATE_SCHEDULED_FARE", "FARE", f.getId(),
                    "PENDING_EFFECTIVE", "ACTIVE",
                    "Automated scheduled activation at " + LocalDateTime.now()
            );

            notificationService.notifyAdmins(
                    "Scheduled Fare Auto-Activated",
                    "Fare for Train " + f.getTrain().getTrainNumber() + " (" + f.getCoachClass() + ") at ₹" + f.getTotalFare() + " has reached effective date and is now ACTIVE.",
                    "FARE_EXPIRY", "INFO"
            );
        }
    }
}
