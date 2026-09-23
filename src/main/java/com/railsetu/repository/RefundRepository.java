package com.railsetu.repository;

import com.railsetu.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByBookingId(Long bookingId);
    Optional<Refund> findByRefundReference(String refundReference);

    @Query("SELECT COALESCE(SUM(r.refundAmount), 0.0) FROM Refund r")
    Double calculateTotalRefunds();

    @Query("SELECT COALESCE(SUM(r.cancellationChargeDeducted), 0.0) FROM Refund r")
    Double calculateTotalCancellationCharges();
}
