package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_holds", indexes = {
        @Index(name = "idx_hold_token", columnList = "hold_token")
})
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hold_token", nullable = false, length = 64)
    private String holdToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_inventory_id", nullable = false)
    private SeatInventory seatInventory;

    @Column(nullable = false, length = 100)
    private String userIdentifier; // email or session token

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, EXPIRED, CONVERTED, RELEASED

    public SeatHold() {}

    public SeatHold(String holdToken, SeatInventory seatInventory, String userIdentifier, LocalDateTime expiresAt) {
        this.holdToken = holdToken;
        this.seatInventory = seatInventory;
        this.userIdentifier = userIdentifier;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoldToken() {
        return holdToken;
    }

    public void setHoldToken(String holdToken) {
        this.holdToken = holdToken;
    }

    public SeatInventory getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
