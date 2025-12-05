<template>
<div class="mechanic-overview">
    <div class="welcome-section glass-panel">
        <div class="welcome-text">
            <h1>Hello, <span class="highlight">{{ mechanic.name }}</span></h1>
            <p>Here are your upcoming jobs and appointments.</p>
        </div>
        <div class="stat-card">
            <span class="stat-number">{{ appointments.length }}</span>
            <span class="stat-label">Active Jobs</span>
        </div>
    </div>

    <div class="appointments-section">
        <div class="section-header-row">
            <h2 class="section-title">My Schedule</h2>
            <div class="view-toggle">
                <button 
                    :class="['toggle-btn', { active: viewMode === 'list' }]" 
                    @click="viewMode = 'list'"
                >
                    <span>📋</span> List
                </button>
                <button 
                    :class="['toggle-btn', { active: viewMode === 'calendar' }]" 
                    @click="viewMode = 'calendar'"
                >
                    <span>📅</span> Calendar
                </button>
            </div>
        </div>

        <!-- Calendar View -->
        <div v-if="viewMode === 'calendar'" class="calendar-container glass-panel">
            <div class="calendar-header">
                <button class="nav-btn" @click="previousMonth">
                    <span>←</span>
                </button>
                <h3 class="calendar-title">{{ currentMonthName }} {{ currentYear }}</h3>
                <button class="nav-btn" @click="nextMonth">
                    <span>→</span>
                </button>
            </div>

            <div class="calendar-grid">
                <div class="calendar-day-header" v-for="day in dayNames" :key="day">
                    {{ day }}
                </div>
                <div 
                    v-for="date in calendarDates" 
                    :key="date.dateString"
                    :class="['calendar-day', {
                        'other-month': !date.isCurrentMonth,
                        'today': date.isToday,
                        'has-appointments': date.appointmentCount > 0,
                        'selected': date.dateString === selectedDate
                    }]"
                    @click="selectCalendarDate(date)"
                >
                    <span class="day-number">{{ date.day }}</span>
                    <span v-if="date.appointmentCount > 0" class="appointment-badge">
                        {{ date.appointmentCount }}
                    </span>
                </div>
            </div>

            <div v-if="selectedDate" class="selected-date-info">
                <h4>Appointments for {{ formatSelectedDate }}</h4>
                <p v-if="filteredAppointments.length === 0" class="no-appointments">No appointments</p>
            </div>
        </div>
        
        <div v-if="appointments.length === 0 && viewMode === 'list'" class="empty-state glass-panel">
            <span class="empty-icon">📅</span>
            <p>No appointments scheduled.</p>
        </div>

        <div class="appointments-grid">
            <div v-for="app in filteredAppointments" :key="app.id" class="appointment-card glass-card">
                <div class="card-header">
                    <span class="service-badge">{{ formatServiceName(app.services[0].serviceType) }}</span>
                    <span :class="['status-badge', app.status.toLowerCase()]">{{ app.status }}</span>
                </div>
                
                <div class="card-body">
                    <!-- Vehicle Details -->
                    <div class="info-section">
                        <div class="section-header">
                            <span class="icon">🚗</span>
                            <span class="section-title">Vehicle</span>
                        </div>
                        <div class="car-type-display">
                            <span class="car-type-value" v-if="app.car">
                                {{ app.car.brand }} {{ app.car.model }} {{ app.car.engine }}
                            </span>
                            <span class="car-type-value" v-else>
                                Vehicle information not available
                            </span>
                        </div>
                    </div>

                    <!-- Time Details -->
                    <div class="info-section">
                        <div class="section-header">
                            <span class="icon">🕒</span>
                            <span class="section-title">Time</span>
                        </div>
                        <div class="details-grid" v-if="app.timeSlot && app.timeSlot.startTime">
                            <div class="detail-item">
                                <span class="detail-label">Date</span>
                                <span class="detail-value">{{ formatDate(app.timeSlot.startTime) }}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Time</span>
                                <span class="detail-value">{{ formatTimeOnly(app.timeSlot.startTime) }}</span>
                            </div>
                        </div>
                        <div class="no-data-message" v-else>
                            Time not scheduled
                        </div>
                    </div>

                    <!-- Customer Details -->
                    <div class="info-section">
                        <div class="section-header">
                            <span class="icon">👤</span>
                            <span class="section-title">Customer</span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-value" v-if="app.customer">{{ getCustomerName(app.customer.id) }}</span>
                            <span class="detail-value" v-else>Unknown Customer</span>
                        </div>
                    </div>

                    <!-- Customer Notes -->
                    <div v-if="app.note" class="info-section notes-section">
                        <div class="section-header">
                            <span class="icon">📝</span>
                            <span class="section-title">Notes</span>
                        </div>
                        <p class="note-text">{{ app.note }}</p>
                    </div>
                </div>

                <div class="card-footer">
                    <div v-if="app.status === 'AppointmentBooked'" class="action-buttons">
                        <button class="btn btn-success btn-sm" @click="confirmAppointment(app)">Confirm</button>
                        <button class="btn btn-danger btn-sm" @click="rejectAppointment(app)">Reject</button>
                    </div>
                    <div v-else-if="app.status !== 'Completed' && app.status !== 'Rejected'" class="action-buttons">
                         <button class="btn btn-info btn-sm" @click="openChat(app)">Chat</button>
                         <button class="btn btn-primary btn-sm" @click="openStatusModal(app)">Update Status</button>
                    </div>
                    <button v-else class="btn btn-primary btn-sm" @click="openStatusModal(app)">
                        Update Status
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Status Update Modal -->
    <b-modal
        v-model="modalShow"
        title="Update Job Status"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        centered
        hide-footer
    >
        <div class="status-form">
            <label>Select New Status</label>
            <multiselect 
                v-model="specificStatus" 
                :options="statusOptions" 
                label="name" 
                track-by="name"
                placeholder="Select status"
                class="custom-multiselect"
            ></multiselect>
            
            <div class="modal-actions">
                <button class="btn btn-secondary" @click="modalShow = false">Cancel</button>
                <button class="btn btn-primary" @click="updateStatus">Save Update</button>
            </div>
        </div>
    </b-modal>

    <ChatWindow 
      v-for="(chatCustomer, index) in activeChats"
      :key="chatCustomer.id"
      :partnerId="chatCustomer.id" 
      :currentUserId="mechanic.id"
      :partnerName="chatCustomer.name"
      :initialPosition="getChatPosition(index)"
      @close="closeChat(chatCustomer.id)" 
    />
