package com.railsetu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "coaches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"train_id", "coach_code"})
})
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "coach_code", nullable = false, length = 10)
    private String coachCode; // e.g. "A1", "B1", "S1", "L1"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachClass coachClass; // 1A, 2A, 3A, SL, CC, 2S

    private Integer totalSeats;

    private Integer coachSequence; // 1, 2, 3...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachStatus status = CoachStatus.ACTIVE;

    public Coach() {}

    public Coach(Train train, String coachCode, CoachClass coachClass, Integer totalSeats, Integer coachSequence) {
        this.train = train;
        this.coachCode = coachCode;
        this.coachClass = coachClass;
        this.totalSeats = totalSeats;
        this.coachSequence = coachSequence;
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

    public String getCoachCode() {
        return coachCode;
    }

    public void setCoachCode(String coachCode) {
        this.coachCode = coachCode;
    }

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getCoachSequence() {
        return coachSequence;
    }

    public void setCoachSequence(Integer coachSequence) {
        this.coachSequence = coachSequence;
    }

    public CoachStatus getStatus() {
        return status;
    }

    public void setStatus(CoachStatus status) {
        this.status = status;
    }
}
