package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String trainNumber;

    @Column(nullable = false, length = 120)
    private String trainName;

    @Column(nullable = false, length = 50)
    private String trainType; // e.g. SUPERFAST, EXPRESS, VANDE_BHARAT, RAJDHANI

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TrainStatus status = TrainStatus.ACTIVE;

    private Integer totalCoaches = 0;
    private Integer totalSeats = 0;
    private Integer activeFareVersion = 1;
    private Integer currentVersion = 1;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Train() {}

    public Train(String trainNumber, String trainName, String trainType, Station sourceStation, Station destinationStation) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.trainType = trainType;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(Station sourceStation) {
        this.sourceStation = sourceStation;
    }

    public Station getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(Station destinationStation) {
        this.destinationStation = destinationStation;
    }

    public TrainStatus getStatus() {
        return status;
    }

    public void setStatus(TrainStatus status) {
        this.status = status;
    }

    public Integer getTotalCoaches() {
        return totalCoaches;
    }

    public void setTotalCoaches(Integer totalCoaches) {
        this.totalCoaches = totalCoaches;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getActiveFareVersion() {
        return activeFareVersion;
    }

    public void setActiveFareVersion(Integer activeFareVersion) {
        this.activeFareVersion = activeFareVersion;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
