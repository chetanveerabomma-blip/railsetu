package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_approval_requests")
public class FareApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fare_id", nullable = false)
    private Fare fare;

    @Column(nullable = false)
    private Double currentFare;

    @Column(nullable = false)
    private Double proposedFare;

    @Column(length = 255)
    private String reason;

    private Integer affectedFutureBookings = 0;
    private Double projectedRevenueImpact = 0.0;

    private LocalDateTime effectiveDate;

    @Column(nullable = false, length = 80)
    private String proposedBy;

    private LocalDateTime proposedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FareStatus approvalStatus = FareStatus.PENDING_APPROVAL;

    @Column(length = 80)
    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(length = 255)
    private String reviewNotes;

    public FareApprovalRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fare getFare() {
        return fare;
    }

    public void setFare(Fare fare) {
        this.fare = fare;
    }

    public Double getCurrentFare() {
        return currentFare;
    }

    public void setCurrentFare(Double currentFare) {
        this.currentFare = currentFare;
    }

    public Double getProposedFare() {
        return proposedFare;
    }

    public void setProposedFare(Double proposedFare) {
        this.proposedFare = proposedFare;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getAffectedFutureBookings() {
        return affectedFutureBookings;
    }

    public void setAffectedFutureBookings(Integer affectedFutureBookings) {
        this.affectedFutureBookings = affectedFutureBookings;
    }

    public Double getProjectedRevenueImpact() {
        return projectedRevenueImpact;
    }

    public void setProjectedRevenueImpact(Double projectedRevenueImpact) {
        this.projectedRevenueImpact = projectedRevenueImpact;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(String proposedBy) {
        this.proposedBy = proposedBy;
    }

    public LocalDateTime getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(LocalDateTime proposedAt) {
        this.proposedAt = proposedAt;
    }

    public FareStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(FareStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}
