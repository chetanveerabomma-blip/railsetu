package com.railsetu.repository;

import com.railsetu.domain.Coach;
import com.railsetu.domain.CoachClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrainIdOrderByCoachSequenceAsc(Long trainId);
    Optional<Coach> findByTrainIdAndCoachCode(Long trainId, String coachCode);
    List<Coach> findByTrainIdAndCoachClass(Long trainId, CoachClass coachClass);
    void deleteByTrainId(Long trainId);
}
