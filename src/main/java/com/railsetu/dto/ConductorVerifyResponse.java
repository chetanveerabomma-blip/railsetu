package com.railsetu.dto;

import java.time.LocalDate;
import java.util.List;

public class ConductorVerifyResponse {
    private boolean valid;
    private String pnr;
    private String trainNumber;
    private String trainName;
    private String travelClass;
    private LocalDate journeyDate;
    private String bookingStatus;
    private List<String> passengers;
    private String message;
    private String verificationTime;

    public ConductorVerifyResponse() {}

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getTravelClass() { return travelClass; }
    public void setTravelClass(String travelClass) { this.travelClass = travelClass; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public List<String> getPassengers() { return passengers; }
    public void setPassengers(List<String> passengers) { this.passengers = passengers; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getVerificationTime() { return verificationTime; }
    public void setVerificationTime(String verificationTime) { this.verificationTime = verificationTime; }
}
