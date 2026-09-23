package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fares", indexes = {
        @Index(name = "idx_fare_lookup", columnList = "train_id, source_station_id, destination_station_id, coach_class, status")
})
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Enumerated(EnumType.STRING)
    @Column(name = "coach_class", nullable = false, length = 20)
    private CoachClass coachClass;

    @Column(nullable = false)
    private Double baseFare = 0.0;

    private Double reservationCharge = 40.0;
    private Double serviceCharge = 20.0;
    private Double dynamicSurcharge = 0.0;
    private Double cancellationCharge = 60.0;

    @Column(nullable = false)
    private Double totalFare = 0.0;

    @Column(nullable = false)
    private Integer fareVersion = 1;

    private LocalDateTime effectiveDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FareStatus status = FareStatus.ACTIVE;

    @Column(length = 80)
    private String createdBy;

    @Column(length = 80)
    private String approvedBy;

    @Column(length = 255)
    private String approvalRemarks;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Fare() {}

    public Fare(Train train, Station sourceStation, Station destinationStation, CoachClass coachClass, Double baseFare, Double reservationCharge, Double serviceCharge, Double totalFare) {
        this.train = train;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.coachClass = coachClass;
        this.baseFare = baseFare;
        this.reservationCharge = reservationCharge;
        this.serviceCharge = serviceCharge;
        this.totalFare = totalFare;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(Station sourceStation) {
        this.sourceStation = sourceStation;
    }

    public Station getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(Station destinationStation) {
        this.destinationStation = destinationStation;
    }

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
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

    public Double getDynamicSurcharge() {
        return dynamicSurcharge;
    }

    public void setDynamicSurcharge(Double dynamicSurcharge) {
        this.dynamicSurcharge = dynamicSurcharge;
    }

    public Double getCancellationCharge() {
        return cancellationCharge;
    }

    public void setCancellationCharge(Double cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public Integer getFareVersion() {
        return fareVersion;
    }

    public void setFareVersion(Integer fareVersion) {
        this.fareVersion = fareVersion;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public FareStatus getStatus() {
        return status;
    }

    public void setStatus(FareStatus status) {
        this.status = status;
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

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
