package com.railsetu.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingResponse {
    private String pnrNumber;
    private Long bookingId;
    private String trainNumber;
    private String trainName;
    private String sourceStation;
    private String destinationStation;
    private LocalDate journeyDate;
    private String travelClass;
    private String bookingStatus;
    private Double totalAmount;
    private Integer fareVersion;
    private String ticketNumber;
    private String qrToken;
    private String qrBase64;
    private List<PassengerOutputDto> passengers = new ArrayList<>();

    public BookingResponse() {}

    public static class PassengerOutputDto {
        private String name;
        private Integer age;
        private String gender;
        private String coachCode;
        private Integer seatNumber;
        private String berthType;
        private String status;
        private Integer racPosition;
        private Integer wlPosition;

        public PassengerOutputDto() {}

        public PassengerOutputDto(String name, Integer age, String gender, String coachCode, Integer seatNumber, String berthType, String status) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.coachCode = coachCode;
            this.seatNumber = seatNumber;
            this.berthType = berthType;
            this.status = status;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getCoachCode() { return coachCode; }
        public void setCoachCode(String coachCode) { this.coachCode = coachCode; }
        public Integer getSeatNumber() { return seatNumber; }
        public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
        public String getBerthType() { return berthType; }
        public void setBerthType(String berthType) { this.berthType = berthType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getRacPosition() { return racPosition; }
        public void setRacPosition(Integer racPosition) { this.racPosition = racPosition; }
        public Integer getWlPosition() { return wlPosition; }
        public void setWlPosition(Integer wlPosition) { this.wlPosition = wlPosition; }
    }

    public String getPnrNumber() { return pnrNumber; }
    public void setPnrNumber(String pnrNumber) { this.pnrNumber = pnrNumber; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getSourceStation() { return sourceStation; }
    public void setSourceStation(String sourceStation) { this.sourceStation = sourceStation; }
    public String getDestinationStation() { return destinationStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public String getTravelClass() { return travelClass; }
    public void setTravelClass(String travelClass) { this.travelClass = travelClass; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public Integer getFareVersion() { return fareVersion; }
    public void setFareVersion(Integer fareVersion) { this.fareVersion = fareVersion; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }
    public String getQrBase64() { return qrBase64; }
    public void setQrBase64(String qrBase64) { this.qrBase64 = qrBase64; }
    public List<PassengerOutputDto> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerOutputDto> passengers) { this.passengers = passengers; }
}
