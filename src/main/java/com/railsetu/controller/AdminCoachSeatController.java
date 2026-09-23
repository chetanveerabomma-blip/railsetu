package com.railsetu.controller;

import com.railsetu.domain.*;
import com.railsetu.repository.*;
import com.railsetu.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN')")
public class AdminCoachSeatController {

    private final CoachRepository coachRepository;
    private final CoachSeatRepository coachSeatRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final AuditLogService auditLogService;

    public AdminCoachSeatController(CoachRepository coachRepository,
                                    CoachSeatRepository coachSeatRepository,
                                    SeatInventoryRepository seatInventoryRepository,
                                    AuditLogService auditLogService) {
        this.coachRepository = coachRepository;
        this.coachSeatRepository = coachSeatRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Section 10: Advanced Coach Management
     */
    @GetMapping("/coaches/{trainId}")
    public ResponseEntity<List<Coach>> getCoaches(@PathVariable Long trainId) {
        return ResponseEntity.ok(coachRepository.findByTrainIdOrderByCoachSequenceAsc(trainId));
    }

    @PutMapping("/coaches/{coachId}/status")
    public ResponseEntity<Coach> updateCoachStatus(
            @PathVariable Long coachId,
            @RequestParam CoachStatus status,
            Principal principal) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new IllegalArgumentException("Coach not found: " + coachId));

        CoachStatus old = coach.getStatus();
        coach.setStatus(status);
        coachRepository.save(coach);

        auditLogService.logActivity(
                principal != null ? principal.getName() : "ADMIN", "UPDATE_COACH_STATUS", "COACH",
                coach.getId(), old.name(), status.name(), "Operational coach status change"
        );

        return ResponseEntity.ok(coach);
    }

    /**
     * Section 11: Seat Inventory Engine - Live Seat Visualizer & Maintenance Lock
     */
    @GetMapping("/seats/{trainId}")
    public ResponseEntity<List<SeatInventory>> getSeatInventory(
            @PathVariable Long trainId,
            @RequestParam(required = false) String journeyDate) {

        LocalDate date = (journeyDate != null && !journeyDate.isEmpty())
                ? LocalDate.parse(journeyDate) : LocalDate.now().plusDays(1);

        return ResponseEntity.ok(seatInventoryRepository.findByTrainIdAndJourneyDate(trainId, date));
    }

    @PutMapping("/seats/{inventoryId}/status")
    public ResponseEntity<SeatInventory> updateSeatStatus(
            @PathVariable Long inventoryId,
            @RequestParam SeatStatus status,
            Principal principal) {

        SeatInventory inv = seatInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));

        SeatStatus old = inv.getStatus();
        inv.setStatus(status);
        seatInventoryRepository.save(inv);

        auditLogService.logActivity(
                principal != null ? principal.getName() : "ADMIN", "UPDATE_SEAT_STATUS", "SEAT_INVENTORY",
                inv.getId(), old.name(), status.name(), "Admin manual seat status toggle"
        );

        return ResponseEntity.ok(inv);
    }
}
