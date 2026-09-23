package com.railsetu.dto;

import java.time.LocalDate;
import java.util.List;

public class SeatHoldRequest {
    private Long trainId;
    private LocalDate journeyDate;
    private List<Long> coachSeatIds;
    private String userIdentifier; // email or session token

    public SeatHoldRequest() {}

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public List<Long> getCoachSeatIds() { return coachSeatIds; }
    public void setCoachSeatIds(List<Long> coachSeatIds) { this.coachSeatIds = coachSeatIds; }
    public String getUserIdentifier() { return userIdentifier; }
    public void setUserIdentifier(String userIdentifier) { this.userIdentifier = userIdentifier; }
}
