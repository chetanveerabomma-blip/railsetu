package com.railsetu.controller;

import com.railsetu.domain.*;
import com.railsetu.dto.FareImpactAnalysisRequest;
import com.railsetu.dto.FareImpactAnalysisResponse;
import com.railsetu.repository.*;
import com.railsetu.service.FareRuleEngineService;
import com.railsetu.service.FareService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FARE_ADMIN')")
public class AdminFareController {

    private final FareRepository fareRepository;
    private final FareVersionRepository fareVersionRepository;
    private final FareRuleRepository fareRuleRepository;
    private final FareApprovalRequestRepository approvalRequestRepository;
    private final FareService fareService;
    private final FareRuleEngineService fareRuleEngine;

    public AdminFareController(FareRepository fareRepository,
                               FareVersionRepository fareVersionRepository,
                               FareRuleRepository fareRuleRepository,
                               FareApprovalRequestRepository approvalRequestRepository,
                               FareService fareService,
                               FareRuleEngineService fareRuleEngine) {
        this.fareRepository = fareRepository;
        this.fareVersionRepository = fareVersionRepository;
        this.fareRuleRepository = fareRuleRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.fareService = fareService;
        this.fareRuleEngine = fareRuleEngine;
    }

    /**
     * Section 12 & 13: Dynamic Fare Matrix
     */
    @GetMapping("/fares")
    public ResponseEntity<Page<Fare>> listFares(
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) FareStatus status,
            @RequestParam(required = false) CoachClass coachClass,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(fareRepository.searchFares(trainId, status, coachClass, pageable));
    }

    @GetMapping("/fares/{id}")
    public ResponseEntity<Fare> getFare(@PathVariable Long id) {
        return fareRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Section 14: Fare Version History
     */
    @GetMapping("/fares/{id}/versions")
    public ResponseEntity<List<FareVersion>> getFareVersions(@PathVariable Long id) {
        return ResponseEntity.ok(fareVersionRepository.findByFareIdOrderByVersionNumberDesc(id));
    }

    /**
     * Section 18: Fare Impact Analysis Preview
     */
    @PostMapping("/fares/impact-analysis")
    public ResponseEntity<FareImpactAnalysisResponse> analyzeFareImpact(@RequestBody FareImpactAnalysisRequest req) {
        return ResponseEntity.ok(fareService.calculateFareImpact(req));
    }

    /**
     * Section 19: Fare Change Approval - FARE_ADMIN submits proposed change
     */
    @PostMapping("/fares/propose")
    public ResponseEntity<FareApprovalRequest> proposeFareChange(
            @RequestBody FareImpactAnalysisRequest req, Principal principal) {
        String admin = (principal != null) ? principal.getName() : "ADMIN_FARE";
        return ResponseEntity.ok(fareService.proposeFareChange(req, admin));
    }

    /**
     * Section 19: Fare Change Approval - SUPER_ADMIN reviews and publishes
     */
    @PostMapping("/fares/approvals/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> reviewFareApproval(
            @PathVariable Long id,
            @RequestParam boolean approve,
            @RequestParam(defaultValue = "Tariff revision validated") String remarks,
            Principal principal) {
        String superAdmin = (principal != null) ? principal.getName() : "ADMIN_SUPER";
        fareService.reviewFareApproval(id, approve, remarks, superAdmin);
        return ResponseEntity.ok(Map.of(
                "message", approve ? "Fare approval granted and published to active tariff." : "Fare change proposal rejected."
        ));
    }

    @GetMapping("/fares/approvals/pending")
    public ResponseEntity<List<FareApprovalRequest>> getPendingApprovals() {
        return ResponseEntity.ok(approvalRequestRepository.findByApprovalStatusOrderByProposedAtDesc(FareStatus.PENDING_APPROVAL));
    }

    /**
     * Section 16: Fare Rules Engine
     */
    @GetMapping("/fare-rules")
    public ResponseEntity<List<FareRule>> getFareRules() {
        return ResponseEntity.ok(fareRuleRepository.findByActiveTrueOrderByPriorityAsc());
    }

    /**
     * Section 17: Academic Dynamic Pricing Simulation Toggle
     */
    @GetMapping("/fares/simulation-status")
    public ResponseEntity<Map<String, Object>> getSimulationStatus() {
        return ResponseEntity.ok(Map.of(
                "enabled", fareRuleEngine.isSimulationEnabled(),
                "label", "Academic Dynamic Pricing Simulation"
        ));
    }

    @PutMapping("/fares/simulation-toggle")
    public ResponseEntity<Map<String, Object>> toggleSimulation(
            @RequestParam boolean enabled, Principal principal) {
        String admin = (principal != null) ? principal.getName() : "ADMIN_FARE";
        fareRuleEngine.setSimulationEnabled(enabled, admin);
        return ResponseEntity.ok(Map.of(
                "enabled", enabled,
                "label", "Academic Dynamic Pricing Simulation",
                "message", "Simulation status updated to: " + (enabled ? "ENABLED" : "DISABLED")
        ));
    }
}
