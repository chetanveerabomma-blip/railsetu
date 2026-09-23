package com.railsetu.dto;

public class FareImpactAnalysisRequest {
    private Long fareId;
    private Double proposedBaseFare;
    private Double proposedTotalFare;
    private String reason;

    public FareImpactAnalysisRequest() {}

    public Long getFareId() { return fareId; }
    public void setFareId(Long fareId) { this.fareId = fareId; }
    public Double getProposedBaseFare() { return proposedBaseFare; }
    public void setProposedBaseFare(Double proposedBaseFare) { this.proposedBaseFare = proposedBaseFare; }
    public Double getProposedTotalFare() { return proposedTotalFare; }
    public void setProposedTotalFare(Double proposedTotalFare) { this.proposedTotalFare = proposedTotalFare; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
