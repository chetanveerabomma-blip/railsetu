package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rac_queue", indexes = {
        @Index(name = "idx_rac_queue", columnList = "train_id, journey_date, coach_class, priority_order")
})
public class RacQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coach_class", nullable = false, length = 20)
    private CoachClass coachClass;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_passenger_id", nullable = false)
    private BookingPassenger bookingPassenger;

    @Column(nullable = false)
    private Integer racNumber; // RAC 01, RAC 02...

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder; // 1, 2, 3...

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public RacQueue() {}

    public RacQueue(Train train, LocalDate journeyDate, CoachClass coachClass, BookingPassenger bookingPassenger, Integer racNumber, Integer priorityOrder) {
        this.train = train;
        this.journeyDate = journeyDate;
        this.coachClass = coachClass;
        this.bookingPassenger = bookingPassenger;
        this.racNumber = racNumber;
        this.priorityOrder = priorityOrder;
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

    public BookingPassenger getBookingPassenger() {
        return bookingPassenger;
    }

    public void setBookingPassenger(BookingPassenger bookingPassenger) {
        this.bookingPassenger = bookingPassenger;
    }

    public Integer getRacNumber() {
        return racNumber;
    }

    public void setRacNumber(Integer racNumber) {
        this.racNumber = racNumber;
    }

    public Integer getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
