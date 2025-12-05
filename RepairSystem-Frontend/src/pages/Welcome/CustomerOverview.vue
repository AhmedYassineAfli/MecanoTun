<template>
<div class="overview-page">
    <div class="dashboard-grid">
        <!-- Welcome Section -->
        <div class="welcome-section glass-card">
            <div class="welcome-content">
                <h1>Hello, <span class="text-highlight">{{customer.name}}</span></h1>
                <p>Welcome back to your garage. Here's what's happening today.</p>
            </div>
            <div class="date-display">
                <span class="today-date">{{ new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
            </div>
        </div>

        <!-- Main Content Area -->
        <div class="main-content">
            <!-- Appointments Card -->
            <div class="appointments-card glass-card">
                <div class="card-header">
                    <h2><span class="icon">📅</span> My Appointments</h2>
                    <div class="header-controls">
                        <select v-model="sortBy" class="sort-select">
                            <option value="date-asc">Date (Earliest)</option>
                            <option value="date-desc">Date (Latest)</option>
                            <option value="status">Status</option>
                        </select>
                        <button v-if="selectedDate" @click="selectedDate = null" class="btn-text">
                            Clear Date Filter
                        </button>
                    </div>
                </div>
                
                <div class="appointments-list" v-if="filteredAppointments.length > 0">
                    <div v-for="appointment in filteredAppointments" :key="appointment.id" class="appointment-item">
                        <div class="app-date-box" v-if="appointment.timeSlot && appointment.timeSlot.startTime">
                            <span class="app-day">{{ getDay(appointment.timeSlot.startTime) }}</span>
                            <span class="app-month">{{ getMonth(appointment.timeSlot.startTime) }}</span>
                        </div>
                        <div class="app-date-box no-date" v-else>
                            <span class="app-day">--</span>
                            <span class="app-month">--</span>
                        </div>
                        <div class="app-info">
                            <div class="app-service" v-if="appointment.services && appointment.services.length > 0">
                                {{ appointment.services[0].serviceType || 'Service' }}
                            </div>
                            <div class="app-service" v-else>Service Info Unavailable</div>
                            <div class="app-details">
                                <span class="app-car" v-if="appointment.car">🚗 {{ appointment.car.brand }} {{ appointment.car.model }}</span>
                                <span class="app-car" v-else>🚗 Car info unavailable</span>
                                
                                <span class="app-time" v-if="appointment.timeSlot && appointment.timeSlot.startTime">
                                    ⏰ {{ formatTime(appointment.timeSlot.startTime) }} - {{ formatTime(appointment.timeSlot.endTime) }}
                                </span>
                                <span class="app-time" v-else>⏰ Time not scheduled</span>
                            </div>
                        </div>
                        <div class="app-meta">
                            <div class="app-price" v-if="appointment.services && appointment.services.length > 0 && appointment.services[0].price">
                                {{ appointment.services[0].price }} DT
                            </div>
                            <span class="status-badge" :class="appointment.status ? appointment.status.toLowerCase() : ''">
                                {{ appointment.status || 'Unknown' }}
                            </span>
                            <button v-if="appointment.status === 'Confirmed'" class="btn-icon-chat" @click="openChat(appointment)" title="Chat with Mechanic">
                                💬
                            </button>
                            <button v-if="appointment.status === 'Completed' && !appointment.hasRating" class="btn-rate" @click="openRatingModal(appointment)" title="Rate Mechanic">
                                ⭐ Rate
                            </button>
                        </div>
                        <div class="app-mechanic" v-if="appointment.mechanics && appointment.mechanics.length > 0">
                            <span class="mechanic-label">Mechanic:</span>
                            {{ appointment.mechanics[0].username || appointment.mechanics[0].name || 'Assigned' }}
                        </div>
                        <button class="btn-icon-delete" @click="removeApp(appointment.id)" title="Cancel Appointment">
                            ✕
                        </button>
                    </div>
                </div>
                
                <div v-else class="empty-state">
                    <div class="empty-icon">📅</div>
                    <p v-if="selectedDate">No appointments on {{ formatDateFull(selectedDate) }}</p>
                    <p v-else>No upcoming appointments.</p>
                    <router-link :to="'/customerDashboard/bookAppointment/' + customer.id" class="btn btn-sm btn-primary">Book Now</router-link>
                </div>
            </div>
        </div>

        <!-- Sidebar / Right Column -->
        <div class="side-content">
            <!-- Profile Card -->
            <div class="profile-card glass-card">
                <div class="profile-header">
                    <div class="profile-avatar">
                        {{ customer.name ? customer.name.charAt(0).toUpperCase() : 'U' }}
                    </div>
                    <h3>{{ customer.name }}</h3>
                    <p class="customer-id">Customer ID: #{{ customer.id }}</p>
                </div>
                <div class="profile-stats">
                    <div class="stat-item">
                        <span class="stat-value">{{ appointments.length }}</span>
                        <span class="stat-label">Total Appts</span>
                    </div>
                </div>
            </div>

            <!-- Custom Calendar Widget -->
            <div class="calendar-widget glass-card">
                <div class="calendar-header">
                    <button @click="changeMonth(-1)" class="btn-icon-small">‹</button>
                    <h3>{{ currentMonthName }} {{ currentYear }}</h3>
                    <button @click="changeMonth(1)" class="btn-icon-small">›</button>
                </div>
                <div class="calendar-grid">
                    <div class="weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
                    <div 
                        v-for="(day, index) in calendarDays" 
                        :key="index" 
                        class="calendar-day"
                        :class="{ 
                            'empty': !day, 
                            'has-appointment': day && hasAppointment(day),
                            'selected': day && isSelected(day),
                            'today': day && isToday(day)
                        }"
                        @click="day && selectDate(day)"
                    >
                        {{ day ? day.getDate() : '' }}
                        <div v-if="day && hasAppointment(day)" class="dot"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <ChatWindow 
      v-for="(chatMechanic, index) in activeChats"
      :key="chatMechanic.id"
      :partnerId="chatMechanic.id" 
      :currentUserId="customer.id"
      :partnerName="chatMechanic.name"
      :initialPosition="getChatPosition(index)"
      @close="closeChat(chatMechanic.id)" 
    />
    
    <!-- Rating Modal -->
    <div v-if="showRatingModal" class="modal-overlay" @click.self="closeRatingModal">
        <div class="rating-modal glass-card">
            <div class="modal-header">
                <h2>Rate Your Experience</h2>
                <button class="btn-close" @click="closeRatingModal">✕</button>
            </div>
            
            <div class="modal-body">
                <div class="mechanic-info" v-if="ratingAppointment">
                    <div class="mechanic-avatar">
                        {{ getMechanicInitial() }}
                    </div>
                    <div>
                        <h3>{{ getMechanicName() }}</h3>
                        <p class="service-name">{{ ratingAppointment.services && ratingAppointment.services[0] ? ratingAppointment.services[0].serviceType : 'Service' }}</p>
                    </div>
                </div>
                
                <div class="rating-section">
                    <p class="rating-label">How was your experience?</p>
                    <StarRating 
                        :rating="selectedRating" 
                        :interactive="true" 
                        size="large"
                        @rating-selected="selectedRating = $event"
                    />
                </div>
                
                <div class="comment-section">
                    <label>Additional Comments (Optional)</label>
                    <textarea 
                        v-model="ratingComment" 
                        placeholder="Share your experience with this mechanic..."
                        maxlength="500"
                        rows="4"
                    ></textarea>
                    <span class="char-count">{{ ratingComment.length }}/500</span>
                </div>
            </div>
            
            <div class="modal-footer">
                <button class="btn btn-secondary" @click="closeRatingModal">Skip</button>
                <button class="btn btn-primary" @click="submitRating" :disabled="selectedRating === 0 || submittingRating">
                    {{ submittingRating ? 'Submitting...' : 'Submit Rating' }}
                </button>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import axios from 'axios'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: {
        'Access-Control-Allow-Origin': frontendUrl
    }
})

