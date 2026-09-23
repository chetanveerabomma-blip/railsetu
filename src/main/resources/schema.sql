-- =========================================================================
-- RAILSETU ADVANCED RAILWAY OPERATIONS & FARE MANAGEMENT SYSTEM
-- Normalized Production MySQL 8.x Database Schema (DDL)
-- =========================================================================

CREATE DATABASE IF NOT EXISTS railsetudb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE railsetudb;

-- 1. Security & RBAC
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2. Stations & Trains
CREATE TABLE IF NOT EXISTS stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    division VARCHAR(50),
    state VARCHAR(50),
    latitude DOUBLE,
    longitude DOUBLE,
    INDEX idx_station_code (code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trains (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_number VARCHAR(10) NOT NULL UNIQUE,
    train_name VARCHAR(120) NOT NULL,
    train_type VARCHAR(50) NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    total_coaches INT DEFAULT 0,
    total_seats INT DEFAULT 0,
    active_fare_version INT DEFAULT 1,
    current_version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_train_src FOREIGN KEY (source_station_id) REFERENCES stations(id),
    CONSTRAINT fk_train_dst FOREIGN KEY (destination_station_id) REFERENCES stations(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS train_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    created_by VARCHAR(80) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_reason VARCHAR(255),
    previous_configuration TEXT,
    new_configuration TEXT,
    INDEX idx_train_version (train_id, version_number)
) ENGINE=InnoDB;

-- 3. Routes & Schedules
CREATE TABLE IF NOT EXISTS train_routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    stop_sequence INT NOT NULL,
    arrival_time VARCHAR(10),
    departure_time VARCHAR(10),
    halt_minutes INT DEFAULT 2,
    distance_km INT DEFAULT 0,
    day_count INT DEFAULT 1,
    UNIQUE KEY uq_train_seq (train_id, stop_sequence),
    UNIQUE KEY uq_train_station (train_id, station_id),
    CONSTRAINT fk_route_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_station FOREIGN KEY (station_id) REFERENCES stations(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS route_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    route_snapshot_json TEXT,
    created_by VARCHAR(80),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_reason VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS train_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    departure_time VARCHAR(10) NOT NULL,
    arrival_time VARCHAR(10) NOT NULL,
    running_days VARCHAR(60) NOT NULL,
    schedule_type VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    effective_from DATE NOT NULL,
    effective_until DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    current_version INT DEFAULT 1,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedule_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS schedule_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    schedule_snapshot_json TEXT,
    created_by VARCHAR(80),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_reason VARCHAR(255)
) ENGINE=InnoDB;

-- 4. Coaches & Seat Inventory
CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    coach_code VARCHAR(10) NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    total_seats INT NOT NULL,
    coach_sequence INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    UNIQUE KEY uq_train_coach (train_id, coach_code),
    CONSTRAINT fk_coach_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS coach_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coach_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    berth_type VARCHAR(20) NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    UNIQUE KEY uq_coach_seat (coach_id, seat_number),
    CONSTRAINT fk_seat_coach FOREIGN KEY (coach_id) REFERENCES coaches(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seat_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_seat_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    held_by_session_id VARCHAR(80),
    hold_expires_at TIMESTAMP NULL,
    version BIGINT DEFAULT 0,
    UNIQUE KEY uq_seat_inv (train_id, journey_date, coach_seat_id),
    INDEX idx_inv_lookup (train_id, journey_date, status),
    CONSTRAINT fk_inv_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_inv_seat FOREIGN KEY (coach_seat_id) REFERENCES coach_seats(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seat_holds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hold_token VARCHAR(64) NOT NULL UNIQUE,
    seat_inventory_id BIGINT NOT NULL,
    user_identifier VARCHAR(100) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_hold_inv FOREIGN KEY (seat_inventory_id) REFERENCES seat_inventory(id)
) ENGINE=InnoDB;

-- 5. Dynamic Fares & Rules Engine
CREATE TABLE IF NOT EXISTS fare_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    rule_name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    applies_to_train_type VARCHAR(40) DEFAULT 'ALL',
    applies_to_class VARCHAR(20) DEFAULT 'ALL',
    min_distance_km INT DEFAULT 0,
    max_distance_km INT DEFAULT 10000,
    base_rate_per_km DOUBLE DEFAULT 0.45,
    reservation_charge DOUBLE DEFAULT 40.0,
    superfast_charge DOUBLE DEFAULT 30.0,
    service_charge DOUBLE DEFAULT 20.0,
    dynamic_multiplier DOUBLE DEFAULT 1.0,
    concession_percent DOUBLE DEFAULT 0.0,
    min_occupancy_percent DOUBLE DEFAULT 0.0,
    max_occupancy_percent DOUBLE DEFAULT 100.0,
    priority INT DEFAULT 1,
    active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS fares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    base_fare DOUBLE NOT NULL,
    reservation_charge DOUBLE DEFAULT 40.0,
    service_charge DOUBLE DEFAULT 20.0,
    dynamic_surcharge DOUBLE DEFAULT 0.0,
    cancellation_charge DOUBLE DEFAULT 60.0,
    total_fare DOUBLE NOT NULL,
    fare_version INT NOT NULL DEFAULT 1,
    effective_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_by VARCHAR(80),
    approved_by VARCHAR(80),
    approval_remarks VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fare_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_fare_src FOREIGN KEY (source_station_id) REFERENCES stations(id),
    CONSTRAINT fk_fare_dst FOREIGN KEY (destination_station_id) REFERENCES stations(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS fare_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fare_id BIGINT NOT NULL,
    train_id BIGINT,
    train_number VARCHAR(20),
    coach_class VARCHAR(20),
    version_number INT NOT NULL,
    previous_fare DOUBLE,
    new_fare DOUBLE,
    base_fare DOUBLE,
    reservation_charge DOUBLE,
    service_charge DOUBLE,
    effective_date TIMESTAMP,
    created_by VARCHAR(80),
    approved_by VARCHAR(80),
    change_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS fare_approval_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fare_id BIGINT NOT NULL,
    current_fare DOUBLE NOT NULL,
    proposed_fare DOUBLE NOT NULL,
    reason VARCHAR(255),
    affected_future_bookings INT DEFAULT 0,
    projected_revenue_impact DOUBLE DEFAULT 0.0,
    effective_date TIMESTAMP,
    proposed_by VARCHAR(80) NOT NULL,
    proposed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approval_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    reviewed_by VARCHAR(80),
    reviewed_at TIMESTAMP NULL,
    review_notes VARCHAR(255),
    CONSTRAINT fk_appr_fare FOREIGN KEY (fare_id) REFERENCES fares(id)
) ENGINE=InnoDB;

-- 6. Bookings, Tickets, Payments, Refunds
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pnr_number VARCHAR(10) NOT NULL UNIQUE,
    train_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    total_passengers INT NOT NULL DEFAULT 1,
    total_fare DOUBLE NOT NULL,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    booked_by_username VARCHAR(60),
    fare_version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pnr (pnr_number),
    CONSTRAINT fk_bk_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_bk_src FOREIGN KEY (source_station_id) REFERENCES stations(id),
    CONSTRAINT fk_bk_dst FOREIGN KEY (destination_station_id) REFERENCES stations(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS booking_passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10),
    berth_preference VARCHAR(20),
    coach_code VARCHAR(10),
    seat_number INT,
    berth_type VARCHAR(20),
    passenger_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    rac_position INT,
    wl_position INT,
    CONSTRAINT fk_pass_bk FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    ticket_number VARCHAR(30) NOT NULL UNIQUE,
    qr_token VARCHAR(64) NOT NULL UNIQUE,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    travel_pass_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_tkt_bk FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    payment_reference VARCHAR(60) NOT NULL UNIQUE,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(30) DEFAULT 'UPI_NETBANKING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_bk FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    refund_reference VARCHAR(60) NOT NULL UNIQUE,
    refund_amount DOUBLE NOT NULL,
    cancellation_charge_deducted DOUBLE NOT NULL,
    refund_status VARCHAR(30) DEFAULT 'COMPLETED',
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),
    CONSTRAINT fk_ref_bk FOREIGN KEY (booking_id) REFERENCES bookings(id)
) ENGINE=InnoDB;

-- 7. RAC & Waitlist Live Queues
CREATE TABLE IF NOT EXISTS rac_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    booking_passenger_id BIGINT NOT NULL UNIQUE,
    rac_number INT NOT NULL,
    priority_order INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rac_queue (train_id, journey_date, coach_class, priority_order),
    CONSTRAINT fk_rac_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_rac_pass FOREIGN KEY (booking_passenger_id) REFERENCES booking_passengers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS waitlist_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_class VARCHAR(20) NOT NULL,
    booking_passenger_id BIGINT NOT NULL UNIQUE,
    waitlist_number INT NOT NULL,
    priority_order INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_wl_queue (train_id, journey_date, coach_class, priority_order),
    CONSTRAINT fk_wl_train FOREIGN KEY (train_id) REFERENCES trains(id),
    CONSTRAINT fk_wl_pass FOREIGN KEY (booking_passenger_id) REFERENCES booking_passengers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Digital Pass & Handheld Verification
CREATE TABLE IF NOT EXISTS digital_passes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pnr VARCHAR(10) NOT NULL UNIQUE,
    qr_token VARCHAR(64) NOT NULL UNIQUE,
    passenger_details_json TEXT,
    qr_base64 TEXT,
    verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    verified_by_verifier VARCHAR(80),
    station_code VARCHAR(20),
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS qr_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pnr VARCHAR(10) NOT NULL,
    verifier_username VARCHAR(80) NOT NULL,
    station_code VARCHAR(20),
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verification_result VARCHAR(30) NOT NULL,
    remarks VARCHAR(255)
) ENGINE=InnoDB;

-- 9. Notifications, Audits & Configs
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient VARCHAR(80) NOT NULL,
    title VARCHAR(120) NOT NULL,
    message TEXT,
    notification_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admin_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_username VARCHAR(80) NOT NULL,
    action VARCHAR(60) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id BIGINT NOT NULL,
    previous_value TEXT,
    new_value TEXT,
    change_reason VARCHAR(255),
    ip_address VARCHAR(60) DEFAULT '127.0.0.1',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_ts (timestamp),
    INDEX idx_audit_entity (entity_type, entity_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS system_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(80) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    last_updated_by VARCHAR(80),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;
