package com.railsetu.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrainWizardRequest {

    // Step 1: Basic Train Info
    private String trainNumber;
    private String trainName;
    private String trainType; // SUPERFAST, EXPRESS, VANDE_BHARAT
    private String sourceStationCode;
    private String destinationStationCode;
    private String changeReason = "Initial train creation";

    // Step 2: Route Stations
    private List<RouteStopDto> routeStops = new ArrayList<>();

    // Step 3: Schedule
    private ScheduleDto schedule;

    // Step 4 & 5: Coach & Seat Configurations
    private List<CoachConfigDto> coaches = new ArrayList<>();

    // Step 6: Fare Configurations
    private List<FareConfigDto> fares = new ArrayList<>();

    public TrainWizardRequest() {}

    public static class RouteStopDto {
        private String stationCode;
        private Integer stopSequence;
        private String arrivalTime;
        private String departureTime;
        private Integer haltMinutes;
        private Integer distanceKm;

        public RouteStopDto() {}

        public RouteStopDto(String stationCode, Integer stopSequence, String arrivalTime, String departureTime, Integer haltMinutes, Integer distanceKm) {
            this.stationCode = stationCode;
            this.stopSequence = stopSequence;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.haltMinutes = haltMinutes;
            this.distanceKm = distanceKm;
        }

        public String getStationCode() { return stationCode; }
        public void setStationCode(String stationCode) { this.stationCode = stationCode; }
        public Integer getStopSequence() { return stopSequence; }
        public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }
        public String getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public Integer getHaltMinutes() { return haltMinutes; }
        public void setHaltMinutes(Integer haltMinutes) { this.haltMinutes = haltMinutes; }
        public Integer getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Integer distanceKm) { this.distanceKm = distanceKm; }
    }

    public static class ScheduleDto {
        private String departureTime;
        private String arrivalTime;
        private String runningDays; // e.g. "MON,TUE,WED,THU,FRI,SAT,SUN"
        private String scheduleType; // NORMAL, SEASONAL, SPECIAL
        private LocalDate effectiveFrom;
        private LocalDate effectiveUntil;

        public ScheduleDto() {}

        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
        public String getRunningDays() { return runningDays; }
        public void setRunningDays(String runningDays) { this.runningDays = runningDays; }
        public String getScheduleType() { return scheduleType; }
        public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }
        public LocalDate getEffectiveFrom() { return effectiveFrom; }
        public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
        public LocalDate getEffectiveUntil() { return effectiveUntil; }
        public void setEffectiveUntil(LocalDate effectiveUntil) { this.effectiveUntil = effectiveUntil; }
    }

    public static class CoachConfigDto {
        private String coachCode;
        private String coachClass;
        private Integer totalSeats;
        private Integer coachSequence;

        public CoachConfigDto() {}

        public CoachConfigDto(String coachCode, String coachClass, Integer totalSeats, Integer coachSequence) {
            this.coachCode = coachCode;
            this.coachClass = coachClass;
            this.totalSeats = totalSeats;
            this.coachSequence = coachSequence;
        }

        public String getCoachCode() { return coachCode; }
        public void setCoachCode(String coachCode) { this.coachCode = coachCode; }
        public String getCoachClass() { return coachClass; }
        public void setCoachClass(String coachClass) { this.coachClass = coachClass; }
        public Integer getTotalSeats() { return totalSeats; }
        public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
        public Integer getCoachSequence() { return coachSequence; }
        public void setCoachSequence(Integer coachSequence) { this.coachSequence = coachSequence; }
    }

    public static class FareConfigDto {
        private String coachClass;
        private Double baseFare;
        private Double reservationCharge;
        private Double serviceCharge;
        private Double dynamicSurcharge;
        private Double totalFare;

        public FareConfigDto() {}

        public String getCoachClass() { return coachClass; }
        public void setCoachClass(String coachClass) { this.coachClass = coachClass; }
        public Double getBaseFare() { return baseFare; }
        public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }
        public Double getReservationCharge() { return reservationCharge; }
        public void setReservationCharge(Double reservationCharge) { this.reservationCharge = reservationCharge; }
        public Double getServiceCharge() { return serviceCharge; }
        public void setServiceCharge(Double serviceCharge) { this.serviceCharge = serviceCharge; }
        public Double getDynamicSurcharge() { return dynamicSurcharge; }
        public void setDynamicSurcharge(Double dynamicSurcharge) { this.dynamicSurcharge = dynamicSurcharge; }
        public Double getTotalFare() { return totalFare; }
        public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }
    }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getTrainType() { return trainType; }
    public void setTrainType(String trainType) { this.trainType = trainType; }
    public String getSourceStationCode() { return sourceStationCode; }
    public void setSourceStationCode(String sourceStationCode) { this.sourceStationCode = sourceStationCode; }
    public String getDestinationStationCode() { return destinationStationCode; }
    public void setDestinationStationCode(String destinationStationCode) { this.destinationStationCode = destinationStationCode; }
    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
    public List<RouteStopDto> getRouteStops() { return routeStops; }
    public void setRouteStops(List<RouteStopDto> routeStops) { this.routeStops = routeStops; }
    public ScheduleDto getSchedule() { return schedule; }
    public void setSchedule(ScheduleDto schedule) { this.schedule = schedule; }
    public List<CoachConfigDto> getCoaches() { return coaches; }
    public void setCoaches(List<CoachConfigDto> coaches) { this.coaches = coaches; }
    public List<FareConfigDto> getFares() { return fares; }
    public void setFares(List<FareConfigDto> fares) { this.fares = fares; }
}
