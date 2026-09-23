package com.railsetu.repository;

import com.railsetu.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByCode(String code);
    List<Station> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
    boolean existsByCode(String code);
}
