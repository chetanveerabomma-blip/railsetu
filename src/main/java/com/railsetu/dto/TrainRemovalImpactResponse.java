package com.railsetu.dto;

public class TrainRemovalImpactResponse {
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private long futureBookings;
    private long confirmedPassengers;
    private long racPassengers;
    private long waitingListPassengers;
    private long pendingRefunds;
    private boolean activeSchedules;
    private boolean deletionAllowed;
    private String recommendedAction;
    private String policyWarning;

    public TrainRemovalImpactResponse() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public long getFutureBookings() { return futureBookings; }
    public void setFutureBookings(long futureBookings) { this.futureBookings = futureBookings; }
    public long getConfirmedPassengers() { return confirmedPassengers; }
    public void setConfirmedPassengers(long confirmedPassengers) { this.confirmedPassengers = confirmedPassengers; }
    public long getRacPassengers() { return racPassengers; }
    public void setRacPassengers(long racPassengers) { this.racPassengers = racPassengers; }
    public long getWaitingListPassengers() { return waitingListPassengers; }
    public void setWaitingListPassengers(long waitingListPassengers) { this.waitingListPassengers = waitingListPassengers; }
    public long getPendingRefunds() { return pendingRefunds; }
    public void setPendingRefunds(long pendingRefunds) { this.pendingRefunds = pendingRefunds; }
    public boolean isActiveSchedules() { return activeSchedules; }
    public void setActiveSchedules(boolean activeSchedules) { this.activeSchedules = activeSchedules; }
    public boolean isDeletionAllowed() { return deletionAllowed; }
    public void setDeletionAllowed(boolean deletionAllowed) { this.deletionAllowed = deletionAllowed; }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    public String getPolicyWarning() { return policyWarning; }
    public void setPolicyWarning(String policyWarning) { this.policyWarning = policyWarning; }
}
