package com.railsetu.repository;

import com.railsetu.domain.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByTrainIdOrderByStopSequenceAsc(Long trainId);
    Optional<RouteStop> findByTrainIdAndStationId(Long trainId, Long stationId);
    void deleteByTrainId(Long trainId);
}
