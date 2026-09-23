package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "digital_passes")
public class DigitalPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String pnr;

    @Column(nullable = false, unique = true, length = 64)
    private String qrToken;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String passengerDetailsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String qrBase64;

    private boolean verified = false;
    private LocalDateTime verifiedAt;

    @Column(length = 80)
    private String verifiedByVerifier;

    @Column(length = 20)
    private String stationCode;

    private LocalDateTime issuedAt = LocalDateTime.now();

    public DigitalPass() {}

    public DigitalPass(String pnr, String qrToken, String passengerDetailsJson, String qrBase64) {
        this.pnr = pnr;
        this.qrToken = qrToken;
        this.passengerDetailsJson = passengerDetailsJson;
        this.qrBase64 = qrBase64;
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

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getPassengerDetailsJson() {
        return passengerDetailsJson;
    }

    public void setPassengerDetailsJson(String passengerDetailsJson) {
        this.passengerDetailsJson = passengerDetailsJson;
    }

    public String getQrBase64() {
        return qrBase64;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getVerifiedByVerifier() {
        return verifiedByVerifier;
    }

    public void setVerifiedByVerifier(String verifiedByVerifier) {
        this.verifiedByVerifier = verifiedByVerifier;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}
