package com.railsetu.repository;

import com.railsetu.domain.RouteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteVersionRepository extends JpaRepository<RouteVersion, Long> {
    List<RouteVersion> findByTrainIdOrderByVersionNumberDesc(Long trainId);
}
