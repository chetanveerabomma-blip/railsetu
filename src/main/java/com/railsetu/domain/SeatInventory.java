package com.railsetu.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_inventory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"train_id", "journey_date", "coach_seat_id"})
}, indexes = {
        @Index(name = "idx_seat_inv_lookup", columnList = "train_id, journey_date, status")
})
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coach_seat_id", nullable = false)
    private CoachSeat coachSeat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(length = 80)
    private String heldBySessionId;

    private LocalDateTime holdExpiresAt;

    @Version
    private Long version = 0L;

    public SeatInventory() {}

    public SeatInventory(Train train, LocalDate journeyDate, CoachSeat coachSeat, SeatStatus status) {
        this.train = train;
        this.journeyDate = journeyDate;
        this.coachSeat = coachSeat;
        this.status = status;
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

    public CoachSeat getCoachSeat() {
        return coachSeat;
    }

    public void setCoachSeat(CoachSeat coachSeat) {
        this.coachSeat = coachSeat;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getHeldBySessionId() {
        return heldBySessionId;
    }

    public void setHeldBySessionId(String heldBySessionId) {
        this.heldBySessionId = heldBySessionId;
    }

    public LocalDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(LocalDateTime holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
