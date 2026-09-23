package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_verifications")
public class QrVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String pnr;

    @Column(nullable = false, length = 80)
    private String verifierUsername;

    @Column(length = 20)
    private String stationCode;

    private LocalDateTime verifiedAt = LocalDateTime.now();

    @Column(nullable = false, length = 30)
    private String verificationResult; // VALID, INVALID, EXPIRED, CANCELLED

    @Column(length = 255)
    private String remarks;

    public QrVerification() {}

    public QrVerification(String pnr, String verifierUsername, String stationCode, String verificationResult, String remarks) {
        this.pnr = pnr;
        this.verifierUsername = verifierUsername;
        this.stationCode = stationCode;
        this.verificationResult = verificationResult;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getVerifierUsername() {
        return verifierUsername;
    }

    public void setVerifierUsername(String verifierUsername) {
        this.verifierUsername = verifierUsername;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerificationResult() {
        return verificationResult;
    }

    public void setVerificationResult(String verificationResult) {
        this.verificationResult = verificationResult;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
