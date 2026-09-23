package com.railsetu.repository;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.SeatInventory;
import com.railsetu.domain.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {

    List<SeatInventory> findByTrainIdAndJourneyDate(Long trainId, LocalDate journeyDate);

    List<SeatInventory> findByTrainIdAndJourneyDateAndCoachSeatCoachClass(Long trainId, LocalDate journeyDate, CoachClass coachClass);

    long countByTrainIdAndJourneyDateAndCoachSeatCoachClassAndStatus(Long trainId, LocalDate journeyDate, CoachClass coachClass, SeatStatus status);

    long countByTrainIdAndJourneyDateAndStatus(Long trainId, LocalDate journeyDate, SeatStatus status);

    long countByTrainIdAndJourneyDate(Long trainId, LocalDate journeyDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT si FROM SeatInventory si WHERE si.id = :id")
    Optional<SeatInventory> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT si FROM SeatInventory si WHERE si.train.id = :trainId AND si.journeyDate = :journeyDate AND si.coachSeat.coachClass = :coachClass AND si.status = 'AVAILABLE' ORDER BY si.id ASC")
    List<SeatInventory> findAvailableSeatsForUpdate(@Param("trainId") Long trainId,
                                                    @Param("journeyDate") LocalDate journeyDate,
                                                    @Param("coachClass") CoachClass coachClass,
                                                    Pageable pageable);

    @Query("SELECT si FROM SeatInventory si WHERE si.status = 'HELD' AND si.holdExpiresAt < :now")
    List<SeatInventory> findExpiredHolds(@Param("now") LocalDateTime now);

    Optional<SeatInventory> findByTrainIdAndJourneyDateAndCoachSeatId(Long trainId, LocalDate journeyDate, Long coachSeatId);
}
