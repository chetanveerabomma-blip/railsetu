package com.railsetu.dto;

import java.util.List;

public class TrainSuspensionRequest {
    private String reason;
    private boolean autoRefundPassengers = true;
    private Double refundPercentage = 100.0;

    public TrainSuspensionRequest() {}

    public TrainSuspensionRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public boolean isAutoRefundPassengers() { return autoRefundPassengers; }
    public void setAutoRefundPassengers(boolean autoRefundPassengers) { this.autoRefundPassengers = autoRefundPassengers; }
    public Double getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(Double refundPercentage) { this.refundPercentage = refundPercentage; }
}
