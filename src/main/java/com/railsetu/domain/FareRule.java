package com.railsetu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "fare_rules")
public class FareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String ruleCode;

    @Column(nullable = false, length = 120)
    private String ruleName;

    @Column(length = 255)
    private String description;

    @Column(length = 40)
    private String appliesToTrainType = "ALL"; // ALL, SUPERFAST, EXPRESS, VANDE_BHARAT

    @Column(length = 20)
    private String appliesToClass = "ALL"; // ALL, 1A, 2A, 3A, SL, CC, 2S

    private Integer minDistanceKm = 0;
    private Integer maxDistanceKm = 10000;

    private Double baseRatePerKm = 0.45; // in rupees
    private Double reservationCharge = 40.0;
    private Double superfastCharge = 30.0;
    private Double serviceCharge = 20.0;
    private Double dynamicMultiplier = 1.0;
    private Double concessionPercent = 0.0;

    private Double minOccupancyPercent = 0.0;
    private Double maxOccupancyPercent = 100.0;

    private Integer priority = 1;
    private boolean active = true;

    public FareRule() {}

    public FareRule(String ruleCode, String ruleName, String description, Double baseRatePerKm, Double reservationCharge, Double superfastCharge, Double serviceCharge) {
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.description = description;
        this.baseRatePerKm = baseRatePerKm;
        this.reservationCharge = reservationCharge;
        this.superfastCharge = superfastCharge;
        this.serviceCharge = serviceCharge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppliesToTrainType() {
        return appliesToTrainType;
    }

    public void setAppliesToTrainType(String appliesToTrainType) {
        this.appliesToTrainType = appliesToTrainType;
    }

    public String getAppliesToClass() {
        return appliesToClass;
    }

    public void setAppliesToClass(String appliesToClass) {
        this.appliesToClass = appliesToClass;
    }

    public Integer getMinDistanceKm() {
        return minDistanceKm;
    }

    public void setMinDistanceKm(Integer minDistanceKm) {
        this.minDistanceKm = minDistanceKm;
    }

    public Integer getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(Integer maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    public Double getBaseRatePerKm() {
        return baseRatePerKm;
    }

    public void setBaseRatePerKm(Double baseRatePerKm) {
        this.baseRatePerKm = baseRatePerKm;
    }

    public Double getReservationCharge() {
        return reservationCharge;
    }

    public void setReservationCharge(Double reservationCharge) {
        this.reservationCharge = reservationCharge;
    }

    public Double getSuperfastCharge() {
        return superfastCharge;
    }

    public void setSuperfastCharge(Double superfastCharge) {
        this.superfastCharge = superfastCharge;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Double getDynamicMultiplier() {
        return dynamicMultiplier;
    }

    public void setDynamicMultiplier(Double dynamicMultiplier) {
        this.dynamicMultiplier = dynamicMultiplier;
    }

    public Double getConcessionPercent() {
        return concessionPercent;
    }

    public void setConcessionPercent(Double concessionPercent) {
        this.concessionPercent = concessionPercent;
    }

    public Double getMinOccupancyPercent() {
        return minOccupancyPercent;
    }

    public void setMinOccupancyPercent(Double minOccupancyPercent) {
        this.minOccupancyPercent = minOccupancyPercent;
    }

    public Double getMaxOccupancyPercent() {
        return maxOccupancyPercent;
    }

    public void setMaxOccupancyPercent(Double maxOccupancyPercent) {
        this.maxOccupancyPercent = maxOccupancyPercent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
