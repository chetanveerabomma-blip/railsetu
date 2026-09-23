package com.railsetu.repository;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.Fare;
import com.railsetu.domain.FareStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FareRepository extends JpaRepository<Fare, Long> {

    List<Fare> findByTrainId(Long trainId);

    Optional<Fare> findByTrainIdAndSourceStationIdAndDestinationStationIdAndCoachClassAndStatus(
            Long trainId, Long sourceStationId, Long destinationStationId, CoachClass coachClass, FareStatus status);

    List<Fare> findByTrainIdAndCoachClassAndStatus(Long trainId, CoachClass coachClass, FareStatus status);

    @Query("SELECT f FROM Fare f WHERE " +
           "(:trainId IS NULL OR f.train.id = :trainId) AND " +
           "(:status IS NULL OR f.status = :status) AND " +
           "(:coachClass IS NULL OR f.coachClass = :coachClass)")
    Page<Fare> searchFares(@Param("trainId") Long trainId,
                           @Param("status") FareStatus status,
                           @Param("coachClass") CoachClass coachClass,
                           Pageable pageable);

    @Query("SELECT f FROM Fare f WHERE f.status = 'APPROVED' AND f.effectiveDate <= :now")
    List<Fare> findApprovedFaresReadyToActivate(@Param("now") LocalDateTime now);

    long countByStatus(FareStatus status);
}
