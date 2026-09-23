package com.railsetu.controller;

import com.railsetu.domain.Booking;
import com.railsetu.domain.BookingStatus;
import com.railsetu.domain.RacQueue;
import com.railsetu.domain.Refund;
import com.railsetu.domain.WaitlistQueue;
import com.railsetu.repository.BookingRepository;
import com.railsetu.repository.RacQueueRepository;
import com.railsetu.repository.WaitlistQueueRepository;
import com.railsetu.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BOOKING_ADMIN')")
public class AdminBookingController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final RacQueueRepository racQueueRepository;
    private final WaitlistQueueRepository waitlistQueueRepository;

    public AdminBookingController(BookingRepository bookingRepository,
                                  BookingService bookingService,
                                  RacQueueRepository racQueueRepository,
                                  WaitlistQueueRepository waitlistQueueRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.racQueueRepository = racQueueRepository;
        this.waitlistQueueRepository = waitlistQueueRepository;
    }

    /**
     * Section 25 & 30: Admin Booking Control with Server-Side Pagination & Filters
     */
    @GetMapping("/bookings")
    public ResponseEntity<Page<Booking>> listBookings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) String journeyDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        LocalDate date = (journeyDate != null && !journeyDate.isEmpty()) ? LocalDate.parse(journeyDate) : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return ResponseEntity.ok(bookingService.searchBookings(search, status, trainId, date, pageable));
    }

    @GetMapping("/bookings/{pnr}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable String pnr) {
        return bookingService.getBookingByPnr(pnr)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Section 25: Admin Cancellations with Audit Trail
     */
    @PostMapping("/cancellations/{pnr}")
    public ResponseEntity<Refund> cancelBooking(
            @PathVariable String pnr,
            @RequestParam(defaultValue = "Administrative cancellation") String reason,
            Principal principal) {
        String admin = (principal != null) ? principal.getName() : "ADMIN_BOOKING";
        return ResponseEntity.ok(bookingService.cancelBooking(pnr, reason, admin));
    }

    /**
     * Section 26: Live RAC Queue
     */
    @GetMapping("/rac/{trainId}")
    public ResponseEntity<List<RacQueue>> getRacQueue(
            @PathVariable Long trainId,
            @RequestParam(required = false) String journeyDate) {
        LocalDate date = (journeyDate != null && !journeyDate.isEmpty()) ? LocalDate.parse(journeyDate) : LocalDate.now().plusDays(1);
        return ResponseEntity.ok(racQueueRepository.findByTrainIdAndJourneyDateAndActiveTrueOrderByCoachClassAscPriorityOrderAsc(trainId, date));
    }

    /**
     * Section 26: Live Waiting List Queue
     */
    @GetMapping("/waitlist/{trainId}")
    public ResponseEntity<List<WaitlistQueue>> getWaitlistQueue(
            @PathVariable Long trainId,
            @RequestParam(required = false) String journeyDate) {
        LocalDate date = (journeyDate != null && !journeyDate.isEmpty()) ? LocalDate.parse(journeyDate) : LocalDate.now().plusDays(1);
        return ResponseEntity.ok(waitlistQueueRepository.findByTrainIdAndJourneyDateAndActiveTrueOrderByCoachClassAscPriorityOrderAsc(trainId, date));
    }
}
