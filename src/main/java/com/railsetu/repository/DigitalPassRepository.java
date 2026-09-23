package com.railsetu.repository;

import com.railsetu.domain.DigitalPass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DigitalPassRepository extends JpaRepository<DigitalPass, Long> {
    Optional<DigitalPass> findByPnr(String pnr);
    Optional<DigitalPass> findByQrToken(String qrToken);
}
