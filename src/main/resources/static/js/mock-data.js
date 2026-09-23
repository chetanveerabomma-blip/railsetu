// =========================================================================
// RAILSETU CLIENT-SIDE MOCK SERVICE WORKER FOR GITHUB PAGES / STANDALONE
// Automatically intercepts /api/* requests when deployed statically on GitHub Pages
// =========================================================================

(function() {
  const isGitHubPages = window.location.hostname.includes('github.io') || 
                        window.location.protocol === 'file:' ||
                        window.location.search.includes('mock=true');

  if (!isGitHubPages) {
    // If running with local Spring Boot server, test if backend is reachable
    return;
  }

  console.info('🚆 RailSetu: Operating in GitHub Pages / Client-Side Simulation Mode');

  // Initial Mock State
  const db = {
    users: {
      superadmin: { username: 'superadmin', fullName: 'Chief Operations Controller (SUPER)', roles: ['ROLE_SUPER_ADMIN'], token: 'mock-jwt-superadmin' },
      opsadmin: { username: 'opsadmin', fullName: 'Rolling Stock & Line Ops Admin', roles: ['ROLE_OPERATIONS_ADMIN'], token: 'mock-jwt-opsadmin' },
      fareadmin: { username: 'fareadmin', fullName: 'Tariff & Revenue Pricing Officer', roles: ['ROLE_FARE_ADMIN'], token: 'mock-jwt-fareadmin' },
      bookingadmin: { username: 'bookingadmin', fullName: 'Commercial Reservation Supervisor', roles: ['ROLE_BOOKING_ADMIN'], token: 'mock-jwt-bookingadmin' },
      verifier: { username: 'verifier', fullName: 'Senior Traveling Ticket Examiner (TTE)', roles: ['ROLE_VERIFIER'], token: 'mock-jwt-verifier' },
      passenger: { username: 'passenger', fullName: 'V Chetan (Verified Passenger)', roles: ['ROLE_USER'], token: 'mock-jwt-passenger' }
    },
    stations: [
      { id: 1, code: 'MAS', name: 'Chennai Central', junction: true, zone: 'SR', division: 'MAS' },
      { id: 2, code: 'MS', name: 'Chennai Egmore', junction: true, zone: 'SR', division: 'MAS' },
      { id: 3, code: 'TBM', name: 'Tambaram', junction: false, zone: 'SR', division: 'MAS' },
      { id: 4, code: 'CGL', name: 'Chengalpattu Junction', junction: true, zone: 'SR', division: 'MAS' },
      { id: 5, code: 'VM', name: 'Villupuram Junction', junction: true, zone: 'SR', division: 'TPJ' },
      { id: 6, code: 'VRI', name: 'Vriddhachalam Junction', junction: true, zone: 'SR', division: 'TPJ' },
      { id: 7, code: 'TPJ', name: 'Tiruchchirappalli Junction', junction: true, zone: 'SR', division: 'TPJ' },
      { id: 8, code: 'DG', name: 'Dindigul Junction', junction: true, zone: 'SR', division: 'MDU' },
      { id: 9, code: 'MDU', name: 'Madurai Junction', junction: true, zone: 'SR', division: 'MDU' },
      { id: 10, code: 'TEN', name: 'Tirunelveli Junction', junction: true, zone: 'SR', division: 'TVC' }
    ],
    trains: [
      {
        id: 1,
        trainNumber: '12635',
        trainName: 'Vaigai Superfast Express',
        trainType: 'SUPERFAST',
        sourceStation: { code: 'MS', name: 'Chennai Egmore' },
        destinationStation: { code: 'MDU', name: 'Madurai Junction' },
        status: 'ACTIVE',
        totalSeats: 320,
        currentVersion: 1
      },
      {
        id: 2,
        trainNumber: '20607',
        trainName: 'Vande Bharat Express',
        trainType: 'VANDE_BHARAT',
        sourceStation: { code: 'MAS', name: 'Chennai Central' },
        destinationStation: { code: 'TPJ', name: 'Tiruchchirappalli Junction' },
        status: 'ACTIVE',
        totalSeats: 480,
        currentVersion: 1
      },
      {
        id: 3,
        trainNumber: '12637',
        trainName: 'Pandian Superfast Express',
        trainType: 'SUPERFAST',
        sourceStation: { code: 'MS', name: 'Chennai Egmore' },
        destinationStation: { code: 'MDU', name: 'Madurai Junction' },
        status: 'ACTIVE',
        totalSeats: 540,
        currentVersion: 1
      }
    ],
    routes: {
      1: [
        { id: 1, stopSequence: 1, station: { code: 'MS', name: 'Chennai Egmore' }, arrivalTime: null, departureTime: '13:50', haltMinutes: 0, distanceKm: 0 },
        { id: 2, stopSequence: 2, station: { code: 'TBM', name: 'Tambaram' }, arrivalTime: '14:18', departureTime: '14:20', haltMinutes: 2, distanceKm: 25 },
        { id: 3, stopSequence: 3, station: { code: 'CGL', name: 'Chengalpattu' }, arrivalTime: '14:48', departureTime: '14:50', haltMinutes: 2, distanceKm: 56 },
        { id: 4, stopSequence: 4, station: { code: 'VM', name: 'Villupuram' }, arrivalTime: '16:00', departureTime: '16:05', haltMinutes: 5, distanceKm: 159 },
        { id: 5, stopSequence: 5, station: { code: 'VRI', name: 'Vriddhachalam' }, arrivalTime: '16:45', departureTime: '16:47', haltMinutes: 2, distanceKm: 213 },
        { id: 6, stopSequence: 6, station: { code: 'TPJ', name: 'Tiruchchirappalli' }, arrivalTime: '18:50', departureTime: '18:55', haltMinutes: 5, distanceKm: 336 },
        { id: 7, stopSequence: 7, station: { code: 'DG', name: 'Dindigul' }, arrivalTime: '20:00', departureTime: '20:02', haltMinutes: 2, distanceKm: 431 },
        { id: 8, stopSequence: 8, station: { code: 'MDU', name: 'Madurai Junction' }, arrivalTime: '21:15', departureTime: null, haltMinutes: 0, distanceKm: 497 }
      ]
    },
    schedules: {
      1: [
        { id: 1, departureTime: '13:50', arrivalTime: '21:15', runningDays: 'MON,TUE,WED,THU,FRI,SAT,SUN', scheduleType: 'NORMAL', effectiveFrom: '2026-01-01', effectiveUntil: '2027-12-31', active: true }
      ]
    },
    coaches: {
      1: [
        { id: 1, coachCode: 'A1', coachClass: '2A', totalSeats: 48, coachSequence: 1 },
        { id: 2, coachCode: 'B1', coachClass: '3A', totalSeats: 64, coachSequence: 2 },
        { id: 3, coachCode: 'B2', coachClass: '3A', totalSeats: 64, coachSequence: 3 },
        { id: 4, coachCode: 'S1', coachClass: 'SL', totalSeats: 72, coachSequence: 4 },
        { id: 5, coachCode: 'S2', coachClass: 'SL', totalSeats: 72, coachSequence: 5 }
      ]
    },
    fares: [
      { id: 1, train: { id: 1, trainNumber: '12635', trainName: 'Vaigai Superfast Express' }, coachClass: 'SL', baseFare: 295.0, reservationCharge: 20.0, serviceCharge: 15.0, dynamicSurcharge: 0.0, totalFare: 330.0, effectiveFrom: '2026-01-01' },
      { id: 2, train: { id: 1, trainNumber: '12635', trainName: 'Vaigai Superfast Express' }, coachClass: 'CC', baseFare: 480.0, reservationCharge: 40.0, serviceCharge: 25.0, dynamicSurcharge: 0.0, totalFare: 545.0, effectiveFrom: '2026-01-01' },
      { id: 3, train: { id: 1, trainNumber: '12635', trainName: 'Vaigai Superfast Express' }, coachClass: '3A', baseFare: 790.0, reservationCharge: 40.0, serviceCharge: 35.0, dynamicSurcharge: 0.0, totalFare: 865.0, effectiveFrom: '2026-01-01' },
      { id: 4, train: { id: 1, trainNumber: '12635', trainName: 'Vaigai Superfast Express' }, coachClass: '2A', baseFare: 1120.0, reservationCharge: 50.0, serviceCharge: 45.0, dynamicSurcharge: 0.0, totalFare: 1215.0, effectiveFrom: '2026-01-01' }
    ],
    fareRules: [
      { id: 1, ruleCode: 'FESTIVAL_PEAK_SURCHARGE', ruleName: 'Diwali & Pongal Demand Surcharge', ruleType: 'SEASONAL_SURCHARGE', modifierPercentage: 15.0, flatAdjustment: 0.0, priority: 1, active: true },
      { id: 2, ruleCode: 'AC_TATKAL_PREMIUM', ruleName: 'Tatkal Last Minute AC Premium', ruleType: 'TATKAL_PREMIUM', modifierPercentage: 30.0, flatAdjustment: 0.0, priority: 2, active: true },
      { id: 3, ruleCode: 'SR_CITIZEN_CONCESSION', ruleName: 'Senior Citizen Operational Concession', ruleType: 'CONCESSION', modifierPercentage: -20.0, flatAdjustment: 0.0, priority: 3, active: true }
    ],
    bookings: [
      { id: 1, pnr: '2847193021', trainNumber: '12635', trainName: 'Vaigai Superfast Express', travelDate: new Date().toISOString().split('T')[0], coachClass: '3A', bookingStatus: 'CONFIRMED', paymentStatus: 'SUCCESS', totalFare: 865.0, createdAt: new Date().toISOString(), passengerCount: 1, passengers: [{ passengerName: 'Ramesh Kumar', seatNumber: 'B1-18', bookingStatus: 'CONFIRMED' }] },
      { id: 2, pnr: '4920183742', trainNumber: '12635', trainName: 'Vaigai Superfast Express', travelDate: new Date().toISOString().split('T')[0], coachClass: 'SL', bookingStatus: 'CONFIRMED', paymentStatus: 'SUCCESS', totalFare: 330.0, createdAt: new Date().toISOString(), passengerCount: 1, passengers: [{ passengerName: 'Meena Sundaram', seatNumber: 'S1-22', bookingStatus: 'CONFIRMED' }] },
      { id: 3, pnr: '7103948215', trainNumber: '20607', trainName: 'Vande Bharat Express', travelDate: new Date().toISOString().split('T')[0], coachClass: 'CC', bookingStatus: 'RAC', paymentStatus: 'SUCCESS', totalFare: 980.0, createdAt: new Date().toISOString(), passengerCount: 1, passengers: [{ passengerName: 'Sanjay Verma', seatNumber: 'RAC-1', bookingStatus: 'RAC' }] }
    ],
    auditLogs: [
      { id: 1, adminUser: { username: 'superadmin', fullName: 'Chief Operations Controller (SUPER)' }, actionType: 'TRAIN_PUBLISHED', entityName: 'Train', entityId: 1, remarks: 'Train 12635 Vaigai SF Express validated and activated', createdAt: new Date(Date.now() - 3600000).toISOString() },
      { id: 2, adminUser: { username: 'fareadmin', fullName: 'Tariff & Revenue Pricing Officer' }, actionType: 'FARE_RULE_CREATED', entityName: 'FareRule', entityId: 1, remarks: 'Seasonal peak surcharge (+15%) added to active engine', createdAt: new Date(Date.now() - 7200000).toISOString() },
      { id: 3, adminUser: { username: 'opsadmin', fullName: 'Rolling Stock & Line Ops Admin' }, actionType: 'INVENTORY_GENERATED', entityName: 'SeatInventory', entityId: 1, remarks: 'Generated rolling 7-day seat allocation matrix for Train 12635', createdAt: new Date(Date.now() - 10800000).toISOString() }
    ],
    notifications: [
      { id: 1, title: 'Fleet Readiness Alert', message: 'All scheduled trains operational. 7-day rolling inventory online.', priority: 'NORMAL', read: false, createdAt: new Date().toISOString() },
      { id: 2, title: 'Occupancy Surge Notice', message: 'Train 12635 reached 84% capacity on Chennai-Madurai sector.', priority: 'HIGH', read: false, createdAt: new Date(Date.now() - 1800000).toISOString() }
    ],
    scans: []
  };

  // Helper response builder
  function jsonResponse(data, status = 200) {
    return Promise.resolve(new Response(JSON.stringify(data), {
      status,
      headers: { 'Content-Type': 'application/json' }
    }));
  }

  // Intercept window.fetch
  const originalFetch = window.fetch;
  window.fetch = async function(url, options = {}) {
    const urlStr = typeof url === 'string' ? url : url.url;
    const method = (options.method || 'GET').toUpperCase();
    const body = options.body ? JSON.parse(options.body) : {};

    // 1. Auth Login
    if (urlStr.includes('/api/auth/login')) {
      const u = db.users[body.username] || db.users.superadmin;
      return jsonResponse(u);
    }

    // 2. Public Stations
    if (urlStr.includes('/api/public/stations')) {
      return jsonResponse(db.stations);
    }

    // 3. Dashboard Metrics
    if (urlStr.includes('/api/admin/dashboard/metrics')) {
      return jsonResponse({
        activeTrains: db.trains.filter(t => t.status === 'ACTIVE').length,
        totalTrains: db.trains.length,
        todayBookings: db.bookings.length * 142,
        totalBookings: db.bookings.length * 892,
        confirmedBookings: db.bookings.length * 750,
        racCount: 48,
        waitlistCount: 24,
        overallOccupancyPercent: 82.4,
        todayRevenue: 284500.0,
        grossRevenue: 1948200.0,
        totalRefunds: 18400.0,
        trainOccupancyList: db.trains.map(t => ({
          trainNumber: t.trainNumber,
          trainName: t.trainName,
          status: t.status,
          totalSeats: t.totalSeats,
          occupancy: t.trainNumber === '12635' ? 84 : (t.trainNumber === '20607' ? 92 : 76)
        }))
      });
    }

    // 4. Trains List & Search
    if (urlStr.startsWith('/api/admin/trains') && method === 'GET') {
      if (urlStr.includes('/removal-check')) {
        return jsonResponse({
          canDeleteSafely: false,
          activeTicketsCount: db.bookings.length,
          totalImpactedRevenue: 12450.0,
          recommendation: 'Active tickets detected. Use Suspend Train with 100% automated refund workflow.'
        });
      }
      if (urlStr.includes('/versions')) {
        return jsonResponse([
          { versionNumber: 1, changeReason: 'Initial fleet commissioning', effectiveFrom: '2026-01-01', snapshotJson: '{}', createdAt: '2026-01-01T00:00:00Z' }
        ]);
      }
      return jsonResponse({
        content: db.trains,
        totalElements: db.trains.length,
        totalPages: 1
      });
    }

    // 5. Train Wizard POST
    if (urlStr.includes('/api/admin/trains/wizard') && method === 'POST') {
      const newTrain = {
        id: db.trains.length + 1,
        trainNumber: body.trainNumber,
        trainName: body.trainName,
        trainType: body.trainType,
        sourceStation: { code: body.sourceStationCode, name: body.sourceStationCode },
        destinationStation: { code: body.destinationStationCode, name: body.destinationStationCode },
        status: 'ACTIVE',
        totalSeats: 360,
        currentVersion: 1
      };
      db.trains.push(newTrain);
      db.auditLogs.unshift({
        id: db.auditLogs.length + 1,
        adminUser: db.users.superadmin,
        actionType: 'TRAIN_CREATED_WIZARD',
        entityName: 'Train',
        entityId: newTrain.id,
        remarks: `Published new train ${newTrain.trainNumber} - ${newTrain.trainName} via 8-Step Wizard`,
        createdAt: new Date().toISOString()
      });
      return jsonResponse({ message: 'Train successfully created and published via 8-Step Wizard', train: newTrain });
    }

    // 6. Suspend Train
    if (urlStr.includes('/suspend') && method === 'POST') {
      return jsonResponse({ message: 'Train suspended successfully. Automated 100% passenger refund batch initiated.' });
    }

    // 7. Routes for train
    if (urlStr.includes('/api/admin/routes/train/')) {
      const trainId = urlStr.split('/train/')[1].split('?')[0];
      return jsonResponse(db.routes[trainId] || db.routes[1]);
    }

    // 8. Schedules for train
    if (urlStr.includes('/api/admin/schedules/train/')) {
      const trainId = urlStr.split('/train/')[1].split('?')[0];
      return jsonResponse(db.schedules[trainId] || db.schedules[1]);
    }

    // 9. Coaches for train
    if (urlStr.includes('/api/admin/coaches/train/')) {
      const trainId = urlStr.split('/train/')[1].split('?')[0];
      return jsonResponse(db.coaches[trainId] || db.coaches[1]);
    }

    // 10. Seat Inventory
    if (urlStr.includes('/api/admin/coaches/inventory/')) {
      const seats = [];
      const types = ['LOWER', 'MIDDLE', 'UPPER', 'SIDE_LOWER', 'SIDE_UPPER'];
      for (let i = 1; i <= 36; i++) {
        const isBooked = [3, 7, 12, 18, 22, 29].includes(i);
        const isRac = [35, 36].includes(i);
        seats.push({
          seatNumber: `B1-${i}`,
          berthType: types[(i - 1) % types.length],
          currentStatus: isBooked ? 'CONFIRMED' : (isRac ? 'RAC' : 'AVAILABLE')
        });
      }
      return jsonResponse(seats);
    }

    // 11. Fare Calculation (Authoritative)
    if (urlStr.includes('/api/fare/calculate') && method === 'POST') {
      const baseMap = { 'SL': 295, 'CC': 480, '3A': 790, '2A': 1120 };
      const base = baseMap[body.coachClass] || 500;
      const resv = 40.0;
      const srv = 25.0;
      let dyn = 0.0;
      if (body.dynamicPricingEnabled) {
        dyn = Math.round(base * 0.15); // +15% dynamic surge simulation
      }
      const total = base + resv + srv + dyn;
      return jsonResponse({
        trainNumber: body.trainNumber || '12635',
        coachClass: body.coachClass,
        baseFare: base,
        reservationCharge: resv,
        serviceCharge: srv,
        dynamicSurcharge: dyn,
        concessionDiscount: 0.0,
        totalFare: total,
        surgePercent: body.dynamicPricingEnabled ? 15.0 : 0.0,
        breakdownText: `Base: ₹${base} + Resv: ₹${resv} + Srv: ₹${srv} + Dynamic: ₹${dyn}`
      });
    }

    // 12. Fares list
    if (urlStr.includes('/api/admin/fares') && method === 'GET') {
      if (urlStr.includes('/approvals')) {
        return jsonResponse([]);
      }
      return jsonResponse(db.fares);
    }

    // 13. Fare Rules
    if (urlStr.includes('/api/admin/fares/rules')) {
      return jsonResponse(db.fareRules);
    }

    // 14. Seat Hold (10-minute lock)
    if (urlStr.includes('/api/public/seats/hold') && method === 'POST') {
      return jsonResponse({
        success: true,
        holdToken: 'HOLD-' + Math.random().toString(36).substring(2, 10).toUpperCase(),
        expiresAt: new Date(Date.now() + 600000).toISOString(),
        expiresInSeconds: 600,
        message: 'Seats held successfully for 10 minutes.'
      });
    }

    // 15. Public Booking (Create Ticket + QR Code)
    if (urlStr.includes('/api/public/bookings') && method === 'POST') {
      const pnr = Math.floor(1000000000 + Math.random() * 9000000000).toString();
      const newBooking = {
        id: db.bookings.length + 1,
        pnr: pnr,
        trainNumber: body.trainNumber || '12635',
        trainName: 'Vaigai Superfast Express',
        travelDate: body.journeyDate || new Date().toISOString().split('T')[0],
        coachClass: body.coachClass || '3A',
        bookingStatus: 'CONFIRMED',
        paymentStatus: 'SUCCESS',
        totalFare: body.totalFare || 865.0,
        createdAt: new Date().toISOString(),
        passengerCount: (body.passengers || []).length || 1,
        qrToken: 'RAILSETU:' + pnr + ':VALID',
        passengers: (body.passengers || [{ passengerName: 'V Chetan', berthPreference: 'LOWER' }]).map((p, idx) => ({
          passengerName: p.passengerName,
          seatNumber: `B1-${25 + idx}`,
          bookingStatus: 'CONFIRMED'
        }))
      };
      db.bookings.unshift(newBooking);
      return jsonResponse(newBooking);
    }

    // 16. Bookings List & Cancellation
    if (urlStr.includes('/api/admin/bookings') || urlStr.includes('/api/public/bookings')) {
      if (urlStr.includes('/pnr/')) {
        const pnr = urlStr.split('/pnr/')[1].split('?')[0];
        const b = db.bookings.find(x => x.pnr === pnr) || db.bookings[0];
        return jsonResponse(b);
      }
      return jsonResponse({
        content: db.bookings,
        totalElements: db.bookings.length,
        totalPages: 1
      });
    }

    // 17. Cancellations
    if (urlStr.includes('/api/admin/cancellations/') && method === 'POST') {
      const pnr = urlStr.split('/cancellations/')[1].split('?')[0];
      const b = db.bookings.find(x => x.pnr === pnr);
      if (b) b.bookingStatus = 'CANCELLED';
      db.auditLogs.unshift({
        id: db.auditLogs.length + 1,
        adminUser: db.users.bookingadmin,
        actionType: 'BOOKING_CANCELLED',
        entityName: 'Booking',
        entityId: b ? b.id : 1,
        remarks: `PNR ${pnr} cancelled. RAC-1 passenger promoted to CONFIRMED.`,
        createdAt: new Date().toISOString()
      });
      return jsonResponse({
        message: `Booking ${pnr} cancelled successfully. Auto-refund initiated and RAC waitlist cascaded.`,
        refundAmount: b ? b.totalFare * 0.8 : 650.0
      });
    }

    // 18. RAC / WL Queues
    if (urlStr.includes('/api/admin/rac-wl')) {
      return jsonResponse({
        racQueue: [
          { priorityNumber: 1, pnr: '7103948215', passengerName: 'Sanjay Verma', travelDate: new Date().toISOString().split('T')[0], coachClass: 'CC' },
          { priorityNumber: 2, pnr: '9920148201', passengerName: 'Priya R', travelDate: new Date().toISOString().split('T')[0], coachClass: '3A' }
        ],
        waitlistQueue: [
          { priorityNumber: 1, pnr: '8839201948', passengerName: 'Karthik N', travelDate: new Date().toISOString().split('T')[0], coachClass: 'SL' },
          { priorityNumber: 2, pnr: '6620194820', passengerName: 'Ananya S', travelDate: new Date().toISOString().split('T')[0], coachClass: 'SL' }
        ]
      });
    }

    // 19. QR / PNR Verification
    if (urlStr.includes('/api/admin/verification/verify') && method === 'POST') {
      const val = body.qrToken || body.pnr || '12635';
      const scanItem = {
        id: db.scans.length + 1,
        verifierName: 'Senior Traveling Ticket Examiner (TTE)',
        pnr: val.replace('RAILSETU:', '').replace(':VALID', ''),
        trainNumber: '12635',
        coachCode: 'B1',
        seatNumber: 'B1-18',
        result: 'VALID',
        remarks: 'Berth occupied by authorized passenger with verified ID.',
        verifiedAt: new Date().toISOString()
      };
      db.scans.unshift(scanItem);
      return jsonResponse({
        valid: true,
        passengerName: 'Ramesh Kumar',
        pnr: scanItem.pnr,
        trainNumber: '12635',
        trainName: 'Vaigai Superfast Express',
        coachCode: 'B1',
        seatNumber: 'B1-18',
        bookingStatus: 'CONFIRMED',
        message: 'PASSENGER VERIFIED & AUTHORIZED'
      });
    }

    // 20. Verification Scan History
    if (urlStr.includes('/api/admin/verification/history')) {
      return jsonResponse(db.scans);
    }

    // 21. Audit Logs
    if (urlStr.includes('/api/admin/audit-logs')) {
      return jsonResponse({
        content: db.auditLogs,
        totalElements: db.auditLogs.length,
        totalPages: 1
      });
    }

    // 22. Notifications
    if (urlStr.includes('/api/admin/notifications')) {
      return jsonResponse(db.notifications);
    }

    // Fallback: try original fetch or return empty json
    try {
      return await originalFetch(url, options);
    } catch (e) {
      console.warn('Mock fallback for', urlStr);
      return jsonResponse({});
    }
  };
})();
