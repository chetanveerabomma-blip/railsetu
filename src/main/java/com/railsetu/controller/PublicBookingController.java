package com.railsetu.controller;

import com.railsetu.domain.*;
import com.railsetu.dto.*;
import com.railsetu.repository.*;
import com.railsetu.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping({"/api/public", "/api"})
public class PublicBookingController {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final RouteStopRepository routeStopRepository;
    private final CoachRepository coachRepository;
    private final CoachSeatRepository coachSeatRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final FareService fareService;
    private final SeatLockService seatLockService;
    private final BookingService bookingService;
    private final TrainLifecycleService trainLifecycleService;

    public PublicBookingController(StationRepository stationRepository,
                                   TrainRepository trainRepository,
                                   RouteStopRepository routeStopRepository,
                                   CoachRepository coachRepository,
                                   CoachSeatRepository coachSeatRepository,
                                   SeatInventoryRepository seatInventoryRepository,
                                   FareService fareService,
                                   SeatLockService seatLockService,
                                   BookingService bookingService,
                                   TrainLifecycleService trainLifecycleService) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.routeStopRepository = routeStopRepository;
        this.coachRepository = coachRepository;
        this.coachSeatRepository = coachSeatRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.fareService = fareService;
        this.seatLockService = seatLockService;
        this.bookingService = bookingService;
        this.trainLifecycleService = trainLifecycleService;
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    @GetMapping("/trains/search")
    public ResponseEntity<List<Map<String, Object>>> searchTrains(
            @RequestParam(required = false) Long sourceStationId,
            @RequestParam(required = false) Long destinationStationId,
            @RequestParam(required = false) String journeyDate) {

        List<Train> trains;
        if (sourceStationId != null && destinationStationId != null) {
            trains = trainRepository.findActiveBetweenStations(sourceStationId, destinationStationId);
        } else {
            trains = trainRepository.findByStatus(TrainStatus.ACTIVE);
        }

        LocalDate date = (journeyDate != null && !journeyDate.isEmpty())
                ? LocalDate.parse(journeyDate) : LocalDate.now().plusDays(1);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Train t : trains) {
            // Ensure seat inventory generated for this date
            trainLifecycleService.generateInventoryForUpcomingDays(t, 7);

            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("trainNumber", t.getTrainNumber());
            map.put("trainName", t.getTrainName());
            map.put("trainType", t.getTrainType());
            map.put("sourceStation", t.getSourceStation());
            map.put("destinationStation", t.getDestinationStation());
            map.put("status", t.getStatus());
            map.put("totalCoaches", t.getTotalCoaches());
            map.put("totalSeats", t.getTotalSeats());
            map.put("fareVersion", t.getActiveFareVersion());

            // Get available seats count
            long availableSeats = seatInventoryRepository.countByTrainIdAndJourneyDateAndStatus(t.getId(), date, SeatStatus.AVAILABLE);
            map.put("availableSeats", availableSeats);

            // Get classes available on this train
            List<Coach> coaches = coachRepository.findByTrainIdOrderByCoachSequenceAsc(t.getId());
            Set<String> classCodes = new LinkedHashSet<>();
            for (Coach c : coaches) {
                classCodes.add(c.getCoachClass().getCode());
            }
            map.put("classes", classCodes);

            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trains/{id}/route")
    public ResponseEntity<List<RouteStop>> getTrainRoute(@PathVariable Long id) {
        return ResponseEntity.ok(routeStopRepository.findByTrainIdOrderByStopSequenceAsc(id));
    }

    @GetMapping("/trains/{id}/coaches")
    public ResponseEntity<List<Coach>> getTrainCoaches(@PathVariable Long id) {
        return ResponseEntity.ok(coachRepository.findByTrainIdOrderByCoachSequenceAsc(id));
    }

    @GetMapping("/trains/{id}/seats")
    public ResponseEntity<List<Map<String, Object>>> getSeatInventory(
            @PathVariable Long id,
            @RequestParam String journeyDate,
            @RequestParam(required = false) String coachClass) {

        LocalDate date = LocalDate.parse(journeyDate);
        Train train = trainRepository.findById(id).orElseThrow();
        trainLifecycleService.generateInventoryForUpcomingDays(train, 7);

        List<SeatInventory> invList;
        if (coachClass != null && !coachClass.isEmpty() && !"ALL".equalsIgnoreCase(coachClass)) {
            invList = seatInventoryRepository.findByTrainIdAndJourneyDateAndCoachSeatCoachClass(
                    id, date, CoachClass.fromCode(coachClass)
            );
        } else {
            invList = seatInventoryRepository.findByTrainIdAndJourneyDate(id, date);
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (SeatInventory inv : invList) {
            Map<String, Object> m = new HashMap<>();
            m.put("inventoryId", inv.getId());
            m.put("coachSeatId", inv.getCoachSeat().getId());
            m.put("coachCode", inv.getCoachSeat().getCoach().getCoachCode());
            m.put("coachClass", inv.getCoachSeat().getCoachClass().getCode());
            m.put("seatNumber", inv.getCoachSeat().getSeatNumber());
            m.put("berthType", inv.getCoachSeat().getBerthType().name());
            m.put("status", inv.getStatus().name());
            res.add(m);
        }

        return ResponseEntity.ok(res);
    }

    /**
     * Section 22: Authoritative Fare Calculation Endpoint (POST /api/fare/calculate)
     */
    @PostMapping("/fare/calculate")
    public ResponseEntity<FareCalculationResponse> calculateFare(@RequestBody FareCalculationRequest req) {
        return ResponseEntity.ok(fareService.calculateFare(req));
    }

    @PostMapping("/seats/hold")
    public ResponseEntity<SeatHoldResponse> holdSeats(@RequestBody SeatHoldRequest req) {
        return ResponseEntity.ok(seatLockService.holdSeats(req));
    }

    @PostMapping("/seats/release")
    public ResponseEntity<Map<String, String>> releaseHold(@RequestParam String holdToken) {
        seatLockService.releaseHold(holdToken);
        return ResponseEntity.ok(Map.of("message", "Seat hold released successfully"));
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest req, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(bookingService.createBooking(req, username));
    }

    @GetMapping("/bookings/{pnr}")
    public ResponseEntity<Booking> getBooking(@PathVariable String pnr) {
        return bookingService.getBookingByPnr(pnr)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bookings/{pnr}/cancel")
    public ResponseEntity<Refund> cancelBooking(
            @PathVariable String pnr,
            @RequestParam(required = false, defaultValue = "Passenger cancelled online") String reason,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : "PASSENGER_SELF";
        return ResponseEntity.ok(bookingService.cancelBooking(pnr, reason, username));
    }
}
