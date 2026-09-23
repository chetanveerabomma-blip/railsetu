package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_pnr", columnList = "pnr_number", unique = true),
        @Index(name = "idx_booking_train_date", columnList = "train_id, journey_date")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pnr_number", nullable = false, unique = true, length = 10)
    private String pnrNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Column(nullable = false)
    private LocalDate journeyDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachClass coachClass;

    @Column(nullable = false)
    private Integer totalPassengers = 1;

    @Column(nullable = false)
    private Double totalFare = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus bookingStatus = BookingStatus.CONFIRMED;

    @Column(length = 100)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 60)
    private String bookedByUsername;

    private Integer fareVersion = 1;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BookingPassenger> passengers = new ArrayList<>();

    public Booking() {}

    public Booking(String pnrNumber, Train train, Station sourceStation, Station destinationStation, LocalDate journeyDate, CoachClass coachClass, Integer totalPassengers, Double totalFare, BookingStatus bookingStatus) {
        this.pnrNumber = pnrNumber;
        this.train = train;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.journeyDate = journeyDate;
        this.coachClass = coachClass;
        this.totalPassengers = totalPassengers;
        this.totalFare = totalFare;
        this.bookingStatus = bookingStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
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

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
    }

    public Integer getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(Integer totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getBookedByUsername() {
        return bookedByUsername;
    }

    public void setBookedByUsername(String bookedByUsername) {
        this.bookedByUsername = bookedByUsername;
    }

    public Integer getFareVersion() {
        return fareVersion;
    }

    public void setFareVersion(Integer fareVersion) {
        this.fareVersion = fareVersion;
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

    public List<BookingPassenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<BookingPassenger> passengers) {
        this.passengers = passengers;
    }
}