</div>
</template>

<script>
import axios from 'axios';
import Multiselect from 'vue-multiselect'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

import ChatWindow from '../../components/ChatWindow.vue';

export default {
    components: { Multiselect, ChatWindow },
    data() {
        return {
            mechanic: "",
            appointments: [],
            customers: [],
            modalShow: false,
            currentApp: null,
            specificStatus: null,
            statusOptions: [
                {name: "AppointmentBooked"},
                {name: "Confirmed"},
                {name: "Rejected"},
                {name: "CarReceived"},
                {name: "InRepair"},
                {name: "Completed"}
            ],
            error: "",
            activeChats: [],
            viewMode: 'list', // 'list' or 'calendar'
            currentMonth: new Date().getMonth(),
            currentYear: new Date().getFullYear(),
            selectedDate: null,
            dayNames: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
        }
    },
    computed: {
        currentMonthName() {
            const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                'July', 'August', 'September', 'October', 'November', 'December'];
            return monthNames[this.currentMonth];
        },
        calendarDates() {
            const dates = [];
            const firstDay = new Date(this.currentYear, this.currentMonth, 1);
            const lastDay = new Date(this.currentYear, this.currentMonth + 1, 0);
            const startDay = firstDay.getDay();
            const daysInMonth = lastDay.getDate();
            
            // Previous month days
            const prevMonthLastDay = new Date(this.currentYear, this.currentMonth, 0).getDate();
            for (let i = startDay - 1; i >= 0; i--) {
                const day = prevMonthLastDay - i;
                const dateObj = new Date(this.currentYear, this.currentMonth - 1, day);
                dates.push(this.createDateObject(dateObj, false));
            }
            
            // Current month days
            for (let day = 1; day <= daysInMonth; day++) {
                const dateObj = new Date(this.currentYear, this.currentMonth, day);
                dates.push(this.createDateObject(dateObj, true));
            }
            
            // Next month days to fill grid
            const remainingDays = 42 - dates.length; // 6 rows * 7 days
            for (let day = 1; day <= remainingDays; day++) {
                const dateObj = new Date(this.currentYear, this.currentMonth + 1, day);
                dates.push(this.createDateObject(dateObj, false));
            }
            
            return dates;
        },
        filteredAppointments() {
            if (!this.selectedDate) {
                return this.appointments;
            }
            return this.appointments.filter(app => {
                if (!app.timeSlot || !app.timeSlot.startTime) return false;
                const appDate = new Date(app.timeSlot.startTime);
                const year = appDate.getFullYear();
                const month = String(appDate.getMonth() + 1).padStart(2, '0');
                const day = String(appDate.getDate()).padStart(2, '0');
                const appDateString = `${year}-${month}-${day}`;
                return appDateString === this.selectedDate;
            });
        },
        formatSelectedDate() {
            if (!this.selectedDate) return '';
            const [year, month, day] = this.selectedDate.split('-');
            const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
            return date.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            var id = this.$route.params.userId
            
            // Fetch Mechanic Profile
            AXIOS.get('/mechanic/'.concat(id))
                .then(response => {
                    this.mechanic = response.data
                })
                .catch(e => console.log(e));

            // Fetch Customers (for name lookup)
            AXIOS.get('/customer/')
                .then(response => {
                    this.customers = response.data
                })
                .catch(e => console.log(e));

            // Fetch Appointments
            AXIOS.get('/appointment/')
                .then(response => {
                    this.appointments = response.data.filter(app => 
                        app.mechanics && app.mechanics.length > 0 && app.mechanics[0].id.toString() === id
                    );
                })
                .catch(e => console.log(e));
        },
        getCustomerName(id) {
            const customer = this.customers.find(c => c.id === id);
            return customer ? customer.name : 'Unknown';
        },
        formatTime(timeString) {
            if (!timeString) return '';
            return new Date(timeString).toLocaleString();
        },
        openStatusModal(app) {
            this.currentApp = app;
            this.specificStatus = { name: app.status };
            this.modalShow = true;
        },
        updateStatus() {
            if (!this.currentApp || !this.specificStatus) return;
            
            AXIOS.put('/appointment/editAppointment/'.concat(this.currentApp.id + "?status=" + this.specificStatus.name))
                .then(() => {
                    this.fetchData(); // Refresh data
                    this.modalShow = false;
                })
                .catch(e => this.error = e);
        },
        confirmAppointment(app) {
            AXIOS.put('/appointment/editAppointment/'.concat(app.id + "?status=Confirmed"))
                .then(() => {
                    this.fetchData();
                })
                .catch(e => this.error = e);
        },
        rejectAppointment(app) {
             AXIOS.put('/appointment/editAppointment/'.concat(app.id + "?status=Rejected"))
                .then(() => {
                    this.fetchData();
                })
                .catch(e => this.error = e);
        },
        openChat(app) {
            if (!this.activeChats.find(c => c.id === app.customer.id)) {
                this.activeChats.push(app.customer);
            }
        },
        closeChat(customerId) {
            this.activeChats = this.activeChats.filter(c => c.id !== customerId);
        },
        getChatPosition(index) {
            const baseX = window.innerWidth ? (window.innerWidth - 350) / 2 : 500;
            const baseY = window.innerHeight ? (window.innerHeight - 500) / 2 : 200;
            const offset = index * 40;
            return { x: baseX + offset, y: baseY + offset };
        },
        createDateObject(dateObj, isCurrentMonth) {
            const year = dateObj.getFullYear();
            const month = String(dateObj.getMonth() + 1).padStart(2, '0');
            const day = String(dateObj.getDate()).padStart(2, '0');
            const dateString = `${year}-${month}-${day}`;
            
            const today = new Date();
            const isToday = dateObj.toDateString() === today.toDateString();
            
            const appointmentCount = this.appointments.filter(app => {
                if (!app.timeSlot || !app.timeSlot.startTime) return false;
                const appDate = new Date(app.timeSlot.startTime);
                return appDate.toDateString() === dateObj.toDateString();
            }).length;
            
            return {
                day: dateObj.getDate(),
                dateString,
                isCurrentMonth,
                isToday,
                appointmentCount
            };
        },
        previousMonth() {
            if (this.currentMonth === 0) {
                this.currentMonth = 11;
                this.currentYear--;
            } else {
                this.currentMonth--;
            }
        },
        nextMonth() {
            if (this.currentMonth === 11) {
                this.currentMonth = 0;
                this.currentYear++;
            } else {
                this.currentMonth++;
            }
        },
        selectCalendarDate(date) {
            if (!date.isCurrentMonth) return;
            this.selectedDate = date.dateString;
        },
        formatDate(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
        },
        formatTimeOnly(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
        },
        formatServiceName(name) {
            if (!name) return '';
            return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
        }
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>
.mechanic-overview {
    padding: 2rem;
    max-width: 1200px;
    margin: 0 auto;
}

.welcome-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2rem;
    margin-bottom: 3rem;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
}

