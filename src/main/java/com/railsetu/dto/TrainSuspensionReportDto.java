package com.railsetu.dto;

public class TrainSuspensionReportDto {
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String suspensionReason;
    private long affectedBookingsCount;
    private long affectedPassengersCount;
    private Double totalRefundIssued;
    private String auditEventId;
    private String message;

    public TrainSuspensionReportDto() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }
    public long getAffectedBookingsCount() { return affectedBookingsCount; }
    public void setAffectedBookingsCount(long affectedBookingsCount) { this.affectedBookingsCount = affectedBookingsCount; }
    public long getAffectedPassengersCount() { return affectedPassengersCount; }
    public void setAffectedPassengersCount(long affectedPassengersCount) { this.affectedPassengersCount = affectedPassengersCount; }
    public Double getTotalRefundIssued() { return totalRefundIssued; }
    public void setTotalRefundIssued(Double totalRefundIssued) { this.totalRefundIssued = totalRefundIssued; }
    public String getAuditEventId() { return auditEventId; }
    public void setAuditEventId(String auditEventId) { this.auditEventId = auditEventId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
