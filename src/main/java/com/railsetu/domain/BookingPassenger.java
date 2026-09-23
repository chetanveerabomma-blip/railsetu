package com.railsetu.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_passengers")
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @Column(nullable = false, length = 100)
    private String passengerName;

    private Integer age;

    @Column(length = 10)
    private String gender; // MALE, FEMALE, OTHER

    @Column(length = 20)
    private String berthPreference;

    @Column(length = 10)
    private String coachCode;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BerthType berthType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus passengerStatus = BookingStatus.CONFIRMED;

    private Integer racPosition;
    private Integer wlPosition;

    public BookingPassenger() {}

    public BookingPassenger(String passengerName, Integer age, String gender, String berthPreference) {
        this.passengerName = passengerName;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBerthPreference() {
        return berthPreference;
    }

    public void setBerthPreference(String berthPreference) {
        this.berthPreference = berthPreference;
    }

    public String getCoachCode() {
        return coachCode;
    }

    public void setCoachCode(String coachCode) {
        this.coachCode = coachCode;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BerthType getBerthType() {
        return berthType;
    }

    public void setBerthType(BerthType berthType) {
        this.berthType = berthType;
    }

    public BookingStatus getPassengerStatus() {
        return passengerStatus;
    }

    public void setPassengerStatus(BookingStatus passengerStatus) {
        this.passengerStatus = passengerStatus;
    }

    public Integer getRacPosition() {
        return racPosition;
    }

    public void setRacPosition(Integer racPosition) {
        this.racPosition = racPosition;
    }

    public Integer getWlPosition() {
        return wlPosition;
    }

    public void setWlPosition(Integer wlPosition) {
        this.wlPosition = wlPosition;
    }
}
