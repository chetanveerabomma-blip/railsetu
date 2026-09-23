package com.railsetu.repository;

import com.railsetu.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByPaymentReference(String paymentReference);
}
