package com.railsetu;

import com.railsetu.domain.*;
import com.railsetu.dto.*;
import com.railsetu.repository.*;
import com.railsetu.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RailSetuApplicationTests {

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private SeatInventoryRepository seatInventoryRepository;

    @Autowired
    private FareService fareService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TrainLifecycleService trainLifecycleService;

    @Autowired
    private RacWaitlistService racWaitlistService;

    @Autowired
    private SeatLockService seatLockService;

    @Test
    @DisplayName("1. System Context & Bootstrap Data Loads Successfully")
    void contextLoads() {
        assertTrue(trainRepository.count() >= 3, "At least 3 seeded trains should be loaded in database");
        assertTrue(stationRepository.count() >= 8, "Major Indian stations should be present");
    }

    @Test
    @DisplayName("2. Authoritative Fare Calculation Engine Test (Section 22)")
    void testAuthoritativeFareCalculation() {
        Train train = trainRepository.findByTrainNumber("12635").orElseThrow();
        Station mas = stationRepository.findByCode("MAS").orElseThrow();
        Station tpj = stationRepository.findByCode("TPJ").orElseThrow();

        FareCalculationRequest req = new FareCalculationRequest();
        req.setTrainId(train.getId());
        req.setSourceStationId(mas.getId());
        req.setDestinationStationId(tpj.getId());
        req.setTravelClass("3A");
        req.setJourneyDate(LocalDate.now().plusDays(2));
        req.setPassengerCount(2);

        FareCalculationResponse resp = fareService.calculateFare(req);

        assertNotNull(resp);
        assertTrue(resp.getBaseFare() > 0, "Base fare must be calculated from database rules");
        assertTrue(resp.getReservationCharge() > 0, "Reservation charge must be included");
        assertTrue(resp.getTotalFare() > resp.getBaseFare(), "Total fare must sum base fare and charges");
        assertEquals(train.getActiveFareVersion(), resp.getFareVersion(), "Must use active fare version");
    }

    @Test
    @DisplayName("3. Train Removal Dependency Check Prevents Destructive Deletion (Section 6)")
    void testTrainRemovalDependencyCheck() {
        Train train = trainRepository.findByTrainNumber("12635").orElseThrow();

        TrainRemovalImpactResponse impact = trainLifecycleService.checkRemovalDependencies(train.getId());

        assertNotNull(impact);
        assertFalse(impact.isDeletionAllowed(), "Permanent deletion must be strictly disallowed");
        assertEquals("SUSPEND TRAIN", impact.getRecommendedAction(), "Recommended action must be SUSPEND TRAIN");
    }

    @Test
    @DisplayName("4. Temporary Seat Locking & Expiration (Section 11)")
    @Transactional
    void testTemporarySeatLocking() {
        Train train = trainRepository.findByTrainNumber("12635").orElseThrow();
        LocalDate journeyDate = LocalDate.now().plusDays(3);
        trainLifecycleService.generateInventoryForUpcomingDays(train, 4);

        List<SeatInventory> invList = seatInventoryRepository.findByTrainIdAndJourneyDate(train.getId(), journeyDate);
        assertFalse(invList.isEmpty(), "Inventory should be generated");

        SeatHoldRequest req = new SeatHoldRequest();
        req.setTrainId(train.getId());
        req.setJourneyDate(journeyDate);
        req.setCoachSeatIds(List.of(invList.get(0).getId(), invList.get(1).getId()));
        req.setUserIdentifier("test_user_session");

        SeatHoldResponse holdResp = seatLockService.holdSeats(req);

        assertTrue(holdResp.isSuccess());
        assertNotNull(holdResp.getHoldToken(), "Hold token UUID must be generated");
        assertTrue(holdResp.getRemainingSeconds() > 0, "Remaining seconds must be positive (~600s)");

        // Release hold
        seatLockService.releaseHold(holdResp.getHoldToken());
    }

    @Test
    @DisplayName("5. Booking Creation and PNR & QR Code Generation (Section 21 & 22)")
    @Transactional
    void testBookingCreation() {
        Train train = trainRepository.findByTrainNumber("12635").orElseThrow();
        Station mas = stationRepository.findByCode("MAS").orElseThrow();
        Station tpj = stationRepository.findByCode("TPJ").orElseThrow();
        LocalDate journeyDate = LocalDate.now().plusDays(4);

        BookingRequest bReq = new BookingRequest();
        bReq.setTrainId(train.getId());
        bReq.setSourceStationId(mas.getId());
        bReq.setDestinationStationId(tpj.getId());
        bReq.setCoachClass("3A");
        bReq.setJourneyDate(journeyDate);
        bReq.setContactEmail("test.passenger@railsetu.in");
        bReq.setContactPhone("+91 99999 88888");
        bReq.setPassengers(List.of(
                new BookingRequest.PassengerInputDto("Test Passenger A", 32, "MALE", "LOWER")
        ));

        BookingResponse bResp = bookingService.createBooking(bReq, "passenger");

        assertNotNull(bResp);
        assertEquals(10, bResp.getPnrNumber().length(), "PNR must be a 10-digit number");
        assertNotNull(bResp.getQrBase64(), "QR Code image must be generated via ZXing");
        assertNotNull(bResp.getTicketNumber());
        assertEquals("CONFIRMED", bResp.getBookingStatus());
    }
}
