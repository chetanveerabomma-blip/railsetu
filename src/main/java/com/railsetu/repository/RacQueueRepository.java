package com.railsetu.repository;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.RacQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RacQueueRepository extends JpaRepository<RacQueue, Long> {
    List<RacQueue> findByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    List<RacQueue> findByTrainIdAndJourneyDateAndActiveTrueOrderByCoachClassAscPriorityOrderAsc(
            Long trainId, LocalDate journeyDate);

    Optional<RacQueue> findFirstByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    long countByTrainIdAndJourneyDateAndCoachClassAndActiveTrue(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    long countByActiveTrue();
}
