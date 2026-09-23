package com.railsetu.repository;

import com.railsetu.domain.BookingPassenger;
import com.railsetu.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    List<BookingPassenger> findByBookingId(Long bookingId);
    long countByPassengerStatus(BookingStatus status);
}