import ChatWindow from '../../components/ChatWindow.vue';
import StarRating from '../../components/StarRating.vue';

export default {
    components: { ChatWindow, StarRating },
    data() {
        return {
            customer: "",
            appointments: [],
            error: "",
            sortBy: 'date-asc',
            selectedDate: null,
            currentDate: new Date(),
            weekdays: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
            activeChats: [],
            showRatingModal: false,
            ratingAppointment: null,
            selectedRating: 0,
            ratingComment: '',
            submittingRating: false
        }
    },
    computed: {
        currentMonthName() {
            return this.currentDate.toLocaleString('default', { month: 'long' });
        },
        currentYear() {
            return this.currentDate.getFullYear();
        },
        calendarDays() {
            const year = this.currentDate.getFullYear();
            const month = this.currentDate.getMonth();
            const firstDay = new Date(year, month, 1);
            const lastDay = new Date(year, month + 1, 0);
            
            const days = [];
            
            // Padding for empty days at start
            for (let i = 0; i < firstDay.getDay(); i++) {
                days.push(null);
            }
            
            // Actual days
            for (let i = 1; i <= lastDay.getDate(); i++) {
                days.push(new Date(year, month, i));
            }
            
            return days;
        },
        filteredAppointments() {
            let result = [...this.appointments];
            
            // Filter by date
            if (this.selectedDate) {
                result = result.filter(app => {
                    const appDate = new Date(app.timeSlot.startTime);
                    return appDate.getDate() === this.selectedDate.getDate() &&
                           appDate.getMonth() === this.selectedDate.getMonth() &&
                           appDate.getFullYear() === this.selectedDate.getFullYear();
                });
            }
            
            // Sort
            result.sort((a, b) => {
                const dateA = new Date(a.timeSlot.startTime);
                const dateB = new Date(b.timeSlot.startTime);
                
                if (this.sortBy === 'date-asc') return dateA - dateB;
                if (this.sortBy === 'date-desc') return dateB - dateA;
                if (this.sortBy === 'status') return a.status.localeCompare(b.status);
                return 0;
            });
            
            return result;
        }
    },
    created: function () {
        var id = this.$route.params.userId
        AXIOS.get('/customer/'.concat(id))
            .then(response => {
                this.customer = response.data
                this.fetchAppointments();
            })
            .catch(e => {
                this.error = e
                console.log(e)
            })
    },
    methods: {
        fetchAppointments() {
            AXIOS.get('/appointments/'.concat(this.$route.params.userId))
                .then(response => {
                    this.appointments = response.data
                })
                .catch(e => {
                    this.error = e
                    console.log(e)
                })
        },
        removeApp: function(appointmentId){
            if(!confirm("Are you sure you want to cancel this appointment?")) return;
            
            AXIOS.delete('/appointment/'.concat(appointmentId)).
                then(() => {
                    const index = this.appointments.findIndex(a => a.id === appointmentId);
                    if (index !== -1) {
                        this.appointments.splice(index, 1);
                    }
                })
                .catch(e => {
                    this.error = e
                })
        },
        formatTime(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        },
        getDay(dateString) {
            return new Date(dateString).getDate();
        },
        getMonth(dateString) {
            return new Date(dateString).toLocaleString('default', { month: 'short' });
        },
        formatDateFull(date) {
            return date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' });
        },
        changeMonth(delta) {
            this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + delta, 1);
        },
        hasAppointment(date) {
            return this.appointments.some(app => {
                const appDate = new Date(app.timeSlot.startTime);
                return appDate.getDate() === date.getDate() &&
                       appDate.getMonth() === date.getMonth() &&
                       appDate.getFullYear() === date.getFullYear();
            });
        },
        isSelected(date) {
            return this.selectedDate && 
                   date.getDate() === this.selectedDate.getDate() &&
                   date.getMonth() === this.selectedDate.getMonth() &&
                   date.getFullYear() === this.selectedDate.getFullYear();
        },
        isToday(date) {
            const today = new Date();
            return date.getDate() === today.getDate() &&
                   date.getMonth() === today.getMonth() &&
                   date.getFullYear() === today.getFullYear();
        },
        selectDate(date) {
            if (this.isSelected(date)) {
                this.selectedDate = null; // Deselect
            } else {
                this.selectedDate = date;
            }
        },
        openChat(app) {
            if (app.mechanics && app.mechanics.length > 0) {
                const mechanic = app.mechanics[0];
                if (!this.activeChats.find(c => c.id === mechanic.id)) {
                    this.activeChats.push(mechanic);
                }
            }
        },
        closeChat(mechanicId) {
            this.activeChats = this.activeChats.filter(c => c.id !== mechanicId);
        },
        getChatPosition(index) {
            const baseX = window.innerWidth ? (window.innerWidth - 350) / 2 : 500;
            const baseY = window.innerHeight ? (window.innerHeight - 500) / 2 : 200;
            const offset = index * 40;
            return { x: baseX + offset, y: baseY + offset };
        },
        openRatingModal(appointment) {
            this.ratingAppointment = appointment;
            this.selectedRating = 0;
            this.ratingComment = '';
            this.showRatingModal = true;
        },
        closeRatingModal() {
            this.showRatingModal = false;
            this.ratingAppointment = null;
            this.selectedRating = 0;
            this.ratingComment = '';
        },
        async submitRating() {
            if (this.selectedRating === 0 || !this.ratingAppointment) return;
            
            this.submittingRating = true;
            
            try {
                const mechanicId = this.ratingAppointment.mechanics && this.ratingAppointment.mechanics[0] ? this.ratingAppointment.mechanics[0].id : null;
                
                if (!mechanicId) {
                    alert('Unable to find mechanic information');
                    return;
                }
                
                await AXIOS.post('/rating', null, {
                    params: {
                        customerId: this.customer.id,
                        mechanicId: mechanicId,
                        appointmentId: this.ratingAppointment.id,
                        stars: this.selectedRating,
                        comment: this.ratingComment || null
                    }
                });
                
                // Mark appointment as rated
                const index = this.appointments.findIndex(a => a.id === this.ratingAppointment.id);
                if (index !== -1) {
                    this.appointments[index].hasRating = true;
                }
                
                this.closeRatingModal();
                alert('Thank you for your rating!');
            } catch (error) {
                console.error('Error submitting rating:', error);
                const errorMessage = error.response && error.response.data ? error.response.data : 'Failed to submit rating. Please try again.';
                alert(errorMessage);
            } finally {
                this.submittingRating = false;
            }
        },
        getMechanicName() {
            if (!this.ratingAppointment) return 'Mechanic';
            if (this.ratingAppointment.mechanics && this.ratingAppointment.mechanics[0]) {
                return this.ratingAppointment.mechanics[0].name || this.ratingAppointment.mechanics[0].username || 'Mechanic';
            }
            return 'Mechanic';
        },
        getMechanicInitial() {
            const name = this.getMechanicName();
            return name.charAt(0).toUpperCase();
        }
    }
}
</script>

