package com.railsetu.controller;

import com.railsetu.domain.Train;
import com.railsetu.domain.TrainStatus;
import com.railsetu.domain.TrainVersion;
import com.railsetu.dto.TrainRemovalImpactResponse;
import com.railsetu.dto.TrainSuspensionReportDto;
import com.railsetu.dto.TrainSuspensionRequest;
import com.railsetu.dto.TrainWizardRequest;
import com.railsetu.repository.TrainRepository;
import com.railsetu.repository.TrainVersionRepository;
import com.railsetu.service.TrainLifecycleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/trains")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATIONS_ADMIN')")
public class AdminTrainController {

    private final TrainRepository trainRepository;
    private final TrainVersionRepository trainVersionRepository;
    private final TrainLifecycleService trainLifecycleService;

    public AdminTrainController(TrainRepository trainRepository,
                                TrainVersionRepository trainVersionRepository,
                                TrainLifecycleService trainLifecycleService) {
        this.trainRepository = trainRepository;
        this.trainVersionRepository = trainVersionRepository;
        this.trainLifecycleService = trainLifecycleService;
    }

    @GetMapping
    public ResponseEntity<Page<Train>> listTrains(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TrainStatus status,
            @RequestParam(required = false) String trainType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "trainNumber") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return ResponseEntity.ok(trainRepository.searchTrains(search, status, trainType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Train> getTrain(@PathVariable Long id) {
        return trainRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Section 4 & 38: 8-Step Train Creation Wizard
     */
    @PostMapping("/wizard")
    public ResponseEntity<Train> publishTrainViaWizard(
            @RequestBody TrainWizardRequest req, Principal principal) {
        String admin = (principal != null) ? principal.getName() : "ADMIN_OPERATIONS";
        return ResponseEntity.ok(trainLifecycleService.createAndPublishTrain(req, admin));
    }

    /**
     * Section 6: Advanced Train Removal - Dependency Check
     */
    @GetMapping("/{id}/removal-check")
    public ResponseEntity<TrainRemovalImpactResponse> checkRemovalDependencies(@PathVariable Long id) {
        return ResponseEntity.ok(trainLifecycleService.checkRemovalDependencies(id));
    }

    /**
     * Section 7: Train Suspension Workflow
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<TrainSuspensionReportDto> suspendTrain(
            @PathVariable Long id,
            @RequestBody TrainSuspensionRequest req,
            Principal principal) {
        String admin = (principal != null) ? principal.getName() : "ADMIN_OPERATIONS";
        return ResponseEntity.ok(trainLifecycleService.suspendTrain(id, req, admin));
    }

    /**
     * Section 5: Train Versioning History
     */
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<TrainVersion>> getTrainVersions(@PathVariable Long id) {
        return ResponseEntity.ok(trainVersionRepository.findByTrainIdOrderByVersionNumberDesc(id));
    }
}
