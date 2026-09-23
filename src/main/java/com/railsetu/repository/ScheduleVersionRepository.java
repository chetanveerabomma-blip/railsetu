package com.railsetu.repository;

import com.railsetu.domain.ScheduleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleVersionRepository extends JpaRepository<ScheduleVersion, Long> {
    List<ScheduleVersion> findByTrainIdOrderByVersionNumberDesc(Long trainId);
}