<style scoped>
.overview-page {
    padding: 2rem;
    height: 100%;
    overflow-y: auto;
}

.dashboard-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    grid-template-rows: auto 1fr;
    gap: 1.5rem;
    max-width: 1400px;
    margin: 0 auto;
}

.welcome-section {
    grid-column: 1 / -1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2rem;
    background: linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(15, 23, 42, 0.9) 100%);
}

.welcome-content h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.text-highlight {
    color: var(--primary);
}

.today-date {
    font-size: 1.1rem;
    color: var(--text-secondary);
    font-weight: 500;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.card-header h2 {
    font-size: 1.5rem;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.header-controls {
    display: flex;
    gap: 1rem;
    align-items: center;
}

.sort-select {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    color: white;
    padding: 0.25rem 1rem;
    height: 32px;
}

.sort-select option {
    background: #2d3748;
}

.btn-text {
    background: none;
    border: none;
    color: var(--primary);
    cursor: pointer;
    font-size: 0.9rem;
}

.btn-text:hover {
    text-decoration: underline;
}

/* Appointments List */
.appointment-item {
    display: grid;
    grid-template-columns: auto 2fr 1fr 1fr auto;
    align-items: center;
    gap: 1.5rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.03);
    border-radius: var(--radius-md);
    margin-bottom: 1rem;
    transition: background 0.2s;
}

.appointment-item:hover {
    background: rgba(255, 255, 255, 0.05);
}

.app-date-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.05);
    padding: 0.5rem;
    border-radius: 8px;
    min-width: 60px;
}

