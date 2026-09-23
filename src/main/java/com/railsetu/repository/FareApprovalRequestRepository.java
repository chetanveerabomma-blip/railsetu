package com.railsetu.repository;

import com.railsetu.domain.FareApprovalRequest;
import com.railsetu.domain.FareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FareApprovalRequestRepository extends JpaRepository<FareApprovalRequest, Long> {
    List<FareApprovalRequest> findByApprovalStatusOrderByProposedAtDesc(FareStatus status);
    List<FareApprovalRequest> findByFareIdOrderByProposedAtDesc(Long fareId);
}
