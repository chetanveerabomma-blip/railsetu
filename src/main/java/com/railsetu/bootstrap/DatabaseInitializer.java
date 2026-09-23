package com.railsetu.bootstrap;

import com.railsetu.domain.*;
import com.railsetu.dto.BookingRequest;
import com.railsetu.repository.*;
import com.railsetu.service.AuditLogService;
import com.railsetu.service.BookingService;
import com.railsetu.service.NotificationService;
import com.railsetu.service.TrainLifecycleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final RouteStopRepository routeStopRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final CoachSeatRepository coachSeatRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final FareRuleRepository fareRuleRepository;
    private final FareRepository fareRepository;
    private final FareVersionRepository fareVersionRepository;
    private final SystemConfigurationRepository configRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrainLifecycleService trainLifecycleService;
    private final BookingService bookingService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public DatabaseInitializer(RoleRepository roleRepository,
                               UserRepository userRepository,
                               StationRepository stationRepository,
                               TrainRepository trainRepository,
                               RouteStopRepository routeStopRepository,
                               TrainScheduleRepository scheduleRepository,
                               CoachRepository coachRepository,
                               CoachSeatRepository coachSeatRepository,
                               SeatInventoryRepository seatInventoryRepository,
                               FareRuleRepository fareRuleRepository,
                               FareRepository fareRepository,
                               FareVersionRepository fareVersionRepository,
                               SystemConfigurationRepository configRepository,
                               PasswordEncoder passwordEncoder,
                               TrainLifecycleService trainLifecycleService,
                               BookingService bookingService,
                               AuditLogService auditLogService,
                               NotificationService notificationService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.routeStopRepository = routeStopRepository;
        this.scheduleRepository = scheduleRepository;
        this.coachRepository = coachRepository;
        this.coachSeatRepository = coachSeatRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.fareRuleRepository = fareRuleRepository;
        this.fareRepository = fareRepository;
        this.fareVersionRepository = fareVersionRepository;
        this.configRepository = configRepository;
        this.passwordEncoder = passwordEncoder;
        this.trainLifecycleService = trainLifecycleService;
        this.bookingService = bookingService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            return;
        }

        System.out.println(">>> Initializing RailSetu Operations & Enterprise Database Seed...");

        // 1. Roles
        Role superAdminRole = roleRepository.save(new Role(RoleName.ROLE_SUPER_ADMIN));
        Role opsAdminRole = roleRepository.save(new Role(RoleName.ROLE_OPERATIONS_ADMIN));
        Role fareAdminRole = roleRepository.save(new Role(RoleName.ROLE_FARE_ADMIN));
        Role bookingAdminRole = roleRepository.save(new Role(RoleName.ROLE_BOOKING_ADMIN));
        Role verifierRole = roleRepository.save(new Role(RoleName.ROLE_VERIFIER));
        Role userRole = roleRepository.save(new Role(RoleName.ROLE_USER));

        // 2. Specialized Administrator Users
        createUser("superadmin", "password123", "superadmin@railsetu.gov.in", "Chief Operations Controller (SUPER)", Set.of(superAdminRole));
        createUser("opsadmin", "password123", "ops@railsetu.gov.in", "Senior Fleet Manager", Set.of(opsAdminRole));
        createUser("fareadmin", "password123", "fare@railsetu.gov.in", "Director Tariff & Revenue", Set.of(fareAdminRole));
        createUser("bookingadmin", "password123", "booking@railsetu.gov.in", "Chief Reservation Supervisor", Set.of(bookingAdminRole));
        createUser("verifier", "password123", "conductor@railsetu.gov.in", "Station Conductor / Ticket Inspector", Set.of(verifierRole));
        createUser("passenger", "password123", "passenger@gmail.com", "Rajesh Sharma", Set.of(userRole));

        // 3. System Configurations
        configRepository.save(new SystemConfiguration("SIMULATION_DYNAMIC_PRICING_ENABLED", "true", "Academic Dynamic Pricing Simulation Toggle"));
        configRepository.save(new SystemConfiguration("SEAT_HOLD_TIMEOUT_MINUTES", "10", "Temporary Seat Hold Timer"));

        // 4. Stations
        Station mas = stationRepository.save(new Station("MAS", "Chennai Central", "MAS", "Tamil Nadu", 13.0827, 80.2707));
        Station tpj = stationRepository.save(new Station("TPJ", "Tiruchchirappalli Jn", "TPJ", "Tamil Nadu", 10.7905, 78.7047));
        Station tbm = stationRepository.save(new Station("TBM", "Tambaram", "MAS", "Tamil Nadu", 12.9249, 80.1000));
        Station cgl = stationRepository.save(new Station("CGL", "Chengalpattu Jn", "MAS", "Tamil Nadu", 12.6841, 79.9836));
        Station vm  = stationRepository.save(new Station("VM", "Villupuram Jn", "TPJ", "Tamil Nadu", 11.9401, 79.4861));
        Station vri = stationRepository.save(new Station("VRI", "Vriddhachalam Jn", "TPJ", "Tamil Nadu", 11.5167, 79.3333));
        Station alu = stationRepository.save(new Station("ALU", "Ariyalur", "TPJ", "Tamil Nadu", 11.1401, 79.0747));
        Station ndls = stationRepository.save(new Station("NDLS", "New Delhi", "DLI", "Delhi", 28.6415, 77.2197));
        Station sbc = stationRepository.save(new Station("SBC", "KSR Bengaluru City", "SBC", "Karnataka", 12.9781, 77.5696));
        Station mdu = stationRepository.save(new Station("MDU", "Madurai Jn", "MDU", "Tamil Nadu", 9.9195, 78.1194));

        // 5. Configurable Dynamic Fare Rules
        fareRuleRepository.save(new FareRule("RULE_SUPERFAST_CHARGE", "Superfast Service Surcharge", "Flat surcharge for all Superfast express trains", 0.50, 40.0, 30.0, 20.0));
        fareRuleRepository.save(new FareRule("RULE_AC_PREMIUM", "Air Conditioned Premium Tier", "Tiered tariff multiplier for AC classes", 0.75, 60.0, 45.0, 35.0));
        fareRuleRepository.save(new FareRule("RULE_VANDE_BHARAT", "Vande Bharat Semi-High Speed Tariff", "High-speed premium corridor tariff structure", 1.10, 80.0, 50.0, 40.0));

        // 6. Train 1: 12635 Vaigai Superfast Express (MAS -> TPJ)
        Train vaigai = new Train("12635", "Vaigai Superfast Express", "SUPERFAST", mas, tpj);
        vaigai.setStatus(TrainStatus.ACTIVE);
        vaigai.setTotalCoaches(8); // streamlined composition for fast loading
        vaigai.setTotalSeats(420);
        vaigai.setActiveFareVersion(1);
        vaigai = trainRepository.save(vaigai);

        // Route stops
        routeStopRepository.save(new RouteStop(vaigai, mas, 1, "06:00", "06:05", 5, 0));
        routeStopRepository.save(new RouteStop(vaigai, tbm, 2, "06:33", "06:35", 2, 28));
        routeStopRepository.save(new RouteStop(vaigai, cgl, 3, "07:03", "07:05", 2, 56));
        routeStopRepository.save(new RouteStop(vaigai, vm,  4, "08:18", "08:20", 2, 159));
        routeStopRepository.save(new RouteStop(vaigai, vri, 5, "09:00", "09:02", 2, 213));
        routeStopRepository.save(new RouteStop(vaigai, alu, 6, "09:40", "09:42", 2, 267));
        routeStopRepository.save(new RouteStop(vaigai, tpj, 7, "10:50", "10:55", 5, 337));

        // Schedule
        scheduleRepository.save(new TrainSchedule(
                vaigai, "06:00", "10:50", "MON,TUE,WED,THU,FRI,SAT,SUN",
                ScheduleType.NORMAL, LocalDate.now(), LocalDate.now().plusYears(1)
        ));

        // Coaches & Physical Seats
        createCoachWithSeats(vaigai, "A1", CoachClass.SECOND_AC, 30, 1);
        createCoachWithSeats(vaigai, "B1", CoachClass.THIRD_AC, 45, 2);
        createCoachWithSeats(vaigai, "B2", CoachClass.THIRD_AC, 45, 3);
        createCoachWithSeats(vaigai, "S1", CoachClass.SLEEPER, 60, 4);
        createCoachWithSeats(vaigai, "S2", CoachClass.SLEEPER, 60, 5);
        createCoachWithSeats(vaigai, "S3", CoachClass.SLEEPER, 60, 6);
        createCoachWithSeats(vaigai, "C1", CoachClass.CHAIR_CAR, 60, 7);
        createCoachWithSeats(vaigai, "C2", CoachClass.CHAIR_CAR, 60, 8);

        // Generate Seat Inventory for Upcoming 7 Days
        trainLifecycleService.generateInventoryForUpcomingDays(vaigai, 7);

        // Fares for Vaigai
        createFare(vaigai, mas, tpj, CoachClass.SLEEPER, 240.0, 40.0, 20.0, 300.0);
        createFare(vaigai, mas, tpj, CoachClass.CHAIR_CAR, 380.0, 40.0, 25.0, 445.0);
        createFare(vaigai, mas, tpj, CoachClass.THIRD_AC, 650.0, 40.0, 30.0, 720.0);
        createFare(vaigai, mas, tpj, CoachClass.SECOND_AC, 980.0, 50.0, 40.0, 1070.0);

        // Train 2: 20607 Vande Bharat Express (MAS -> SBC)
        Train vb = new Train("20607", "Vande Bharat Express", "VANDE_BHARAT", mas, sbc);
        vb.setStatus(TrainStatus.ACTIVE);
        vb.setTotalCoaches(6);
        vb.setTotalSeats(360);
        vb.setActiveFareVersion(1);
        vb = trainRepository.save(vb);

        routeStopRepository.save(new RouteStop(vb, mas, 1, "05:50", "05:55", 5, 0));
        routeStopRepository.save(new RouteStop(vb, sbc, 2, "10:15", "10:20", 5, 360));

        scheduleRepository.save(new TrainSchedule(
                vb, "05:50", "10:15", "MON,WED,THU,FRI,SAT,SUN",
                ScheduleType.NORMAL, LocalDate.now(), LocalDate.now().plusYears(1)
        ));

        createCoachWithSeats(vb, "E1", CoachClass.FIRST_AC, 40, 1);
        createCoachWithSeats(vb, "C1", CoachClass.CHAIR_CAR, 64, 2);
        createCoachWithSeats(vb, "C2", CoachClass.CHAIR_CAR, 64, 3);
        createCoachWithSeats(vb, "C3", CoachClass.CHAIR_CAR, 64, 4);
        createCoachWithSeats(vb, "C4", CoachClass.CHAIR_CAR, 64, 5);
        createCoachWithSeats(vb, "C5", CoachClass.CHAIR_CAR, 64, 6);

        trainLifecycleService.generateInventoryForUpcomingDays(vb, 7);
        createFare(vb, mas, sbc, CoachClass.CHAIR_CAR, 850.0, 50.0, 40.0, 940.0);
        createFare(vb, mas, sbc, CoachClass.FIRST_AC, 1650.0, 80.0, 60.0, 1790.0);

        // Train 3: 12637 Pandian Superfast Express (MAS -> MDU)
        Train pandian = new Train("12637", "Pandian Superfast Express", "SUPERFAST", mas, mdu);
        pandian.setStatus(TrainStatus.ACTIVE);
        pandian.setTotalCoaches(6);
        pandian.setTotalSeats(360);
        pandian.setActiveFareVersion(1);
        pandian = trainRepository.save(pandian);

        routeStopRepository.save(new RouteStop(pandian, mas, 1, "21:40", "21:45", 5, 0));
        routeStopRepository.save(new RouteStop(pandian, tpj, 2, "03:15", "03:20", 5, 337));
        routeStopRepository.save(new RouteStop(pandian, mdu, 3, "05:35", "05:40", 5, 497));

        scheduleRepository.save(new TrainSchedule(
                pandian, "21:40", "05:35", "MON,TUE,WED,THU,FRI,SAT,SUN",
                ScheduleType.NORMAL, LocalDate.now(), LocalDate.now().plusYears(1)
        ));

        createCoachWithSeats(pandian, "A1", CoachClass.SECOND_AC, 30, 1);
        createCoachWithSeats(pandian, "B1", CoachClass.THIRD_AC, 45, 2);
        createCoachWithSeats(pandian, "S1", CoachClass.SLEEPER, 60, 3);
        createCoachWithSeats(pandian, "S2", CoachClass.SLEEPER, 60, 4);
        createCoachWithSeats(pandian, "S3", CoachClass.SLEEPER, 60, 5);
        createCoachWithSeats(pandian, "S4", CoachClass.SLEEPER, 60, 6);

        trainLifecycleService.generateInventoryForUpcomingDays(pandian, 7);
        createFare(pandian, mas, mdu, CoachClass.SLEEPER, 310.0, 40.0, 20.0, 370.0);
        createFare(pandian, mas, mdu, CoachClass.THIRD_AC, 820.0, 40.0, 30.0, 890.0);

        // 7. Seed Sample Initial Bookings & Audit Trail
        seedInitialBookings(vaigai, mas, tpj);

        // 8. Notifications
        notificationService.notifyAdmins(
                "Fleet Operations Initialized",
                "RailSetu central operations engine online. 3 primary corridors active: Vaigai SF (12635), Vande Bharat (20607), Pandian SF (12637).",
                "HIGH_OCCUPANCY", "INFO"
        );

        auditLogService.logActivity(
                "SYSTEM", "SYSTEM_BOOTSTRAP", "PLATFORM", 1L,
                "NULL", "ONLINE", "Database tables seeded with operational trains, routes, schedules, fares, and RBAC hierarchy."
        );

        System.out.println(">>> RailSetu Operations Database Seed Completed Successfully!");
    }

    private void createUser(String username, String password, String email, String fullName, Set<Role> roles) {
        User u = new User(username, passwordEncoder.encode(password), email, fullName, "+91 98401 23456");
        u.setRoles(roles);
        userRepository.save(u);
    }

    private void createCoachWithSeats(Train train, String coachCode, CoachClass cClass, int seatCount, int seq) {
        Coach coach = new Coach(train, coachCode, cClass, seatCount, seq);
        coach = coachRepository.save(coach);

        for (int s = 1; s <= seatCount; s++) {
            BerthType bType = (cClass == CoachClass.CHAIR_CAR) ?
                    (s % 3 == 0 ? BerthType.WINDOW : BerthType.AISLE) :
                    switch (s % 8) {
                        case 1, 4 -> BerthType.LOWER;
                        case 2, 5 -> BerthType.MIDDLE;
                        case 3, 6 -> BerthType.UPPER;
                        case 7 -> BerthType.SIDE_LOWER;
                        default -> BerthType.SIDE_UPPER;
                    };
            CoachSeat cs = new CoachSeat(coach, s, bType, cClass);
            coachSeatRepository.save(cs);
        }
    }

    private void createFare(Train train, Station src, Station dst, CoachClass cClass, Double base, Double res, Double srv, Double total) {
        Fare f = new Fare(train, src, dst, cClass, base, res, srv, total);
        f.setFareVersion(1);
        f.setStatus(FareStatus.ACTIVE);
        f.setCreatedBy("SUPER_ADMIN");
        f.setApprovedBy("SUPER_ADMIN");
        f = fareRepository.save(f);

        FareVersion fv = new FareVersion(
                f.getId(), train.getId(), train.getTrainNumber(), cClass.getCode(), 1,
                0.0, total, LocalDateTime.now(), "SUPER_ADMIN", "SUPER_ADMIN", "Baseline tariff initialization"
        );
        fareVersionRepository.save(fv);
    }

    private void seedInitialBookings(Train train, Station src, Station dst) {
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            BookingRequest req = new BookingRequest();
            req.setTrainId(train.getId());
            req.setSourceStationId(src.getId());
            req.setDestinationStationId(dst.getId());
            req.setCoachClass("3A");
            req.setJourneyDate(tomorrow);
            req.setContactEmail("rajesh.sharma@example.com");
            req.setContactPhone("9841029384");
            req.setPassengers(List.of(
                    new BookingRequest.PassengerInputDto("Rajesh Sharma", 42, "MALE", "LOWER"),
                    new BookingRequest.PassengerInputDto("Sunita Sharma", 38, "FEMALE", "LOWER")
            ));
            bookingService.createBooking(req, "passenger");
        } catch (Exception e) {
            System.err.println("Seed booking error: " + e.getMessage());
        }
    }
}
