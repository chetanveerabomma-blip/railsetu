package com.railsetu.repository;

import com.railsetu.domain.Train;
import com.railsetu.domain.TrainStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainNumber(String trainNumber);

    boolean existsByTrainNumber(String trainNumber);

    List<Train> findByStatus(TrainStatus status);

    long countByStatus(TrainStatus status);

    @Query("SELECT t FROM Train t WHERE " +
           "(:search IS NULL OR LOWER(t.trainNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.trainName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:trainType IS NULL OR LOWER(t.trainType) = LOWER(:trainType))")
    Page<Train> searchTrains(@Param("search") String search,
                             @Param("status") TrainStatus status,
                             @Param("trainType") String trainType,
                             Pageable pageable);

    @Query("SELECT t FROM Train t WHERE t.sourceStation.id = :sourceId AND t.destinationStation.id = :destinationId AND t.status = 'ACTIVE'")
    List<Train> findActiveBetweenStations(@Param("sourceId") Long sourceId, @Param("destinationId") Long destinationId);
}
