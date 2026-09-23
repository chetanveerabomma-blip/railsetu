package com.railsetu.dto;

public class FareImpactAnalysisResponse {
    private Long fareId;
    private String trainNumber;
    private String trainName;
    private String coachClass;
    private Double currentFare;
    private Double proposedFare;
    private Double difference;
    private long affectedFutureBookings;
    private String existingTicketsPolicy = "NOT CHANGED (Original fare preserved)";
    private Double projectedRevenueImpact;
    private boolean requiresSuperAdminApproval = true;

    public FareImpactAnalysisResponse() {}

    public Long getFareId() { return fareId; }
    public void setFareId(Long fareId) { this.fareId = fareId; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getCoachClass() { return coachClass; }
    public void setCoachClass(String coachClass) { this.coachClass = coachClass; }
    public Double getCurrentFare() { return currentFare; }
    public void setCurrentFare(Double currentFare) { this.currentFare = currentFare; }
    public Double getProposedFare() { return proposedFare; }
    public void setProposedFare(Double proposedFare) { this.proposedFare = proposedFare; }
    public Double getDifference() { return difference; }
    public void setDifference(Double difference) { this.difference = difference; }
    public long getAffectedFutureBookings() { return affectedFutureBookings; }
    public void setAffectedFutureBookings(long affectedFutureBookings) { this.affectedFutureBookings = affectedFutureBookings; }
    public String getExistingTicketsPolicy() { return existingTicketsPolicy; }
    public void setExistingTicketsPolicy(String existingTicketsPolicy) { this.existingTicketsPolicy = existingTicketsPolicy; }
    public Double getProjectedRevenueImpact() { return projectedRevenueImpact; }
    public void setProjectedRevenueImpact(Double projectedRevenueImpact) { this.projectedRevenueImpact = projectedRevenueImpact; }
    public boolean isRequiresSuperAdminApproval() { return requiresSuperAdminApproval; }
    public void setRequiresSuperAdminApproval(boolean requiresSuperAdminApproval) { this.requiresSuperAdminApproval = requiresSuperAdminApproval; }
}
