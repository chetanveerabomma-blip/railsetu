# 🚆 RailSetu — Advanced Admin Railway Operations & Fare Management Platform

[![Java](https://img.shields.io/badge/Java-21%2B%20%7C%2026-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-RBAC%20%2B%20JWT-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/MySQL%208.x-Supported-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An enterprise-grade railway operations, fleet inventory, dynamic fare calculation, and reservation management platform designed with strict separation of concerns, concurrency control, and verifiable digital travel passes.

---

## 🌟 Key Architectural Capabilities

### 1. 🛡️ Role-Based Access Control (RBAC Hierarchy)
- **`SUPER_ADMIN`**: Full platform authority, train publishing & suspension, tariff approvals, financial analytics, system toggles, and immutable audit inspection.
- **`OPERATIONS_ADMIN`**: Fleet management, sequential route editing, running day timetables, coach rake compositions, and physical seat inventory.
- **`FARE_ADMIN`**: Dynamic tariff creation, fare rule authoring, fare revision proposals, impact analysis, and pricing simulation.
- **`BOOKING_ADMIN`**: Reservation control, PNR lookup, operational cancellations, and live RAC/WL queues.
- **`VERIFIER`**: Handheld conductor terminal with QR token cryptographic verification and scan audit logging.
- **`USER / PASSENGER`**: Customer booking portal, train search, real-time fare calculation, temporary seat lock with 10-minute timer, and digital travel passes.

---

### 2. 🚆 Train Fleet Control Center & 8-Step Wizard
- **Real-time Status Monitoring**: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `CANCELLED`, `UNDER_MAINTENANCE`, `SEASONAL`.
- **8-Step Wizard**: Basic Info → Route Sequence → Schedule → Coach Composition → Seats → Fare Matrix → Review → 10-Point Pre-Publish Validation → Publish.
- **Train Versioning**: Configuration snapshots archived in `train_versions`.
- **Dependency-Checked Suspension**: Prohibits destructive deletion when active tickets exist; executes safe suspension workflow with 100% automated passenger refunds and audit records.

---

### 3. 💺 Concurrency-Protected Seat Inventory & 10-Minute Lock
- **Berth Mapping**: Lower, Middle, Upper, Side Lower, Side Upper, Window, Aisle.
- **Pessimistic Row-Level Locking** (`LockModeType.PESSIMISTIC_WRITE`) guarantees zero double-booking under concurrent load.
- **Temporary Seat Locking**: 10-minute payment countdown timer with automated garbage-collection worker releasing expired holds.

---

### 4. 💰 Dynamic Fare Control Center & Rule Engine
- **Authoritative Backend API**: `POST /api/fare/calculate` enforces tamper-proof server-side pricing.
- **Academic Dynamic Pricing Simulation**: Configurable surge (+12% to +25%) based on occupancy tiers (>50%, >80%) with visual disclaimer and toggle.
- **Fare Impact Analysis & Approval Workflow**: Previews tariff differences, affected future bookings, and projected revenue before Super Admin approval.
- **Scheduled Fare Changes**: Automated background worker (`@Scheduled`) activates future approved fares when effective date arrives.

---

### 5. 🔄 RAC & Waitlist Cascading Queue Engine
- Live priority queues for RAC and Waiting List.
- When any confirmed ticket is cancelled:
  1. Seat is transactionally released.
  2. Top RAC passenger is automatically promoted to `CONFIRMED` and allocated the berth.
  3. Top Waiting List passenger is automatically promoted to `RAC`.
  4. System emits passenger alerts and records audit events.

---

### 6. 📱 Digital Travel Pass & QR Ticket Verification
- Generates cryptographically verifiable QR Codes via **ZXing**.
- Conductor handheld scanner validates QR tokens or 10-digit PNRs and logs verification history.

---

## 🛠️ Technology Stack

- **Backend**: Java 21+ / Java 26, Spring Boot 3.3.4, Spring Data JPA / Hibernate, Spring Security 6, JJWT 0.12.5, ZXing 3.5.3.
- **Database**:
  - **Development / Default**: In-Memory H2 in MySQL dialect mode (`jdbc:h2:mem:railsetudb;MODE=MySQL;DATABASE_TO_UPPER=FALSE`) for zero external dependencies.
  - **Production**: MySQL 8.x with 25+ normalized tables provided in `src/main/resources/schema.sql`.
- **Frontend**: Responsive Single-Page Application (HTML5, Modern CSS Glassmorphic design, Vanilla ES6+ JavaScript, Chart.js for operational telemetry).

---

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher (`java -version`)
- Maven 3.9+ (`mvn -v`)

### Run Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/chetanveerabomma-blip/railsetu.git
   cd railsetu
   ```

2. **Build and test**:
   ```bash
   mvn clean test
   ```

3. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the platform**:
   - Web Application: **[http://localhost:8080](http://localhost:8080)**
   - H2 Database Console: **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**
     - JDBC URL: `jdbc:h2:mem:railsetudb`
     - Username: `railsetu`
     - Password: `railsetu_secret`

---

## 🔑 Quick Login Credentials

The application features a convenient **Role Quick Switcher** dropdown in the top bar:

| Role | Username | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **`ROLE_SUPER_ADMIN`** | `superadmin` | `password123` | Full administrative control & tariff approvals |
| **`ROLE_OPERATIONS_ADMIN`**| `opsadmin` | `password123` | Train fleet, routes, schedules, coach rakes |
| **`ROLE_FARE_ADMIN`** | `fareadmin` | `password123` | Tariffs, fare rules, dynamic simulation |
| **`ROLE_BOOKING_ADMIN`** | `bookingadmin`| `password123` | PNR search, cancellations, RAC/WL queues |
| **`ROLE_VERIFIER`** | `verifier` | `password123` | Conductor handheld QR scanner & audit history |
| **`ROLE_USER`** | `passenger` | `password123` | Customer booking portal & digital travel passes |

---

## 📡 Key REST API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate and obtain JWT token |
| `POST` | `/api/fare/calculate` | Authoritative server-side fare calculation |
| `POST` | `/api/public/seats/hold` | Create temporary 10-minute seat lock |
| `POST` | `/api/public/bookings` | Book ticket, allocate berth, issue PNR & QR pass |
| `POST` | `/api/admin/trains/wizard` | Execute 8-Step Train Creation Wizard |
| `GET` | `/api/admin/trains/{id}/removal-check` | Dependency check preventing destructive deletion |
| `POST` | `/api/admin/trains/{id}/suspend` | Execute safe suspension workflow with 100% refunds |
| `POST` | `/api/admin/fares/propose` | FARE_ADMIN submits proposed tariff revision |
| `POST` | `/api/admin/fares/approvals/{id}` | SUPER_ADMIN approves/rejects tariff change |
| `POST` | `/api/admin/cancellations/{pnr}` | Cancel booking and trigger cascading RAC/WL promotion |
| `POST` | `/api/admin/verification/verify` | Verify QR token or PNR |
| `GET` | `/api/admin/audit-logs` | Retrieve immutable operational audit trail |

---

## 📄 License
This project is licensed under the MIT License.
