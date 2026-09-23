package com.railsetu.dto;

import java.time.LocalDate;

public class FareCalculationRequest {
    private Long trainId;
    private Long sourceStationId;
    private Long destinationStationId;
    private String travelClass; // 1A, 2A, 3A, SL, CC, 2S
    private LocalDate journeyDate;
    private Integer passengerCount = 1;

    public FareCalculationRequest() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public Long getSourceStationId() { return sourceStationId; }
    public void setSourceStationId(Long sourceStationId) { this.sourceStationId = sourceStationId; }
    public Long getDestinationStationId() { return destinationStationId; }
    public void setDestinationStationId(Long destinationStationId) { this.destinationStationId = destinationStationId; }
    public String getTravelClass() { return travelClass; }
    public void setTravelClass(String travelClass) { this.travelClass = travelClass; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
}
