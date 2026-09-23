package com.railsetu.dto;

import java.util.List;
import java.util.Map;

public class DashboardMetricsResponse {
    private long activeTrains;
    private long totalTrains;
    private long todayBookings;
    private long totalBookings;
    private long confirmedBookings;
    private long racCount;
    private long waitlistCount;
    private double overallOccupancyPercent;
    private double grossRevenue;
    private double todayRevenue;
    private double totalRefunds;
    private double netRevenue;

    private List<Map<String, Object>> revenueByTrain;
    private List<Map<String, Object>> revenueByClass;
    private List<Map<String, Object>> recentBookings;
    private List<Map<String, Object>> trainOccupancyList;

    public DashboardMetricsResponse() {}

    public long getActiveTrains() { return activeTrains; }
    public void setActiveTrains(long activeTrains) { this.activeTrains = activeTrains; }
    public long getTotalTrains() { return totalTrains; }
    public void setTotalTrains(long totalTrains) { this.totalTrains = totalTrains; }
    public long getTodayBookings() { return todayBookings; }
    public void setTodayBookings(long todayBookings) { this.todayBookings = todayBookings; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }
    public long getRacCount() { return racCount; }
    public void setRacCount(long racCount) { this.racCount = racCount; }
    public long getWaitlistCount() { return waitlistCount; }
    public void setWaitlistCount(long waitlistCount) { this.waitlistCount = waitlistCount; }
    public double getOverallOccupancyPercent() { return overallOccupancyPercent; }
    public void setOverallOccupancyPercent(double overallOccupancyPercent) { this.overallOccupancyPercent = overallOccupancyPercent; }
    public double getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(double grossRevenue) { this.grossRevenue = grossRevenue; }
    public double getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(double todayRevenue) { this.todayRevenue = todayRevenue; }
    public double getTotalRefunds() { return totalRefunds; }
    public void setTotalRefunds(double totalRefunds) { this.totalRefunds = totalRefunds; }
    public double getNetRevenue() { return netRevenue; }
    public void setNetRevenue(double netRevenue) { this.netRevenue = netRevenue; }
    public List<Map<String, Object>> getRevenueByTrain() { return revenueByTrain; }
    public void setRevenueByTrain(List<Map<String, Object>> revenueByTrain) { this.revenueByTrain = revenueByTrain; }
    public List<Map<String, Object>> getRevenueByClass() { return revenueByClass; }
    public void setRevenueByClass(List<Map<String, Object>> revenueByClass) { this.revenueByClass = revenueByClass; }
    public List<Map<String, Object>> getRecentBookings() { return recentBookings; }
    public void setRecentBookings(List<Map<String, Object>> recentBookings) { this.recentBookings = recentBookings; }
    public List<Map<String, Object>> getTrainOccupancyList() { return trainOccupancyList; }
    public void setTrainOccupancyList(List<Map<String, Object>> trainOccupancyList) { this.trainOccupancyList = trainOccupancyList; }
}
