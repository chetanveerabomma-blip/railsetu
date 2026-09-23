package com.railsetu.repository;

import com.railsetu.domain.QrVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QrVerificationRepository extends JpaRepository<QrVerification, Long> {
    List<QrVerification> findByPnrOrderByVerifiedAtDesc(String pnr);
    List<QrVerification> findTop20ByOrderByVerifiedAtDesc();
}
