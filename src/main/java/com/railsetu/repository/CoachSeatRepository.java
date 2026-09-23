package com.railsetu.repository;

import com.railsetu.domain.CoachClass;
import com.railsetu.domain.CoachSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoachSeatRepository extends JpaRepository<CoachSeat, Long> {
    List<CoachSeat> findByCoachIdOrderBySeatNumberAsc(Long coachId);
    List<CoachSeat> findByCoachTrainId(Long trainId);
    List<CoachSeat> findByCoachTrainIdAndCoachClass(Long trainId, CoachClass coachClass);
    long countByCoachTrainId(Long trainId);
}
