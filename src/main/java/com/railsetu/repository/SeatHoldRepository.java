package com.railsetu.repository;

import com.railsetu.domain.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
    List<SeatHold> findByHoldToken(String holdToken);
    List<SeatHold> findByStatusAndExpiresAtBefore(String status, LocalDateTime now);
    List<SeatHold> findByUserIdentifierAndStatus(String userIdentifier, String status);
}
