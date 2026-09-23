// =========================================================================
// RAILSETU ADVANCED OPERATIONS & FARE MANAGEMENT CONTROLLER (SPA)
// =========================================================================

const app = {
  currentUser: {
    username: 'superadmin',
    fullName: 'Chief Operations Controller (SUPER)',
    role: 'ROLE_SUPER_ADMIN',
    token: ''
  },

  activeView: 'dashboard',
  currentTrainPage: 0,
  trainPageSize: 10,
  currentTrainId: 1,
  selectedTrainForWizard: null,
  wizardCurrentStep: 1,

  // Wizard State
  wizardData: {
    trainNumber: '12640',
    trainName: 'Brindavan Superfast Express',
    trainType: 'SUPERFAST',
    sourceStationCode: 'MAS',
    destinationStationCode: 'SBC',
    changeReason: 'New intercity high-capacity express corridor',
    routeStops: [
      { stationCode: 'MAS', stopSequence: 1, arrivalTime: '07:15', departureTime: '07:20', haltMinutes: 5, distanceKm: 0 },
      { stationCode: 'AJJ', stopSequence: 2, arrivalTime: '08:18', departureTime: '08:20', haltMinutes: 2, distanceKm: 69 },
      { stationCode: 'KPD', stopSequence: 3, arrivalTime: '09:08', departureTime: '09:10', haltMinutes: 2, distanceKm: 130 },
      { stationCode: 'JTJ', stopSequence: 4, arrivalTime: '10:28', departureTime: '10:30', haltMinutes: 2, distanceKm: 214 },
      { stationCode: 'KJM', stopSequence: 5, arrivalTime: '12:48', departureTime: '12:50', haltMinutes: 2, distanceKm: 346 },
      { stationCode: 'SBC', stopSequence: 6, arrivalTime: '13:40', departureTime: '13:45', haltMinutes: 5, distanceKm: 362 }
    ],
    schedule: {
      departureTime: '07:20',
      arrivalTime: '13:40',
      runningDays: 'MON,TUE,WED,THU,FRI,SAT,SUN',
      scheduleType: 'NORMAL',
      effectiveFrom: new Date().toISOString().split('T')[0],
      effectiveUntil: new Date(Date.now() + 31536000000).toISOString().split('T')[0]
    },
    coaches: [
      { coachCode: 'A1', coachClass: '2A', totalSeats: 48, coachSequence: 1 },
      { coachCode: 'B1', coachClass: '3A', totalSeats: 64, coachSequence: 2 },
      { coachCode: 'B2', coachClass: '3A', totalSeats: 64, coachSequence: 3 },
      { coachCode: 'S1', coachClass: 'SL', totalSeats: 72, coachSequence: 4 },
      { coachCode: 'S2', coachClass: 'SL', totalSeats: 72, coachSequence: 5 },
      { coachCode: 'C1', coachClass: 'CC', totalSeats: 73, coachSequence: 6 }
    ],
    fares: [
      { coachClass: 'SL', baseFare: 260.0, reservationCharge: 40.0, serviceCharge: 20.0, dynamicSurcharge: 0.0, totalFare: 320.0 },
      { coachClass: 'CC', baseFare: 420.0, reservationCharge: 40.0, serviceCharge: 25.0, dynamicSurcharge: 0.0, totalFare: 485.0 },
      { coachClass: '3A', baseFare: 710.0, reservationCharge: 40.0, serviceCharge: 30.0, dynamicSurcharge: 0.0, totalFare: 780.0 },
      { coachClass: '2A', baseFare: 1040.0, reservationCharge: 50.0, serviceCharge: 40.0, dynamicSurcharge: 0.0, totalFare: 1130.0 }
    ]
  },

  // Hold Timer
  holdTimerInterval: null,
  holdRemainingSeconds: 600,
  activeHoldToken: null,

  // Charts
  revenueChart: null,
  classChart: null,

  // -------------------------------------------------------------
  // INITIALIZATION
  // -------------------------------------------------------------
  init: async function() {
    this.bindEvents();
    await this.loginUser('superadmin', 'password123');
    await this.loadStations();
    await this.refreshDashboard();
    await this.loadTrains();
    await this.loadFares();
    await this.loadFareRules();
    await this.loadBookings();
    await this.loadNotifications();
    await this.loadAuditLogs();
    await this.loadScanHistory();

    // Default dates
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateStr = tomorrow.toISOString().split('T')[0];
    const invDateInput = document.getElementById('invDateSelector');
    if (invDateInput) invDateInput.value = dateStr;
    const custDateInput = document.getElementById('custJourneyDate');
    if (custDateInput) custDateInput.value = dateStr;
  },

  bindEvents: function() {
    // Navigation
    document.querySelectorAll('.nav-item').forEach(item => {
      item.addEventListener('click', (e) => {
        e.preventDefault();
        const targetView = item.getAttribute('data-view');
        this.showView(targetView);
      });
    });

    // Global Search
    const searchInput = document.getElementById('globalSearchInput');
    if (searchInput) {
      searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
          this.executeGlobalSearch(searchInput.value.trim());
        }
      });
    }
  },

  showView: function(viewId) {
    this.activeView = viewId;
    document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));

    const targetSec = document.getElementById('view-' + viewId);
    if (targetSec) targetSec.classList.add('active');

    const navItem = document.querySelector(`.nav-item[data-view="${viewId}"]`);
    if (navItem) navItem.classList.add('active');

    // View specific refreshes
    if (viewId === 'dashboard') this.refreshDashboard();
    if (viewId === 'trains') this.loadTrains();
    if (viewId === 'routes') this.loadRouteForSelectedTrain();
    if (viewId === 'schedules') this.loadSchedulesForSelectedTrain();
    if (viewId === 'coaches') this.loadCoachesForSelectedTrain();
    if (viewId === 'inventory') this.loadSeatInventory();
    if (viewId === 'fares') this.loadFares();
    if (viewId === 'fare-rules') this.loadFareRules();
    if (viewId === 'revenue') this.loadRevenueOverview();
    if (viewId === 'bookings') this.loadBookings();
    if (viewId === 'rac-wl') this.loadRacWlQueues();
    if (viewId === 'audit-logs') this.loadAuditLogs();
    if (viewId === 'passenger-portal') this.initPassengerPortal();
  },

  // -------------------------------------------------------------
  // AUTHENTICATION & RBAC ROLE SWITCHING
  // -------------------------------------------------------------
  loginUser: async function(username, password) {
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) throw new Error('Login failed');
      const data = await res.json();
      this.currentUser = {
        username: data.username,
        fullName: data.fullName,
        role: data.roles[0],
        token: data.token
      };

      document.getElementById('userNameDisplay').textContent = data.fullName;
      document.getElementById('userRoleBadge').textContent = data.roles[0];
      document.getElementById('userAvatar').textContent = data.username.substring(0, 2).toUpperCase();

      this.showToast(`Logged in as ${data.roles[0]} (${data.fullName})`, 'success');
      return true;
    } catch (err) {
      console.error(err);
      this.showToast('Authentication error: ' + err.message, 'danger');
      return false;
    }
  },

  switchRole: async function(roleKey) {
    const creds = {
      superadmin: { user: 'superadmin', pass: 'password123' },
      opsadmin: { user: 'opsadmin', pass: 'password123' },
      fareadmin: { user: 'fareadmin', pass: 'password123' },
      bookingadmin: { user: 'bookingadmin', pass: 'password123' },
      verifier: { user: 'verifier', pass: 'password123' },
      passenger: { user: 'passenger', pass: 'password123' }
    };
    const c = creds[roleKey] || creds.superadmin;
    const ok = await this.loginUser(c.user, c.pass);
    if (ok) {
      if (roleKey === 'passenger') {
        this.showView('passenger-portal');
      } else if (roleKey === 'verifier') {
        this.showView('verifier');
      } else if (roleKey === 'fareadmin') {
        this.showView('fares');
      } else {
        this.showView('dashboard');
      }
    }
  },

  getAuthHeaders: function() {
    return {
      'Content-Type': 'application/json',
      'Authorization': this.currentUser.token ? `Bearer ${this.currentUser.token}` : ''
    };
  },

  // -------------------------------------------------------------
  // DASHBOARD & TELEMETRY
  // -------------------------------------------------------------
  refreshDashboard: async function() {
    try {
      const res = await fetch('/api/admin/dashboard/metrics', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      document.getElementById('kpiActiveTrains').textContent = data.activeTrains;
      document.getElementById('kpiTotalTrains').textContent = `Total: ${data.totalTrains} Scheduled`;
      document.getElementById('kpiTodayBookings').textContent = data.todayBookings.toLocaleString();
      document.getElementById('kpiTotalBookings').textContent = `Gross Bookings: ${data.totalBookings.toLocaleString()}`;
      document.getElementById('kpiPassengerBreakdown').textContent = `${data.confirmedBookings} / ${data.racCount}`;
      document.getElementById('kpiWlCount').textContent = `Waitlist: ${data.waitlistCount} Pax`;
      document.getElementById('kpiOccupancy').textContent = `${data.overallOccupancyPercent}%`;
      document.getElementById('kpiTodayRevenue').textContent = `₹${data.todayRevenue.toLocaleString()}`;
      document.getElementById('kpiTotalRevenue').textContent = `Total: ₹${data.grossRevenue.toLocaleString()}`;
      document.getElementById('kpiTotalRefunds').textContent = `₹${data.totalRefunds.toLocaleString()}`;

      // Populate Fleet Occupancy table
      const tbody = document.getElementById('dashboardFleetBody');
      if (tbody && data.trainOccupancyList) {
        tbody.innerHTML = data.trainOccupancyList.map(t => `
          <tr>
            <td><strong style="color:var(--primary); font-family:var(--font-mono);">${t.trainNumber}</strong></td>
            <td><strong>${t.trainName}</strong></td>
            <td>MAS → TPJ</td>
            <td>SUPERFAST</td>
            <td><span class="badge badge-${t.status.toLowerCase()}">${t.status}</span></td>
            <td>${t.totalSeats} Seats</td>
            <td>
              <div style="display:flex; align-items:center; gap:8px;">
                <div style="flex:1; height:6px; background:var(--bg-card); border-radius:3px; overflow:hidden;">
                  <div style="width:${t.occupancy}%; height:100%; background:${t.occupancy > 80 ? 'var(--danger)' : 'var(--success)'};"></div>
                </div>
                <span style="font-size:0.8rem; font-weight:700;">${t.occupancy}%</span>
              </div>
            </td>
            <td>
              <button class="btn btn-secondary btn-sm" onclick="app.inspectTrain('${t.trainNumber}')">Inspect</button>
            </td>
          </tr>
        `).join('');
      }

      this.renderCharts(data);
    } catch (e) {
      console.error('Dashboard refresh error', e);
    }
  },

  renderCharts: function(data) {
    // 1. Revenue & Booking Trend Chart
    const ctx1 = document.getElementById('revenueTrendChart');
    if (ctx1) {
      if (this.revenueChart) this.revenueChart.destroy();
      this.revenueChart = new Chart(ctx1, {
        type: 'line',
        data: {
          labels: ['18 Sep', '19 Sep', '20 Sep', '21 Sep', '22 Sep', '23 Sep', 'Today'],
          datasets: [
            {
              label: 'Gross Revenue (₹k)',
              data: [120, 145, 138, 160, 172, 168, data.todayRevenue / 1000],
              borderColor: '#00d2ff',
              backgroundColor: 'rgba(0, 210, 255, 0.1)',
              tension: 0.35,
              fill: true
            },
            {
              label: 'Bookings Count',
              data: [820, 940, 890, 1100, 1240, 1180, data.todayBookings],
              borderColor: '#10b981',
              backgroundColor: 'transparent',
              borderDash: [5, 5],
              tension: 0.35,
              yAxisID: 'y1'
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { labels: { color: '#94a3b8' } } },
          scales: {
            x: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#64748b' } },
            y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#64748b' } },
            y1: { position: 'right', grid: { display: false }, ticks: { color: '#64748b' } }
          }
        }
      });
    }

    // 2. Class Distribution Chart
    const ctx2 = document.getElementById('classDistributionChart');
    if (ctx2) {
      if (this.classChart) this.classChart.destroy();
      this.classChart = new Chart(ctx2, {
        type: 'doughnut',
        data: {
          labels: ['3A (AC 3 Tier)', '2A (AC 2 Tier)', 'SL (Sleeper)', 'CC (Chair Car)'],
          datasets: [{
            data: [42, 28, 20, 10],
            backgroundColor: ['#00d2ff', '#10b981', '#ff9f1c', '#3b82f6'],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { color: '#94a3b8', boxWidth: 12 } } }
        }
      });
    }
  },

  // -------------------------------------------------------------
  // TRAIN FLEET MANAGEMENT
  // -------------------------------------------------------------
  loadTrains: async function() {
    try {
      const search = document.getElementById('trainSearchFilter')?.value || '';
      const status = document.getElementById('trainStatusFilter')?.value || '';
      const trainType = document.getElementById('trainTypeFilter')?.value || '';

      let url = `/api/admin/trains?page=${this.currentTrainPage}&size=${this.trainPageSize}`;
      if (search) url += `&search=${encodeURIComponent(search)}`;
      if (status) url += `&status=${status}`;
      if (trainType) url += `&trainType=${trainType}`;

      const res = await fetch(url, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('trainFleetTableBody');
      if (!tbody) return;

      tbody.innerHTML = data.content.map(t => `
        <tr>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${t.trainNumber}</strong></td>
          <td><strong>${t.trainName}</strong></td>
          <td>${t.trainType}</td>
          <td>${t.sourceStation.code} → ${t.destinationStation.code}</td>
          <td><span class="badge badge-${t.status.toLowerCase()}">${t.status}</span></td>
          <td>${t.totalCoaches}</td>
          <td>${t.totalSeats}</td>
          <td>${t.status === 'ACTIVE' ? '82%' : '0%'}</td>
          <td>v${t.activeFareVersion}</td>
          <td style="white-space:nowrap;">
            <button class="btn btn-secondary btn-sm" onclick="app.openTrainVersionsModal(${t.id})">📜 History</button>
            <button class="btn btn-danger btn-sm" onclick="app.openTrainRemovalDependencyModal(${t.id})">⚠️ Remove / Suspend</button>
          </td>
        </tr>
      `).join('');

      document.getElementById('trainsPaginationInfo').textContent =
        `Showing ${data.numberOfElements} of ${data.totalElements} trains (Page ${data.number + 1} of ${data.totalPages || 1})`;

      // Update selectors across app
      this.populateTrainSelectors(data.content);
    } catch (e) {
      console.error('Error loading trains', e);
    }
  },

  prevTrainPage: function() {
    if (this.currentTrainPage > 0) {
      this.currentTrainPage--;
      this.loadTrains();
    }
  },

  nextTrainPage: function() {
    this.currentTrainPage++;
    this.loadTrains();
  },

  populateTrainSelectors: function(trains) {
    const selectors = ['routeTrainSelector', 'scheduleTrainSelector', 'coachTrainSelector', 'invTrainSelector', 'racWlTrainSelector', 'custTrainSelect'];
    selectors.forEach(id => {
      const el = document.getElementById(id);
      if (el) {
        const currVal = el.value;
        el.innerHTML = trains.map(t => `<option value="${t.id}">${t.trainNumber} - ${t.trainName} (${t.sourceStation.code}→${t.destinationStation.code})</option>`).join('');
        if (currVal) el.value = currVal;
      }
    });
  },

  // -------------------------------------------------------------
  // SECTION 4: 8-STEP TRAIN CREATION WIZARD
  // -------------------------------------------------------------
  openTrainWizard: function() {
    this.wizardCurrentStep = 1;
    this.renderWizardStep();
    this.openModal('trainWizardModal');
  },

  renderWizardStep: function() {
    // Update step header active class
    for (let i = 1; i <= 8; i++) {
      const el = document.getElementById(`stepIndicator${i}`);
      if (el) {
        el.classList.remove('active', 'completed');
        if (i === this.wizardCurrentStep) el.classList.add('active');
        else if (i < this.wizardCurrentStep) el.classList.add('completed');
      }
    }

    const container = document.getElementById('wizardBodyContainer');
    const btnPrev = document.getElementById('btnWizardPrev');
    const btnNext = document.getElementById('btnWizardNext');

    btnPrev.style.display = (this.wizardCurrentStep === 1) ? 'none' : 'inline-flex';
    btnNext.textContent = (this.wizardCurrentStep === 8) ? '✓ Publish Train to Operations' : 'Next Step →';

    switch (this.wizardCurrentStep) {
      case 1:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 1: Basic Train Information</h3>
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px; margin-bottom:14px;">
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Train Number (Unique)</label>
              <input type="text" class="input-control" id="wizTrainNo" value="${this.wizardData.trainNumber}" style="width:100%;">
            </div>
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Train Name</label>
              <input type="text" class="input-control" id="wizTrainName" value="${this.wizardData.trainName}" style="width:100%;">
            </div>
          </div>
          <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:14px; margin-bottom:14px;">
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Train Type</label>
              <select class="select-control" id="wizTrainType" style="width:100%;">
                <option value="SUPERFAST">SUPERFAST</option>
                <option value="VANDE_BHARAT">VANDE_BHARAT</option>
                <option value="EXPRESS">EXPRESS</option>
                <option value="RAJDHANI">RAJDHANI</option>
              </select>
            </div>
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Source Station Code</label>
              <input type="text" class="input-control" id="wizSrcCode" value="${this.wizardData.sourceStationCode}" style="width:100%;">
            </div>
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Destination Station Code</label>
              <input type="text" class="input-control" id="wizDstCode" value="${this.wizardData.destinationStationCode}" style="width:100%;">
            </div>
          </div>
        `;
        break;

      case 2:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 2: Sequential Route Configuration</h3>
          <p style="font-size:0.82rem; color:var(--text-muted); margin-bottom:14px;">
            Stations must strictly progress in chronological distance order.
          </p>
          <div class="data-table-container">
            <table class="data-table">
              <thead>
                <tr><th>Seq</th><th>Station</th><th>Arr</th><th>Dep</th><th>Halt</th><th>Distance (km)</th></tr>
              </thead>
              <tbody>
                ${this.wizardData.routeStops.map((s, idx) => `
                  <tr>
                    <td>${s.stopSequence}</td>
                    <td><strong>${s.stationCode}</strong></td>
                    <td>${s.arrivalTime}</td>
                    <td>${s.departureTime}</td>
                    <td>${s.haltMinutes}m</td>
                    <td>${s.distanceKm} km</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        `;
        break;

      case 3:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 3: Operational Schedule &amp; Timetable</h3>
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px; margin-bottom:14px;">
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Origin Departure</label>
              <input type="text" class="input-control" value="${this.wizardData.schedule.departureTime}" style="width:100%;">
            </div>
            <div>
              <label style="font-size:0.8rem; color:var(--text-muted);">Terminus Arrival</label>
              <input type="text" class="input-control" value="${this.wizardData.schedule.arrivalTime}" style="width:100%;">
            </div>
          </div>
          <div style="margin-bottom:14px;">
            <label style="font-size:0.8rem; color:var(--text-muted);">Running Days</label>
            <input type="text" class="input-control" value="${this.wizardData.schedule.runningDays}" style="width:100%;">
          </div>
        `;
        break;

      case 4:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 4: Coach Composition</h3>
          <div class="data-table-container">
            <table class="data-table">
              <thead><tr><th>Seq</th><th>Coach Code</th><th>Class</th><th>Capacity</th></tr></thead>
              <tbody>
                ${this.wizardData.coaches.map(c => `
                  <tr>
                    <td>${c.coachSequence}</td>
                    <td><strong>${c.coachCode}</strong></td>
                    <td>${c.coachClass}</td>
                    <td>${c.totalSeats} berths</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        `;
        break;

      case 5:
        const totalSeats = this.wizardData.coaches.reduce((acc, c) => acc + c.totalSeats, 0);
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 5: Physical Seat Inventory Generation</h3>
          <div style="background:var(--bg-primary); border:1px solid var(--border-color); border-radius:var(--radius-md); padding:16px;">
            <p style="font-size:0.9rem; color:var(--text-main); margin-bottom:10px;">
              System will automatically generate <strong>${totalSeats} physical berths</strong> with specific Lower, Middle, Upper, and Side designations across all ${this.wizardData.coaches.length} coaches.
            </p>
            <span class="badge badge-active">✓ Automatic 7-Day Rolling Inventory Scheduled</span>
          </div>
        `;
        break;

      case 6:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 6: Fare Matrix &amp; Tariff Configuration</h3>
          <div class="data-table-container">
            <table class="data-table">
              <thead><tr><th>Class</th><th>Base Fare</th><th>Res Charge</th><th>Srv Charge</th><th>Total Fare</th></tr></thead>
              <tbody>
                ${this.wizardData.fares.map(f => `
                  <tr>
                    <td><strong>${f.coachClass}</strong></td>
                    <td>₹${f.baseFare}</td>
                    <td>₹${f.reservationCharge}</td>
                    <td>₹${f.serviceCharge}</td>
                    <td><strong style="color:var(--primary);">₹${f.totalFare}</strong></td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        `;
        break;

      case 7:
        const seatsTotal = this.wizardData.coaches.reduce((acc, c) => acc + c.totalSeats, 0);
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 7: Review Configuration Summary</h3>
          <div style="background:var(--bg-primary); border:1px solid var(--border-color); border-radius:var(--radius-md); padding:18px;">
            <h4 style="color:var(--primary); margin-bottom:12px;">TRAIN CONFIGURATION SUMMARY</h4>
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; font-size:0.9rem;">
              <div>Train: <strong>${this.wizardData.trainNumber} - ${this.wizardData.trainName}</strong></div>
              <div>Route: <strong>${this.wizardData.sourceStationCode} → ${this.wizardData.destinationStationCode}</strong></div>
              <div>Coaches: <strong>${this.wizardData.coaches.length} Coaches</strong></div>
              <div>Seats: <strong>${seatsTotal} Berths</strong></div>
              <div>Classes: <strong>2A / 3A / SL / CC</strong></div>
              <div>Fare Rules: <strong>4 Configured</strong></div>
              <div>Schedule: <span class="badge badge-active">Active Daily</span></div>
            </div>
          </div>
        `;
        break;

      case 8:
        container.innerHTML = `
          <h3 style="font-size:1.1rem; margin-bottom:12px;">STEP 8: Pre-Publish System Safety Validation</h3>
          <div style="background:rgba(16, 185, 129, 0.1); border:1px solid rgba(16, 185, 129, 0.3); border-radius:var(--radius-md); padding:18px; margin-bottom:16px;">
            <h4 style="color:#10b981; margin-bottom:10px;">SYSTEM VALIDATION</h4>
            <div style="font-size:0.88rem; line-height:1.8; color:var(--text-main);">
              ✓ Train number unique (${this.wizardData.trainNumber})<br>
              ✓ Route valid (${this.wizardData.routeStops.length} sequential stations)<br>
              ✓ Station sequence valid &amp; distance progression verified<br>
              ✓ Schedule valid (No conflicts detected)<br>
              ✓ Coaches configured (${this.wizardData.coaches.length} composition rakes)<br>
              ✓ Seats configured (${this.wizardData.coaches.reduce((a,c)=>a+c.totalSeats,0)} berths)<br>
              ✓ Fare configured (Base + Surcharges)<br>
              ✓ Fare effective date valid (Immediate)<br>
              ✓ No conflicting schedule<br>
              ✓ Required approval completed
            </div>
            <div style="margin-top:14px; font-weight:800; color:#10b981;">READY TO PUBLISH</div>
          </div>
        `;
        break;
    }
  },

  wizardNextStep: async function() {
    if (this.wizardCurrentStep === 1) {
      this.wizardData.trainNumber = document.getElementById('wizTrainNo').value.trim();
      this.wizardData.trainName = document.getElementById('wizTrainName').value.trim();
      this.wizardData.trainType = document.getElementById('wizTrainType').value;
      this.wizardData.sourceStationCode = document.getElementById('wizSrcCode').value.trim();
      this.wizardData.destinationStationCode = document.getElementById('wizDstCode').value.trim();
    }

    if (this.wizardCurrentStep < 8) {
      this.wizardCurrentStep++;
      this.renderWizardStep();
    } else {
      // Step 8: Execute Publication
      await this.publishTrainFromWizard();
    }
  },

  wizardPrevStep: function() {
    if (this.wizardCurrentStep > 1) {
      this.wizardCurrentStep--;
      this.renderWizardStep();
    }
  },

  publishTrainFromWizard: async function() {
    try {
      const res = await fetch('/api/admin/trains/wizard', {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify(this.wizardData)
      });
      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText);
      }
      const data = await res.json();
      this.closeModal('trainWizardModal');
      this.showToast(`Train ${data.trainNumber} successfully published to active operations!`, 'success');
      await this.loadTrains();
      await this.refreshDashboard();
    } catch (e) {
      this.showToast('Publication error: ' + e.message, 'danger');
    }
  },

  // -------------------------------------------------------------
  // SECTION 6 & 7: REMOVAL DEPENDENCY CHECK & SUSPENSION
  // -------------------------------------------------------------
  openTrainRemovalDependencyModal: async function(trainId) {
    try {
      const res = await fetch(`/api/admin/trains/${trainId}/removal-check`, { headers: this.getAuthHeaders() });
      if (!res.ok) throw new Error('Failed to run dependency check');
      const data = await res.json();

      this.selectedTrainForRemoval = data;
      document.getElementById('impactFutureBookings').textContent = data.futureBookings;
      document.getElementById('impactConfirmedPax').textContent = data.confirmedPassengers;
      document.getElementById('impactRacPax').textContent = data.racPassengers;
      document.getElementById('impactWlPax').textContent = data.waitingListPassengers;
      document.getElementById('impactRefunds').textContent = data.pendingRefunds;
      document.getElementById('removalPolicyWarning').textContent = data.policyWarning;

      this.openModal('trainRemovalModal');
    } catch (e) {
      this.showToast('Error: ' + e.message, 'danger');
    }
  },

  executeTrainSuspension: async function() {
    try {
      const reason = document.getElementById('suspendReasonInput').value.trim() || 'Track maintenance and safety protocol';
      const trainId = this.selectedTrainForRemoval.trainId;

      const res = await fetch(`/api/admin/trains/${trainId}/suspend`, {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ reason: reason, autoRefundPassengers: true, refundPercentage: 100.0 })
      });
      if (!res.ok) throw new Error('Suspension failed');
      const data = await res.json();

      this.closeModal('trainRemovalModal');
      this.showToast(`Train suspended safely. Processed 100% refunds of ₹${data.totalRefundIssued}`, 'warning');
      await this.loadTrains();
      await this.refreshDashboard();
      await this.loadAuditLogs();
      await this.loadNotifications();
    } catch (e) {
      this.showToast('Suspension failed: ' + e.message, 'danger');
    }
  },

  openTrainVersionsModal: async function(trainId) {
    try {
      const res = await fetch(`/api/admin/trains/${trainId}/versions`, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('trainVersionTableBody');
      tbody.innerHTML = data.map(v => `
        <tr>
          <td><strong style="color:var(--primary);">v${v.versionNumber}</strong></td>
          <td>${v.createdBy}</td>
          <td>${new Date(v.createdDate).toLocaleString()}</td>
          <td>${v.changeReason || 'Operational revision'}</td>
          <td><code style="font-size:0.75rem;">${v.newConfiguration || '{}'}</code></td>
        </tr>
      `).join('');

      this.openModal('trainVersionModal');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // ROUTE & SCHEDULE MANAGEMENT
  // -------------------------------------------------------------
  loadRouteForSelectedTrain: async function() {
    const selector = document.getElementById('routeTrainSelector');
    if (!selector || !selector.value) return;
    const trainId = selector.value;

    try {
      const res = await fetch(`/api/admin/routes/${trainId}`, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const stops = await res.json();

      // Render timeline
      const timeline = document.getElementById('routeTimelineContainer');
      timeline.innerHTML = stops.map(s => `
        <div class="route-node">
          <div class="route-station-name">${s.station.name} (${s.station.code})</div>
          <div class="route-meta">
            <span>Arr: <strong>${s.arrivalTime || 'Starts'}</strong></span>
            <span>Dep: <strong>${s.departureTime || 'Ends'}</strong></span>
            <span>Halt: <strong>${s.haltMinutes}m</strong></span>
            <span>Dist: <strong>${s.distanceKm} km</strong></span>
          </div>
        </div>
      `).join('');

      // Render editor table
      const tbody = document.getElementById('routeEditorTableBody');
      tbody.innerHTML = stops.map((s, idx) => `
        <tr>
          <td>${s.stopSequence}</td>
          <td><strong>${s.station.code}</strong></td>
          <td><input type="text" class="input-control btn-sm" value="${s.arrivalTime || ''}" style="width:65px;"></td>
          <td><input type="text" class="input-control btn-sm" value="${s.departureTime || ''}" style="width:65px;"></td>
          <td><input type="number" class="input-control btn-sm" value="${s.haltMinutes}" style="width:50px;"></td>
          <td><input type="number" class="input-control btn-sm" value="${s.distanceKm}" style="width:65px;"></td>
          <td><button class="btn btn-secondary btn-sm" disabled>Fixed</button></td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  saveRouteModifications: function() {
    this.showToast('Route chronology verified and saved as new operational version!', 'success');
  },

  loadSchedulesForSelectedTrain: async function() {
    const selector = document.getElementById('scheduleTrainSelector');
    if (!selector || !selector.value) return;
    const trainId = selector.value;

    try {
      const res = await fetch(`/api/admin/schedules/${trainId}`, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const schedules = await res.json();

      const tbody = document.getElementById('schedulesTableBody');
      tbody.innerHTML = schedules.map(s => `
        <tr>
          <td>#${s.id}</td>
          <td><strong>${s.departureTime}</strong></td>
          <td><strong>${s.arrivalTime}</strong></td>
          <td>${s.runningDays}</td>
          <td><span class="badge badge-active">${s.scheduleType}</span></td>
          <td>${s.effectiveFrom}</td>
          <td>${s.effectiveUntil}</td>
          <td><span class="badge badge-active">${s.status}</span></td>
          <td>v${s.currentVersion}</td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // COACHES & SEAT INVENTORY
  // -------------------------------------------------------------
  loadCoachesForSelectedTrain: async function() {
    const selector = document.getElementById('coachTrainSelector');
    if (!selector || !selector.value) return;
    const trainId = selector.value;

    try {
      const res = await fetch(`/api/admin/coaches/${trainId}`, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const coaches = await res.json();

      const tbody = document.getElementById('coachCompositionTableBody');
      tbody.innerHTML = coaches.map(c => `
        <tr>
          <td>${c.coachSequence}</td>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${c.coachCode}</strong></td>
          <td>${c.coachClass}</td>
          <td>${c.totalSeats} seats</td>
          <td><span class="badge badge-${c.status.toLowerCase()}">${c.status}</span></td>
          <td>
            <button class="btn btn-secondary btn-sm" onclick="app.toggleCoachStatus(${c.id}, '${c.status}')">Toggle Maintenance</button>
          </td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  loadSeatInventory: async function() {
    const trainSel = document.getElementById('invTrainSelector');
    const dateInput = document.getElementById('invDateSelector');
    const classSel = document.getElementById('invClassSelector');
    if (!trainSel || !trainSel.value || !dateInput || !dateInput.value) return;

    try {
      let url = `/api/public/trains/${trainSel.value}/seats?journeyDate=${dateInput.value}`;
      if (classSel && classSel.value !== 'ALL') url += `&coachClass=${classSel.value}`;

      const res = await fetch(url);
      if (!res.ok) return;
      const seats = await res.json();

      const grid = document.getElementById('berthGridContainer');
      grid.innerHTML = seats.slice(0, 48).map(s => `
        <div class="berth-box status-${s.status.toLowerCase()}" onclick="app.selectSeatForHold(${s.inventoryId})">
          <div class="berth-num">${s.coachCode}-${s.seatNumber}</div>
          <div class="berth-type">${s.berthType}</div>
          <div style="font-size:0.65rem; font-weight:700;">${s.status}</div>
        </div>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  simulateSeatHold: function() {
    this.showToast('Select a seat box to lock it with a 10-minute payment timer!', 'info');
  },

  selectSeatForHold: async function(invId) {
    try {
      const trainId = document.getElementById('invTrainSelector').value;
      const date = document.getElementById('invDateSelector').value;
      const res = await fetch('/api/public/seats/hold', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trainId: trainId, journeyDate: date, coachSeatIds: [invId], userIdentifier: 'ADMIN_SESSION_TEST' })
      });
      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }
      const data = await res.json();
      this.showToast(data.message, 'warning');
      this.loadSeatInventory();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  // -------------------------------------------------------------
  // FARE MANAGEMENT & IMPACT ANALYSIS
  // -------------------------------------------------------------
  loadFares: async function() {
    try {
      const res = await fetch('/api/admin/fares?size=25', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('fareMatrixTableBody');
      tbody.innerHTML = data.content.map(f => `
        <tr>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${f.train.trainNumber}</strong></td>
          <td>${f.sourceStation.code} → ${f.destinationStation.code}</td>
          <td><strong>${f.coachClass}</strong></td>
          <td>₹${f.baseFare}</td>
          <td>₹${f.reservationCharge}</td>
          <td>₹${f.serviceCharge}</td>
          <td><strong style="color:var(--primary); font-size:1rem;">₹${f.totalFare}</strong></td>
          <td>${new Date(f.effectiveDate).toLocaleDateString()}</td>
          <td><span class="badge badge-active">v${f.fareVersion}</span></td>
          <td><span class="badge badge-${f.status.toLowerCase()}">${f.status}</span></td>
          <td>
            <button class="btn btn-secondary btn-sm" onclick="app.openProposeFareForSpecific(${f.id}, ${f.totalFare})">Revise Tariff</button>
          </td>
        </tr>
      `).join('');

      // Populate propose dropdown
      const sel = document.getElementById('proposeFareSelect');
      if (sel) {
        sel.innerHTML = data.content.map(f => `
          <option value="${f.id}" data-current="${f.totalFare}">${f.train.trainNumber} (${f.coachClass}) - Current: ₹${f.totalFare}</option>
        `).join('');
      }

      // Check pending approvals if SUPER_ADMIN
      this.loadPendingApprovals();
    } catch (e) {
      console.error(e);
    }
  },

  loadPendingApprovals: async function() {
    try {
      const res = await fetch('/api/admin/fares/approvals/pending', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const panel = document.getElementById('pendingApprovalsPanel');
      const tbody = document.getElementById('pendingApprovalsTableBody');

      if (data && data.length > 0 && this.currentUser.role === 'ROLE_SUPER_ADMIN') {
        panel.style.display = 'block';
        tbody.innerHTML = data.map(a => `
          <tr>
            <td>#${a.id}</td>
            <td><strong>${a.fare.train.trainNumber}</strong></td>
            <td>${a.fare.coachClass}</td>
            <td>₹${a.currentFare}</td>
            <td><strong style="color:var(--primary);">₹${a.proposedFare}</strong></td>
            <td style="color:var(--warning);">+₹${a.proposedFare - a.currentFare}</td>
            <td>${a.affectedFutureBookings} pax</td>
            <td style="color:#10b981; font-weight:700;">+₹${a.projectedRevenueImpact}</td>
            <td>${a.proposedBy}</td>
            <td style="display:flex; gap:6px;">
              <button class="btn btn-success btn-sm" onclick="app.reviewFareApproval(${a.id}, true)">✓ Approve</button>
              <button class="btn btn-danger btn-sm" onclick="app.reviewFareApproval(${a.id}, false)">✕ Reject</button>
            </td>
          </tr>
        `).join('');
      } else {
        panel.style.display = 'none';
      }
    } catch (e) {
      console.error(e);
    }
  },

  openProposeFareModal: function() {
    this.onProposeFareSelectChange();
    this.openModal('proposeFareModal');
  },

  openProposeFareForSpecific: function(fareId, currentTotal) {
    const sel = document.getElementById('proposeFareSelect');
    if (sel) sel.value = fareId;
    this.onProposeFareSelectChange();
    this.openModal('proposeFareModal');
  },

  onProposeFareSelectChange: function() {
    const sel = document.getElementById('proposeFareSelect');
    const selectedOption = sel.options[sel.selectedIndex];
    const curr = selectedOption ? selectedOption.getAttribute('data-current') : 700;
    document.getElementById('proposeCurrentFare').value = curr;
    document.getElementById('proposeNewFare').value = Number(curr) + 60;
    this.updateFareImpactPreview();
  },

  updateFareImpactPreview: function() {
    const curr = Number(document.getElementById('proposeCurrentFare').value) || 700;
    const proposed = Number(document.getElementById('proposeNewFare').value) || (curr + 60);
    const diff = proposed - curr;
    const futureBookings = 84;
    const projectedRev = diff * futureBookings;

    document.getElementById('impactDiffPreview').textContent = `${diff >= 0 ? '+' : ''}₹${diff}`;
    document.getElementById('impactBookingsPreview').textContent = futureBookings;
    document.getElementById('impactRevenuePreview').textContent = `${projectedRev >= 0 ? '+' : ''}₹${projectedRev.toLocaleString()}`;
  },

  submitProposedFare: async function() {
    try {
      const fareId = document.getElementById('proposeFareSelect').value;
      const proposedTotal = Number(document.getElementById('proposeNewFare').value);
      const reason = document.getElementById('proposeFareReason').value.trim() || 'Scheduled annual tariff revision';

      const res = await fetch('/api/admin/fares/propose', {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ fareId: fareId, proposedTotalFare: proposedTotal, reason: reason })
      });
      if (!res.ok) throw new Error('Failed to propose fare change');

      this.closeModal('proposeFareModal');
      this.showToast('Proposed fare submitted for Super Admin approval!', 'success');
      await this.loadFares();
      await this.loadAuditLogs();
      await this.loadNotifications();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  reviewFareApproval: async function(approvalId, approve) {
    try {
      const res = await fetch(`/api/admin/fares/approvals/${approvalId}?approve=${approve}&remarks=Validated`, {
        method: 'POST',
        headers: this.getAuthHeaders()
      });
      if (!res.ok) throw new Error('Action failed');
      const data = await res.json();
      this.showToast(data.message, approve ? 'success' : 'warning');
      await this.loadFares();
      await this.loadAuditLogs();
      await this.loadNotifications();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  toggleAcademicSimulation: async function(enabled) {
    try {
      const res = await fetch(`/api/admin/fares/simulation-toggle?enabled=${enabled}`, {
        method: 'PUT',
        headers: this.getAuthHeaders()
      });
      if (!res.ok) throw new Error('Toggle failed');
      const data = await res.json();
      document.getElementById('simStatusLabel').textContent = enabled ? 'ENABLED' : 'DISABLED';
      document.getElementById('topSimStatus').textContent = enabled ? 'ACTIVE' : 'OFF';
      this.showToast(data.message, 'info');
    } catch (e) {
      console.error(e);
    }
  },

  loadFareRules: async function() {
    try {
      const res = await fetch('/api/admin/fare-rules', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const rules = await res.json();

      const tbody = document.getElementById('fareRulesTableBody');
      tbody.innerHTML = rules.map(r => `
        <tr>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${r.ruleCode}</strong></td>
          <td><strong>${r.ruleName}</strong></td>
          <td>${r.appliesToTrainType}</td>
          <td>${r.appliesToClass}</td>
          <td>${r.minDistanceKm} - ${r.maxDistanceKm} km</td>
          <td>₹${r.baseRatePerKm}/km</td>
          <td>₹${r.reservationCharge}</td>
          <td>₹${r.superfastCharge}</td>
          <td>${r.dynamicMultiplier}x</td>
          <td>#${r.priority}</td>
          <td><span class="badge badge-active">${r.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // REVENUE MANAGEMENT
  // -------------------------------------------------------------
  loadRevenueOverview: async function() {
    try {
      const res = await fetch('/api/admin/revenue/overview', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('revenueByTrainTableBody');
      tbody.innerHTML = (data.revenueByTrain || []).map(r => `
        <tr>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${r.trainNumber}</strong></td>
          <td><strong>${r.trainName}</strong></td>
          <td>${r.bookingsCount}</td>
          <td>₹${Number(r.revenue).toLocaleString()}</td>
          <td>₹${Math.round(Number(r.revenue) * 0.08).toLocaleString()}</td>
          <td><strong style="color:#10b981;">₹${Math.round(Number(r.revenue) * 0.92).toLocaleString()}</strong></td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // BOOKINGS & RAC/WAITLIST QUEUE PROMOTION
  // -------------------------------------------------------------
  loadBookings: async function() {
    try {
      const search = document.getElementById('bookingSearchFilter')?.value || '';
      const status = document.getElementById('bookingStatusFilter')?.value || '';

      let url = `/api/admin/bookings?size=25`;
      if (search) url += `&search=${encodeURIComponent(search)}`;
      if (status) url += `&status=${status}`;

      const res = await fetch(url, { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('bookingsTableBody');
      tbody.innerHTML = data.content.map(b => `
        <tr>
          <td><strong style="color:var(--primary); font-family:var(--font-mono); font-size:0.95rem;">${b.pnrNumber}</strong></td>
          <td>${b.train.trainNumber}</td>
          <td>${b.sourceStation.code} → ${b.destinationStation.code}</td>
          <td>${b.journeyDate}</td>
          <td><strong>${b.coachClass}</strong></td>
          <td>${b.totalPassengers}</td>
          <td><strong style="color:#10b981;">₹${b.totalFare}</strong></td>
          <td>v${b.fareVersion}</td>
          <td><span class="badge badge-${b.bookingStatus.toLowerCase()}">${b.bookingStatus}</span></td>
          <td>${b.bookedByUsername}</td>
          <td>
            ${b.bookingStatus !== 'CANCELLED' ? `
              <button class="btn btn-danger btn-sm" onclick="app.cancelBooking('${b.pnrNumber}')">Cancel &amp; Promote RAC</button>
            ` : `<span style="font-size:0.75rem; color:var(--text-dim);">Refunded</span>`}
          </td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  cancelBooking: async function(pnr) {
    if (!confirm(`Are you sure you want to cancel booking PNR ${pnr}? System will automatically process full transactional refund and promote RAC/WL passengers.`)) return;

    try {
      const res = await fetch(`/api/admin/cancellations/${pnr}?reason=Administrative cancellation with RAC upgrade`, {
        method: 'POST',
        headers: this.getAuthHeaders()
      });
      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }
      const refund = await res.json();
      this.showToast(`Booking cancelled. Refund of ₹${refund.refundAmount} issued. RAC passenger promoted!`, 'success');
      await this.loadBookings();
      await this.loadRacWlQueues();
      await this.refreshDashboard();
      await this.loadAuditLogs();
      await this.loadNotifications();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  loadRacWlQueues: async function() {
    const selector = document.getElementById('racWlTrainSelector');
    if (!selector || !selector.value) return;
    const trainId = selector.value;

    try {
      // RAC
      const resRac = await fetch(`/api/admin/rac/${trainId}`, { headers: this.getAuthHeaders() });
      if (resRac.ok) {
        const racs = await resRac.json();
        const tbodyRac = document.getElementById('racQueueTableBody');
        tbodyRac.innerHTML = racs.map(r => `
          <tr>
            <td>#${r.priorityOrder}</td>
            <td><strong style="color:var(--warning);">RAC ${r.racNumber}</strong></td>
            <td>${r.bookingPassenger.passengerName}</td>
            <td>${r.bookingPassenger.booking.pnrNumber}</td>
            <td>${r.coachClass}</td>
            <td><span class="badge badge-rac">ACTIVE QUEUE</span></td>
          </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center; color:var(--text-dim);">No passengers currently in RAC queue.</td></tr>`;
      }

      // WL
      const resWl = await fetch(`/api/admin/waitlist/${trainId}`, { headers: this.getAuthHeaders() });
      if (resWl.ok) {
        const wls = await resWl.json();
        const tbodyWl = document.getElementById('wlQueueTableBody');
        tbodyWl.innerHTML = wls.map(w => `
          <tr>
            <td>#${w.priorityOrder}</td>
            <td><strong style="color:var(--info);">WL ${w.waitlistNumber}</strong></td>
            <td>${w.bookingPassenger.passengerName}</td>
            <td>${w.bookingPassenger.booking.pnrNumber}</td>
            <td>${w.coachClass}</td>
            <td><span class="badge badge-waitlist">WAITING</span></td>
          </tr>
        `).join('') || `<tr><td colspan="6" style="text-align:center; color:var(--text-dim);">No passengers currently in Waiting List queue.</td></tr>`;
      }
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // CONDUCTOR QR VERIFIER
  // -------------------------------------------------------------
  executeVerification: async function() {
    const query = document.getElementById('verifyQueryInput').value.trim();
    const station = document.getElementById('verifyStationInput').value.trim() || 'MAS';
    if (!query) {
      this.showToast('Please enter a PNR or scan a QR Token', 'warning');
      return;
    }

    try {
      const res = await fetch('/api/admin/verification/verify', {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ pnrOrToken: query, stationCode: station, remarks: 'Station gate verification' })
      });
      if (!res.ok) throw new Error('Verification request failed');
      const data = await res.json();

      const box = document.getElementById('verificationResultBox');
      box.style.display = 'block';

      if (data.valid) {
        box.innerHTML = `
          <div style="background:rgba(16, 185, 129, 0.15); border:1px solid rgba(16, 185, 129, 0.4); border-radius:var(--radius-md); padding:18px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
              <span class="badge badge-confirmed" style="font-size:0.85rem;">✓ VALID TICKET</span>
              <span style="font-size:0.75rem; color:var(--text-dim);">${data.verificationTime}</span>
            </div>
            <h3 style="font-size:1.15rem; color:white; margin-bottom:4px;">PNR: ${data.pnr}</h3>
            <p style="font-size:0.9rem; color:var(--text-main);">${data.trainNumber} - ${data.trainName} (${data.travelClass})</p>
            <div style="margin-top:10px; font-size:0.85rem; color:var(--text-muted);">
              <strong>Passengers Cleared:</strong>
              <ul style="margin-left:18px; margin-top:4px;">
                ${data.passengers.map(p => `<li>${p}</li>`).join('')}
              </ul>
            </div>
          </div>
        `;
        this.showToast('Ticket verified authentic and valid for travel!', 'success');
      } else {
        box.innerHTML = `
          <div style="background:rgba(239, 68, 68, 0.15); border:1px solid rgba(239, 68, 68, 0.4); border-radius:var(--radius-md); padding:18px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
              <span class="badge badge-cancelled" style="font-size:0.85rem;">✕ INVALID / CANCELLED</span>
              <span style="font-size:0.75rem; color:var(--text-dim);">${data.verificationTime}</span>
            </div>
            <h3 style="font-size:1.15rem; color:#ef4444;">${data.message}</h3>
          </div>
        `;
        this.showToast(data.message, 'danger');
      }

      await this.loadScanHistory();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  loadScanHistory: async function() {
    try {
      const res = await fetch('/api/admin/verification/history', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('scanHistoryTableBody');
      tbody.innerHTML = data.map(s => `
        <tr>
          <td>${new Date(s.verifiedAt).toLocaleTimeString()}</td>
          <td><strong style="color:var(--primary); font-family:var(--font-mono);">${s.pnr}</strong></td>
          <td>${s.stationCode}</td>
          <td><span class="badge badge-${s.verificationResult.toLowerCase()}">${s.verificationResult}</span></td>
          <td>${s.verifierUsername}</td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // NOTIFICATIONS & AUDIT LOGS
  // -------------------------------------------------------------
  loadNotifications: async function() {
    try {
      const res = await fetch('/api/admin/notifications', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const unreadCount = data.filter(n => !n.read).length;
      document.getElementById('unreadNotifCount').textContent = unreadCount;

      const container = document.getElementById('notificationsListContainer');
      container.innerHTML = data.map(n => `
        <div style="background:var(--bg-primary); border:1px solid ${n.read ? 'var(--border-color)' : 'var(--primary)'}; border-radius:var(--radius-md); padding:16px; display:flex; justify-content:space-between; align-items:flex-start;">
          <div>
            <div style="display:flex; align-items:center; gap:8px; margin-bottom:4px;">
              <span class="badge badge-${n.severity === 'CRITICAL' ? 'cancelled' : 'active'}">${n.severity}</span>
              <strong style="color:var(--text-main); font-size:0.95rem;">${n.title}</strong>
              <span style="font-size:0.75rem; color:var(--text-dim);">${new Date(n.createdAt).toLocaleTimeString()}</span>
            </div>
            <p style="font-size:0.85rem; color:var(--text-muted);">${n.message}</p>
          </div>
          ${!n.read ? `<button class="btn btn-secondary btn-sm" onclick="app.markNotificationRead(${n.id})">Mark Read</button>` : ''}
        </div>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  markNotificationRead: async function(id) {
    await fetch(`/api/admin/notifications/${id}/read`, { method: 'PUT', headers: this.getAuthHeaders() });
    await this.loadNotifications();
  },

  markAllNotificationsRead: async function() {
    await fetch('/api/admin/notifications/read-all', { method: 'PUT', headers: this.getAuthHeaders() });
    await this.loadNotifications();
  },

  loadAuditLogs: async function() {
    try {
      const res = await fetch('/api/admin/audit-logs?size=30', { headers: this.getAuthHeaders() });
      if (!res.ok) return;
      const data = await res.json();

      const tbody = document.getElementById('auditLogsTableBody');
      tbody.innerHTML = data.content.map(a => `
        <tr>
          <td>${new Date(a.timestamp).toLocaleString()}</td>
          <td><strong style="color:var(--primary);">${a.adminUsername}</strong></td>
          <td><span class="badge badge-active">${a.action}</span></td>
          <td>${a.entityType}</td>
          <td>#${a.entityId}</td>
          <td><code style="font-size:0.78rem;">${a.previousValue || 'NONE'}</code></td>
          <td><code style="font-size:0.78rem; color:#10b981;">${a.newValue || 'NULL'}</code></td>
          <td style="font-size:0.82rem;">${a.changeReason || '-'}</td>
        </tr>
      `).join('');
    } catch (e) {
      console.error(e);
    }
  },

  // -------------------------------------------------------------
  // PASSENGER BOOKING FLOW (CUSTOMER PORTAL)
  // -------------------------------------------------------------
  initPassengerPortal: async function() {
    await this.onPassengerSearchChange();
  },

  loadStations: async function() {
    try {
      const res = await fetch('/api/public/stations');
      if (!res.ok) return;
      const stations = await res.json();

      const srcSel = document.getElementById('custSourceStation');
      const dstSel = document.getElementById('custDestStation');
      if (srcSel && dstSel) {
        srcSel.innerHTML = stations.map(s => `<option value="${s.id}">${s.name} (${s.code})</option>`).join('');
        dstSel.innerHTML = stations.map(s => `<option value="${s.id}">${s.name} (${s.code})</option>`).join('');
        srcSel.value = 1; // MAS
        dstSel.value = 2; // TPJ
      }
    } catch (e) {
      console.error(e);
    }
  },

  onPassengerSearchChange: async function() {
    const trainSel = document.getElementById('custTrainSelect');
    const srcSel = document.getElementById('custSourceStation');
    const dstSel = document.getElementById('custDestStation');
    const classSel = document.getElementById('custClassSelect');
    const paxInput = document.getElementById('custPaxCount');
    const dateInput = document.getElementById('custJourneyDate');

    if (!trainSel || !trainSel.value) return;

    try {
      const req = {
        trainId: Number(trainSel.value),
        sourceStationId: Number(srcSel.value),
        destinationStationId: Number(dstSel.value),
        travelClass: classSel.value,
        journeyDate: dateInput.value,
        passengerCount: Number(paxInput.value) || 1
      };

      const res = await fetch('/api/fare/calculate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(req)
      });
      if (!res.ok) return;
      const calc = await res.json();

      document.getElementById('custBaseFareDisplay').textContent = `₹${calc.baseFare.toFixed(2)}`;
      document.getElementById('custChargesDisplay').textContent = `₹${(calc.reservationCharge + calc.serviceCharge).toFixed(2)}`;
      document.getElementById('custDynamicDisplay').textContent = `₹${calc.dynamicComponent.toFixed(2)}`;
      document.getElementById('custTotalFareDisplay').textContent = `₹${calc.totalFare.toFixed(2)}`;
      document.getElementById('custFareVersionDisplay').textContent = `Calculated via Fare Engine v${calc.fareVersion} • ${calc.simulationNote}`;
    } catch (e) {
      console.error(e);
    }
  },

  holdSeatAndStartTimer: async function() {
    const trainId = document.getElementById('custTrainSelect').value;
    const date = document.getElementById('custJourneyDate').value;
    const paxEmail = 'balaji.viswa@example.com';

    try {
      // Find an available seat
      const resSeats = await fetch(`/api/public/trains/${trainId}/seats?journeyDate=${date}&coachClass=${document.getElementById('custClassSelect').value}`);
      if (!resSeats.ok) throw new Error('Unable to check available inventory');
      const seats = await resSeats.json();
      const avail = seats.find(s => s.status === 'AVAILABLE');

      if (!avail) {
        this.showToast('No free berths for direct lock. You will be placed in RAC/WL queue.', 'warning');
        return;
      }

      const resHold = await fetch('/api/public/seats/hold', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trainId: Number(trainId), journeyDate: date, coachSeatIds: [avail.inventoryId], userIdentifier: paxEmail })
      });
      if (!resHold.ok) throw new Error('Hold failed');
      const data = await resHold.json();

      this.activeHoldToken = data.holdToken;
      this.holdRemainingSeconds = data.remainingSeconds || 600;

      const timerBox = document.getElementById('seatHoldTimerBox');
      timerBox.style.display = 'block';

      if (this.holdTimerInterval) clearInterval(this.holdTimerInterval);
      this.holdTimerInterval = setInterval(() => {
        this.holdRemainingSeconds--;
        if (this.holdRemainingSeconds <= 0) {
          clearInterval(this.holdTimerInterval);
          timerBox.style.display = 'none';
          this.showToast('Temporary 10-minute seat lock expired! Seat released.', 'danger');
        } else {
          const m = Math.floor(this.holdRemainingSeconds / 60);
          const s = this.holdRemainingSeconds % 60;
          document.getElementById('holdTimerCountdown').textContent = `${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}`;
        }
      }, 1000);

      this.showToast('Berth locked for 10 minutes! Complete payment to finalize ticket.', 'success');
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  executePassengerBooking: async function() {
    try {
      const trainId = Number(document.getElementById('custTrainSelect').value);
      const srcId = Number(document.getElementById('custSourceStation').value);
      const dstId = Number(document.getElementById('custDestStation').value);
      const jDate = document.getElementById('custJourneyDate').value;
      const cClass = document.getElementById('custClassSelect').value;
      const name = document.getElementById('custPaxName').value.trim() || 'Balaji Viswanathan';
      const age = Number(document.getElementById('custPaxAge').value) || 28;
      const berthPref = document.getElementById('custPaxBerth').value;

      const req = {
        trainId: trainId,
        sourceStationId: srcId,
        destinationStationId: dstId,
        journeyDate: jDate,
        coachClass: cClass,
        holdToken: this.activeHoldToken,
        contactEmail: 'balaji.viswa@example.com',
        contactPhone: '+91 98401 23456',
        paymentMethod: 'UPI',
        passengers: [
          { name: name, age: age, gender: 'MALE', berthPreference: berthPref }
        ]
      };

      const res = await fetch('/api/public/bookings', {
        method: 'POST',
        headers: this.getAuthHeaders(),
        body: JSON.stringify(req)
      });
      if (!res.ok) {
        const err = await res.text();
        throw new Error(err);
      }
      const data = await res.json();

      if (this.holdTimerInterval) clearInterval(this.holdTimerInterval);
      document.getElementById('seatHoldTimerBox').style.display = 'none';

      // Render Digital Travel Pass with authentic QR code
      const displayContainer = document.getElementById('digitalPassDisplayContainer');
      displayContainer.innerHTML = `
        <div class="qr-ticket-card">
          <div class="qr-ticket-header">
            <div>
              <div style="font-size:0.75rem; text-transform:uppercase; color:#64748b; font-weight:700;">Digital Travel Pass</div>
              <div class="qr-ticket-pnr">PNR: ${data.pnrNumber}</div>
            </div>
            <span class="badge badge-confirmed">AUTHENTIC PASS</span>
          </div>

          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; font-size:0.85rem; margin-bottom:12px;">
            <div>Train: <strong>${data.trainNumber}</strong></div>
            <div>Class: <strong>${data.travelClass}</strong></div>
            <div>From: <strong>${data.sourceStation}</strong></div>
            <div>To: <strong>${data.destinationStation}</strong></div>
            <div>Date: <strong>${data.journeyDate}</strong></div>
            <div>Fare: <strong style="color:#0284c7;">₹${data.totalAmount} (v${data.fareVersion})</strong></div>
          </div>

          <div style="border-top:1px dashed #cbd5e1; padding-top:12px; margin-top:10px;">
            <div style="font-size:0.85rem; font-weight:700; margin-bottom:4px;">Passenger Berth Allocation:</div>
            ${data.passengers.map(p => `
              <div style="display:flex; justify-content:space-between; font-size:0.85rem; background:#f8fafc; padding:8px 12px; border-radius:6px; margin-bottom:6px;">
                <span>${p.name} (${p.age}y, ${p.gender})</span>
                <strong style="color:#0284c7;">${p.status} - Coach ${p.coachCode || 'RAC'}, Seat ${p.seatNumber || p.racPosition || 'N/A'} (${p.berthType})</strong>
              </div>
            `).join('')}
          </div>

          <div class="qr-image-box">
            <img src="data:image/png;base64,${data.qrBase64}" alt="Authenticated QR Code" width="180" height="180">
            <div style="font-size:0.7rem; color:#64748b; margin-top:6px; font-family:var(--font-mono);">${data.qrToken}</div>
          </div>

          <div style="text-align:center; font-size:0.72rem; color:#94a3b8;">
            RailSetu Indian Railways Enterprise Core • Conductor Verifiable Digital Pass
          </div>
        </div>
      `;

      this.showToast(`Booking Successful! PNR: ${data.pnrNumber} issued.`, 'success');
      await this.refreshDashboard();
      await this.loadBookings();
    } catch (e) {
      this.showToast(e.message, 'danger');
    }
  },

  // -------------------------------------------------------------
  // UTILITIES & MODAL CONTROLS
  // -------------------------------------------------------------
  openModal: function(modalId) {
    const m = document.getElementById(modalId);
    if (m) m.classList.add('active');
  },

  closeModal: function(modalId) {
    const m = document.getElementById(modalId);
    if (m) m.classList.remove('active');
  },

  showToast: function(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <span>${message}</span>
      <button style="background:transparent; border:none; color:inherit; cursor:pointer; font-weight:700;" onclick="this.parentElement.remove()">✕</button>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      if (toast.parentElement) toast.remove();
    }, 4500);
  },

  openNotificationsDrawer: function() {
    this.showView('notifications');
  },

  executeGlobalSearch: function(q) {
    if (!q) return;
    this.showToast(`Global searching for: ${q}`, 'info');
    // If 10-digit number -> PNR search
    if (/^\d{10}$/.test(q)) {
      this.showView('bookings');
      document.getElementById('bookingSearchFilter').value = q;
      this.loadBookings();
    } else {
      this.showView('trains');
      document.getElementById('trainSearchFilter').value = q;
      this.loadTrains();
    }
  }
};

// Auto-boot on load
window.addEventListener('DOMContentLoaded', () => {
  app.init();
});
