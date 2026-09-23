package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_versions")
public class FareVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fareId;

    private Long trainId;
    private String trainNumber;

    @Column(length = 20)
    private String coachClass;

    @Column(nullable = false)
    private Integer versionNumber;

    private Double previousFare;
    private Double newFare;

    private Double baseFare;
    private Double reservationCharge;
    private Double serviceCharge;

    private LocalDateTime effectiveDate;

    @Column(length = 80)
    private String createdBy;

    @Column(length = 80)
    private String approvedBy;

    @Column(length = 255)
    private String changeReason;

    private LocalDateTime createdAt = LocalDateTime.now();

    public FareVersion() {}

    public FareVersion(Long fareId, Long trainId, String trainNumber, String coachClass, Integer versionNumber, Double previousFare, Double newFare, LocalDateTime effectiveDate, String createdBy, String approvedBy, String changeReason) {
        this.fareId = fareId;
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.coachClass = coachClass;
        this.versionNumber = versionNumber;
        this.previousFare = previousFare;
        this.newFare = newFare;
        this.effectiveDate = effectiveDate;
        this.createdBy = createdBy;
        this.approvedBy = approvedBy;
        this.changeReason = changeReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFareId() {
        return fareId;
    }

    public void setFareId(Long fareId) {
        this.fareId = fareId;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(String coachClass) {
        this.coachClass = coachClass;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Double getPreviousFare() {
        return previousFare;
    }

    public void setPreviousFare(Double previousFare) {
        this.previousFare = previousFare;
    }

    public Double getNewFare() {
        return newFare;
    }

    public void setNewFare(Double newFare) {
        this.newFare = newFare;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getReservationCharge() {
        return reservationCharge;
    }

    public void setReservationCharge(Double reservationCharge) {
        this.reservationCharge = reservationCharge;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
