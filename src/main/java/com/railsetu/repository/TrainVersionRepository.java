package com.railsetu.repository;

import com.railsetu.domain.TrainVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainVersionRepository extends JpaRepository<TrainVersion, Long> {
    List<TrainVersion> findByTrainIdOrderByVersionNumberDesc(Long trainId);
}
