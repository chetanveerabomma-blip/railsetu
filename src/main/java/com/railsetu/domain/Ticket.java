package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, unique = true, length = 30)
    private String ticketNumber;

    @Column(nullable = false, unique = true, length = 64)
    private String qrToken;

    private LocalDateTime issuedAt = LocalDateTime.now();
    private boolean travelPassActive = true;

    public Ticket() {}

    public Ticket(Booking booking, String ticketNumber, String qrToken) {
        this.booking = booking;
        this.ticketNumber = ticketNumber;
        this.qrToken = qrToken;
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

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public boolean isTravelPassActive() {
        return travelPassActive;
    }

    public void setTravelPassActive(boolean travelPassActive) {
        this.travelPassActive = travelPassActive;
    }
}
