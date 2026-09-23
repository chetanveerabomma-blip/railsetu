package com.railsetu.controller;

import com.railsetu.domain.*;
import com.railsetu.repository.*;
import com.railsetu.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN')")
public class AdminRouteScheduleController {

    private final RouteStopRepository routeStopRepository;
    private final RouteVersionRepository routeVersionRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final AuditLogService auditLogService;

    public AdminRouteScheduleController(RouteStopRepository routeStopRepository,
                                        RouteVersionRepository routeVersionRepository,
                                        TrainScheduleRepository scheduleRepository,
                                        ScheduleVersionRepository scheduleVersionRepository,
                                        TrainRepository trainRepository,
                                        StationRepository stationRepository,
                                        AuditLogService auditLogService) {
        this.routeStopRepository = routeStopRepository;
        this.routeVersionRepository = routeVersionRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleVersionRepository = scheduleVersionRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Section 8: Interactive Route Editor - Get Route Stops
     */
    @GetMapping("/routes/{trainId}")
    public ResponseEntity<List<RouteStop>> getTrainRoute(@PathVariable Long trainId) {
        return ResponseEntity.ok(routeStopRepository.findByTrainIdOrderByStopSequenceAsc(trainId));
    }

    /**
     * Section 8: Interactive Route Editor - Save & Validate Route with Versioning
     */
    @PutMapping("/routes/{trainId}")
    @Transactional
    public ResponseEntity<?> updateTrainRoute(
            @PathVariable Long trainId,
            @RequestBody List<RouteStop> stops,
            @RequestParam(defaultValue = "Route alignment modification") String changeReason,
            Principal principal) {

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new IllegalArgumentException("Train not found: " + trainId));

        if (stops.size() < 2) {
            return ResponseEntity.badRequest().body("Route must contain at least 2 stations (Origin and Destination).");
        }

        // Validate sequence uniqueness & chronological distance order
        for (int i = 0; i < stops.size() - 1; i++) {
            RouteStop s1 = stops.get(i);
            RouteStop s2 = stops.get(i + 1);

            if (s1.getStation().getId().equals(s2.getStation().getId())) {
                return ResponseEntity.badRequest().body("Duplicate consecutive station found: " + s1.getStation().getCode());
            }
            if (s1.getDistanceKm() >= s2.getDistanceKm()) {
                return ResponseEntity.badRequest().body("Station distance must strictly increase chronologically along route.");
            }
        }

        // Archive previous route version
        List<RouteStop> oldStops = routeStopRepository.findByTrainIdOrderByStopSequenceAsc(trainId);
        long versionCount = routeVersionRepository.findByTrainIdOrderByVersionNumberDesc(trainId).size() + 1;
        RouteVersion rv = new RouteVersion(trainId, (int) versionCount, "{\"stopsCount\":" + oldStops.size() + "}",
                principal != null ? principal.getName() : "ADMIN", changeReason);
        routeVersionRepository.save(rv);

        // Delete old and save new stops
        routeStopRepository.deleteByTrainId(trainId);
        for (int i = 0; i < stops.size(); i++) {
            RouteStop s = stops.get(i);
            s.setTrain(train);
            s.setStopSequence(i + 1);
            Station st = stationRepository.findById(s.getStation().getId()).orElseThrow();
            s.setStation(st);
            routeStopRepository.save(s);
        }

        auditLogService.logActivity(
                principal != null ? principal.getName() : "ADMIN", "UPDATE_ROUTE", "ROUTE",
                trainId, "Version " + (versionCount - 1), "Version " + versionCount, changeReason
        );

        return ResponseEntity.ok(routeStopRepository.findByTrainIdOrderByStopSequenceAsc(trainId));
    }

    /**
     * Section 9: Schedule Management
     */
    @GetMapping("/schedules/{trainId}")
    public ResponseEntity<List<TrainSchedule>> getSchedules(@PathVariable Long trainId) {
        return ResponseEntity.ok(scheduleRepository.findByTrainId(trainId));
    }

    @PutMapping("/schedules/{trainId}")
    @Transactional
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long trainId,
            @RequestBody TrainSchedule newSchedule,
            Principal principal) {

        Train train = trainRepository.findById(trainId).orElseThrow();
        newSchedule.setTrain(train);
        newSchedule.setUpdatedAt(LocalDateTime.now());
        TrainSchedule saved = scheduleRepository.save(newSchedule);

        auditLogService.logActivity(
                principal != null ? principal.getName() : "ADMIN", "UPDATE_SCHEDULE", "SCHEDULE",
                saved.getId(), "OLD_SCHEDULE", saved.getScheduleType().name() + " (" + saved.getDepartureTime() + ")",
                "Operational schedule update"
        );

        return ResponseEntity.ok(saved);
    }
}