.app-day {
    font-size: 1.5rem;
    font-weight: bold;
    line-height: 1;
}

.app-month {
    font-size: 0.8rem;
    text-transform: uppercase;
    color: var(--text-secondary);
}

.app-service {
    font-weight: 600;
    font-size: 1.1rem;
    color: var(--text-primary);
}

.app-details {
    font-size: 0.9rem;
    color: var(--text-secondary);
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
}

.app-meta {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 0.5rem;
}

.app-price {
    font-weight: bold;
    color: var(--primary);
}

.status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
}

.status-badge.booked { background: rgba(16, 185, 129, 0.2); color: #10b981; }
.status-badge.pending { background: rgba(245, 158, 11, 0.2); color: #f59e0b; }
.status-badge.cancelled { background: rgba(239, 68, 68, 0.2); color: #ef4444; }

.app-mechanic {
    font-size: 0.9rem;
    color: var(--text-secondary);
    display: flex;
    flex-direction: column;
    align-items: center;
}

.mechanic-label {
    font-size: 0.7rem;
    text-transform: uppercase;
    opacity: 0.7;
}

.btn-icon-delete {
    background: none;
    border: none;
    color: var(--text-muted);
    font-size: 1.2rem;
    cursor: pointer;
    padding: 0.5rem;
    transition: color 0.2s;
}

.btn-icon-delete:hover {
    color: var(--error);
}

.empty-state {
    text-align: center;
    padding: 3rem;
    color: var(--text-secondary);
}

.empty-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
    opacity: 0.5;
}

/* Profile Card */
.profile-card {
    text-align: center;
    padding: 2rem;
    margin-bottom: 1.5rem;
}

.profile-avatar {
    width: 80px;
    height: 80px;
    background: var(--primary);
    color: #000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0 auto 1rem;
}

.customer-id {
    font-size: 0.9rem;
    color: var(--text-muted);
}

.profile-stats {
    display: flex;
    justify-content: center;
    gap: 2rem;
    margin-top: 1.5rem;
    padding-top: 1.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-item {
    display: flex;
    flex-direction: column;
}

.stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--text-primary);
}

.stat-label {
    font-size: 0.85rem;
    color: var(--text-secondary);
}

/* Calendar Widget */
.calendar-widget {
    padding: 1.5rem;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.calendar-header h3 {
    margin: 0;
    font-size: 1.1rem;
}

.btn-icon-small {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    color: white;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-icon-small:hover {
    background: var(--primary);
}

.calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 5px;
    text-align: center;
}

.weekday {
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin-bottom: 0.5rem;
}

.calendar-day {
    aspect-ratio: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    font-size: 0.9rem;
    border-radius: 50%;
    cursor: pointer;
    position: relative;
    transition: all 0.2s;
}

.calendar-day:not(.empty):hover {
    background: rgba(255, 255, 255, 0.1);
}

.calendar-day.selected {
    background: var(--primary);
    color: white;
}

.calendar-day.today {
    border: 1px solid var(--primary);
}

.calendar-day.has-appointment .dot {
    width: 4px;
    height: 4px;
    background: var(--primary);
    border-radius: 50%;
    position: absolute;
    bottom: 4px;
}

.calendar-day.selected .dot {
    background: white;
}

.btn-rate {
    background: linear-gradient(135deg, #F3BE35, #FFA000);
    border: none;
    color: #000;
    padding: 0.4rem 0.8rem;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.btn-rate:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(243, 190, 53, 0.4);
}

/* Rating Modal */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(5px);
}

.rating-modal {
    width: 90%;
    max-width: 500px;
    padding: 0;
    animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
    from {
        opacity: 0;
        transform: translateY(-50px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h2 {
    margin: 0;
    font-size: 1.5rem;
}

.btn-close {
    background: none;
    border: none;
    color: var(--text-secondary);
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0.5rem;
    transition: color 0.2s;
}

.btn-close:hover {
    color: var(--text-primary);
}

.modal-body {
    padding: 2rem;
}

.mechanic-info {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-md);
}

.mechanic-avatar {
    width: 60px;
    height: 60px;
    background: var(--primary);
    color: #000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    font-weight: 700;
}

.mechanic-info h3 {
    margin: 0 0 0.25rem 0;
    font-size: 1.2rem;
}

.service-name {
    margin: 0;
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.rating-section {
    text-align: center;
    margin-bottom: 2rem;
}

.rating-label {
    font-size: 1.1rem;
    margin-bottom: 1rem;
    color: var(--text-primary);
}

.comment-section {
    margin-bottom: 1rem;
}

.comment-section label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.comment-section textarea {
    width: 100%;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    color: var(--text-primary);
    padding: 0.75rem;
    font-family: inherit;
    resize: vertical;
}

.comment-section textarea:focus {
    outline: none;
    border-color: var(--primary);
}

.char-count {
    display: block;
    text-align: right;
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin-top: 0.25rem;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    padding: 1.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-icon-chat {
    background: rgba(59, 130, 246, 0.2);
    border: 1px solid rgba(59, 130, 246, 0.4);
    color: #3b82f6;
    padding: 0.4rem 0.8rem;
    border-radius: 20px;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn-icon-chat:hover {
    background: rgba(59, 130, 246, 0.3);
    transform: translateY(-2px);
}

/* Responsive */
@media (max-width: 900px) {
    .dashboard-grid {
        grid-template-columns: 1fr;
    }
}
</style>
