package com.railsetu.repository;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.WaitlistQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaitlistQueueRepository extends JpaRepository<WaitlistQueue, Long> {
    List<WaitlistQueue> findByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    List<WaitlistQueue> findByTrainIdAndJourneyDateAndActiveTrueOrderByCoachClassAscPriorityOrderAsc(
            Long trainId, LocalDate journeyDate);

    Optional<WaitlistQueue> findFirstByTrainIdAndJourneyDateAndCoachClassAndActiveTrueOrderByPriorityOrderAsc(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    long countByTrainIdAndJourneyDateAndCoachClassAndActiveTrue(
            Long trainId, LocalDate journeyDate, CoachClass coachClass);

    long countByActiveTrue();
}
