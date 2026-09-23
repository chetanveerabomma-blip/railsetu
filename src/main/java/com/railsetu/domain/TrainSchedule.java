package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_schedules")
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(nullable = false, length = 10)
    private String departureTime; // "06:00"

    @Column(nullable = false, length = 10)
    private String arrivalTime; // "13:30"

    @Column(nullable = false, length = 60)
    private String runningDays; // "MON,TUE,WED,THU,FRI,SAT,SUN"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleType scheduleType = ScheduleType.NORMAL;

    private LocalDate effectiveFrom = LocalDate.now();
    private LocalDate effectiveUntil = LocalDate.now().plusYears(1);

    @Column(length = 20)
    private String status = "ACTIVE";

    private Integer currentVersion = 1;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public TrainSchedule() {}

    public TrainSchedule(Train train, String departureTime, String arrivalTime, String runningDays, ScheduleType scheduleType, LocalDate effectiveFrom, LocalDate effectiveUntil) {
        this.train = train;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.runningDays = runningDays;
        this.scheduleType = scheduleType;
        this.effectiveFrom = effectiveFrom;
        this.effectiveUntil = effectiveUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getRunningDays() {
        return runningDays;
    }

    public void setRunningDays(String runningDays) {
        this.runningDays = runningDays;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveUntil() {
        return effectiveUntil;
    }

    public void setEffectiveUntil(LocalDate effectiveUntil) {
        this.effectiveUntil = effectiveUntil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
