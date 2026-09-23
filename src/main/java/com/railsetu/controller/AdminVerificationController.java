package com.railsetu.controller;

import com.railsetu.domain.QrVerification;
import com.railsetu.dto.ConductorVerifyRequest;
import com.railsetu.dto.ConductorVerifyResponse;
import com.railsetu.repository.QrVerificationRepository;
import com.railsetu.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/verification")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'VERIFIER')")
public class AdminVerificationController {

    private final VerificationService verificationService;
    private final QrVerificationRepository verificationRepository;

    public AdminVerificationController(VerificationService verificationService,
                                       QrVerificationRepository verificationRepository) {
        this.verificationService = verificationService;
        this.verificationRepository = verificationRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<ConductorVerifyResponse> verifyTicket(
            @RequestBody ConductorVerifyRequest req, Principal principal) {
        String verifier = (principal != null) ? principal.getName() : "CONDUCTOR_OFFICIAL";
        return ResponseEntity.ok(verificationService.verifyTicket(req, verifier));
    }

    @GetMapping("/history")
    public ResponseEntity<List<QrVerification>> getVerificationHistory() {
        return ResponseEntity.ok(verificationRepository.findTop20ByOrderByVerifiedAtDesc());
    }
}
