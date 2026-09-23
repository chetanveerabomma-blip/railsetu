package com.railsetu.service;

import com.railsetu.domain.SeatHold;
import com.railsetu.domain.SeatInventory;
import com.railsetu.domain.SeatStatus;
import com.railsetu.dto.SeatHoldRequest;
import com.railsetu.dto.SeatHoldResponse;
import com.railsetu.repository.SeatHoldRepository;
import com.railsetu.repository.SeatInventoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SeatLockService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final SeatHoldRepository seatHoldRepository;

    @Value("${railsetu.inventory.seat-hold-timeout-minutes:10}")
    private int seatHoldTimeoutMinutes;

    public SeatLockService(SeatInventoryRepository seatInventoryRepository, SeatHoldRepository seatHoldRepository) {
        this.seatInventoryRepository = seatInventoryRepository;
        this.seatHoldRepository = seatHoldRepository;
    }

    /**
     * Section 11 & 21: Pessimistic Row-Level Lock & Hold Creation
     */
    @Transactional
    public SeatHoldResponse holdSeats(SeatHoldRequest req) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(seatHoldTimeoutMinutes);
        List<Long> lockedIds = new ArrayList<>();

        for (Long seatId : req.getCoachSeatIds()) {
            SeatInventory inv = seatInventoryRepository.findByIdForUpdate(seatId)
                    .orElseThrow(() -> new IllegalStateException("Seat Inventory record not found for ID: " + seatId));

            if (inv.getStatus() != SeatStatus.AVAILABLE) {
                // If held by same user and not expired, allow renewal
                if (inv.getStatus() == SeatStatus.HELD && req.getUserIdentifier().equals(inv.getHeldBySessionId()) && inv.getHoldExpiresAt().isAfter(LocalDateTime.now())) {
                    inv.setHoldExpiresAt(expiresAt);
                    seatInventoryRepository.save(inv);
                    lockedIds.add(inv.getId());
                    continue;
                }
                throw new IllegalStateException("Seat is no longer available! Current status: " + inv.getStatus());
            }

            // Lock seat
            inv.setStatus(SeatStatus.HELD);
            inv.setHeldBySessionId(req.getUserIdentifier());
            inv.setHoldExpiresAt(expiresAt);
            seatInventoryRepository.save(inv);

            SeatHold hold = new SeatHold(token, inv, req.getUserIdentifier(), expiresAt);
            seatHoldRepository.save(hold);
            lockedIds.add(inv.getId());
        }

        long remainingSecs = Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
        return new SeatHoldResponse(
                true, token, lockedIds, expiresAt, remainingSecs,
                "Seats locked successfully for 10 minutes. Please complete payment."
        );
    }

    @Transactional
    public void releaseHold(String holdToken) {
        List<SeatHold> holds = seatHoldRepository.findByHoldToken(holdToken);
        for (SeatHold hold : holds) {
            SeatInventory inv = hold.getSeatInventory();
            if (inv.getStatus() == SeatStatus.HELD) {
                inv.setStatus(SeatStatus.AVAILABLE);
                inv.setHeldBySessionId(null);
                inv.setHoldExpiresAt(null);
                seatInventoryRepository.save(inv);
            }
            hold.setStatus("RELEASED");
            seatHoldRepository.save(hold);
        }
    }

    /**
     * Scheduled cleanup of expired holds (Runs every 30 seconds)
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatInventory> expiredInventories = seatInventoryRepository.findExpiredHolds(now);
        for (SeatInventory inv : expiredInventories) {
            inv.setStatus(SeatStatus.AVAILABLE);
            inv.setHeldBySessionId(null);
            inv.setHoldExpiresAt(null);
            seatInventoryRepository.save(inv);
        }

        List<SeatHold> expiredHolds = seatHoldRepository.findByStatusAndExpiresAtBefore("ACTIVE", now);
        for (SeatHold hold : expiredHolds) {
            hold.setStatus("EXPIRED");
            seatHoldRepository.save(hold);
        }
    }
}
