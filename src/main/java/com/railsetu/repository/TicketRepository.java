package com.railsetu.repository;

import com.railsetu.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    Optional<Ticket> findByBookingId(Long bookingId);
    Optional<Ticket> findByQrToken(String qrToken);
    Optional<Ticket> findByBookingPnrNumber(String pnrNumber);
}
