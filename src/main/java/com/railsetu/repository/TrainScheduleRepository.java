package com.railsetu.repository;

import com.railsetu.domain.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    List<TrainSchedule> findByTrainId(Long trainId);
    Optional<TrainSchedule> findFirstByTrainIdAndStatus(Long trainId, String status);
    void deleteByTrainId(Long trainId);
}
