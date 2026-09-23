package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, unique = true, length = 60)
    private String refundReference;

    @Column(nullable = false)
    private Double refundAmount;

    @Column(nullable = false)
    private Double cancellationChargeDeducted;

    @Column(length = 30)
    private String refundStatus = "COMPLETED";

    private LocalDateTime processedAt = LocalDateTime.now();

    @Column(length = 255)
    private String reason;

    public Refund() {}

    public Refund(Booking booking, String refundReference, Double refundAmount, Double cancellationChargeDeducted, String reason) {
        this.booking = booking;
        this.refundReference = refundReference;
        this.refundAmount = refundAmount;
        this.cancellationChargeDeducted = cancellationChargeDeducted;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Double getCancellationChargeDeducted() {
        return cancellationChargeDeducted;
    }

    public void setCancellationChargeDeducted(Double cancellationChargeDeducted) {
        this.cancellationChargeDeducted = cancellationChargeDeducted;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