.welcome-text h1 {
    font-size: 2.5rem;
    margin-bottom: 0.5rem;
}

.highlight {
    color: var(--primary);
}

.stat-card {
    text-align: center;
    background: rgba(243, 190, 53, 0.1);
    padding: 1rem 2rem;
    border-radius: var(--radius-md);
    border: 1px solid rgba(243, 190, 53, 0.3);
}

.stat-number {
    display: block;
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--primary);
}

.stat-label {
    font-size: 0.9rem;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 1px;
}

.section-title {
    font-size: 1.5rem;
    margin: 0;
    border-left: 4px solid var(--primary);
    padding-left: 1rem;
}

.section-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.view-toggle {
    display: flex;
    gap: 0.5rem;
    background: rgba(255, 255, 255, 0.05);
    padding: 0.25rem;
    border-radius: var(--radius-sm);
}

.toggle-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    background: transparent;
    border: none;
    color: var(--text-secondary);
    border-radius: var(--radius-sm);
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 0.9rem;
}

.toggle-btn:hover {
    background: rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
}

.toggle-btn.active {
    background: var(--primary);
    color: #000;
    font-weight: 600;
}

/* Calendar Styles */
.calendar-container {
    padding: 1.5rem;
    margin-bottom: 2rem;
    max-width: 800px;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.calendar-title {
    font-size: 1.5rem;
    margin: 0;
}

.nav-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: var(--text-primary);
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 1.2rem;
}

