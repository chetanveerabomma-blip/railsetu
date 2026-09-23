package com.railsetu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "train_routes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"train_id", "stop_sequence"}),
        @UniqueConstraint(columnNames = {"train_id", "station_id"})
})
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(length = 10)
    private String arrivalTime; // "HH:mm"

    @Column(length = 10)
    private String departureTime; // "HH:mm"

    private Integer haltMinutes = 2;

    private Integer distanceKm = 0;

    private Integer dayCount = 1;

    public RouteStop() {}

    public RouteStop(Train train, Station station, Integer stopSequence, String arrivalTime, String departureTime, Integer haltMinutes, Integer distanceKm) {
        this.train = train;
        this.station = station;
        this.stopSequence = stopSequence;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.haltMinutes = haltMinutes;
        this.distanceKm = distanceKm;
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

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getHaltMinutes() {
        return haltMinutes;
    }

    public void setHaltMinutes(Integer haltMinutes) {
        this.haltMinutes = haltMinutes;
    }

    public Integer getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Integer distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(Integer dayCount) {
        this.dayCount = dayCount;
    }
}
