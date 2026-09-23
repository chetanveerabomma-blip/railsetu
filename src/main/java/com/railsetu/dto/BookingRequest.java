package com.railsetu.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingRequest {
    private Long trainId;
    private Long sourceStationId;
    private Long destinationStationId;
    private LocalDate journeyDate;
    private String coachClass;
    private String holdToken;
    private String contactEmail;
    private String contactPhone;
    private String paymentMethod = "UPI";
    private List<PassengerInputDto> passengers = new ArrayList<>();

    public BookingRequest() {}

    public static class PassengerInputDto {
        private String name;
        private Integer age;
        private String gender;
        private String berthPreference;

        public PassengerInputDto() {}

        public PassengerInputDto(String name, Integer age, String gender, String berthPreference) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.berthPreference = berthPreference;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBerthPreference() { return berthPreference; }
        public void setBerthPreference(String berthPreference) { this.berthPreference = berthPreference; }
    }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public Long getSourceStationId() { return sourceStationId; }
    public void setSourceStationId(Long sourceStationId) { this.sourceStationId = sourceStationId; }
    public Long getDestinationStationId() { return destinationStationId; }
    public void setDestinationStationId(Long destinationStationId) { this.destinationStationId = destinationStationId; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public String getCoachClass() { return coachClass; }
    public void setCoachClass(String coachClass) { this.coachClass = coachClass; }
    public String getHoldToken() { return holdToken; }
    public void setHoldToken(String holdToken) { this.holdToken = holdToken; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public List<PassengerInputDto> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerInputDto> passengers) { this.passengers = passengers; }
}