.nav-btn:hover {
    background: rgba(243, 190, 53, 0.2);
    border-color: var(--primary);
}

.calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 0.5rem;
}

.calendar-day-header {
    text-align: center;
    padding: 0.5rem;
    font-weight: 600;
    color: var(--text-secondary);
    font-size: 0.85rem;
}

.calendar-day {
    aspect-ratio: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 0.25rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    font-size: 0.9rem;
}

.calendar-day:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: scale(1.05);
}

.calendar-day.other-month {
    opacity: 0.3;
    cursor: default;
}

.calendar-day.other-month:hover {
    transform: none;
}

.calendar-day.today {
    border-color: var(--primary);
    background: rgba(243, 190, 53, 0.1);
}

.calendar-day.has-appointments {
    background: rgba(52, 152, 219, 0.2);
    border-color: #3498db;
}

.calendar-day.selected {
    background: var(--primary);
    color: #000;
    font-weight: 700;
}

.day-number {
    font-size: 1rem;
}

.appointment-badge {
    position: absolute;
    top: 4px;
    right: 4px;
    background: var(--primary);
    color: #000;
    font-size: 0.7rem;
    font-weight: 700;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.selected-date-info {
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.selected-date-info h4 {
    margin-bottom: 1rem;
    color: var(--primary);
}

.no-appointments {
    color: var(--text-secondary);
    font-style: italic;
}

/* Enhanced Card Styles */.appointments-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
}

.appointment-card {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.service-badge {
    font-weight: 600;
    font-size: 1.1rem;
}

.status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 600;
}

.status-badge.appointmentbooked { background: rgba(52, 152, 219, 0.2); color: #3498db; }
.status-badge.carreceived { background: rgba(241, 196, 15, 0.2); color: #f1c40f; }
.status-badge.inrepair { background: rgba(230, 126, 34, 0.2); color: #e67e22; }
.status-badge.completed { background: rgba(46, 204, 113, 0.2); color: #2ecc71; }

.info-section {
    margin-bottom: 1.5rem;
}

.section-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.75rem;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
    font-weight: 600;
    font-size: 0.9rem;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.details-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
}

.detail-item {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.detail-label {
    font-size: 0.75rem;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.detail-value {
    font-size: 1rem;
    color: var(--text-primary);
    font-weight: 500;
}

.notes-section {
    background: rgba(255, 255, 255, 0.03);
    padding: 1rem;
    border-radius: var(--radius-sm);
    border-left: 3px solid var(--primary);
}

.note-text {
    margin: 0;
    color: var(--text-primary);
    line-height: 1.6;
    font-size: 0.95rem;
}

.car-type-display {
    margin-top: 0.5rem;
}

.car-type-value {
    font-size: 1rem;
    color: var(--text-primary);
    font-weight: 500;
}

.info-row {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 0.8rem;
}

.icon {
    font-size: 1.2rem;
    width: 30px;
    text-align: center;
    opacity: 0.7;
}

.info-content {
    display: flex;
    flex-direction: column;
}

.label {
    font-size: 0.75rem;
    color: var(--text-secondary);
}

.value {
    font-size: 1rem;
}

.card-footer {
    margin-top: auto;
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    text-align: right;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

.empty-state {
    text-align: center;
    padding: 3rem;
    color: var(--text-secondary);
}

.empty-icon {
    font-size: 3rem;
    display: block;
    margin-bottom: 1rem;
    opacity: 0.5;
}
</style>
