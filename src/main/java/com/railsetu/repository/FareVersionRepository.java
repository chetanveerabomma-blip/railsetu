package com.railsetu.repository;

import com.railsetu.domain.FareVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FareVersionRepository extends JpaRepository<FareVersion, Long> {
    List<FareVersion> findByFareIdOrderByVersionNumberDesc(Long fareId);
    List<FareVersion> findByTrainIdOrderByVersionNumberDesc(Long trainId);
}
