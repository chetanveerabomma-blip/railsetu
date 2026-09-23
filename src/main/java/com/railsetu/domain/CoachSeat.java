package com.railsetu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "coach_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"coach_id", "seat_number"})
})
public class CoachSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BerthType berthType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachClass coachClass;

    public CoachSeat() {}

    public CoachSeat(Coach coach, Integer seatNumber, BerthType berthType, CoachClass coachClass) {
        this.coach = coach;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.coachClass = coachClass;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
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

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public void setCoachClass(CoachClass coachClass) {
        this.coachClass = coachClass;
    }
}
