package com.railsetu.dto;

public class FareCalculationResponse {
    private Double baseFare;
    private Double reservationCharge;
    private Double serviceCharge;
    private Double dynamicComponent;
    private Double discount;
    private Double totalFare;
    private Integer fareVersion;
    private Double occupancyPercent;
    private String simulationNote;

    public FareCalculationResponse() {}

    public FareCalculationResponse(Double baseFare, Double reservationCharge, Double serviceCharge, Double dynamicComponent, Double discount, Double totalFare, Integer fareVersion) {
        this.baseFare = baseFare;
        this.reservationCharge = reservationCharge;
        this.serviceCharge = serviceCharge;
        this.dynamicComponent = dynamicComponent;
        this.discount = discount;
        this.totalFare = totalFare;
        this.fareVersion = fareVersion;
    }

    public Double getBaseFare() { return baseFare; }
    public void setBaseFare(Double baseFare) { this.baseFare = baseFare; }
    public Double getReservationCharge() { return reservationCharge; }
    public void setReservationCharge(Double reservationCharge) { this.reservationCharge = reservationCharge; }
    public Double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(Double serviceCharge) { this.serviceCharge = serviceCharge; }
    public Double getDynamicComponent() { return dynamicComponent; }
    public void setDynamicComponent(Double dynamicComponent) { this.dynamicComponent = dynamicComponent; }
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    public Double getTotalFare() { return totalFare; }
    public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }
    public Integer getFareVersion() { return fareVersion; }
    public void setFareVersion(Integer fareVersion) { this.fareVersion = fareVersion; }
    public Double getOccupancyPercent() { return occupancyPercent; }
    public void setOccupancyPercent(Double occupancyPercent) { this.occupancyPercent = occupancyPercent; }
    public String getSimulationNote() { return simulationNote; }
    public void setSimulationNote(String simulationNote) { this.simulationNote = simulationNote; }
}
