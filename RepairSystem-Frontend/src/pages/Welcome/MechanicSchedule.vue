<template>
<div class="schedule-page">
    <div class="schedule-container glass-card">
        <div class="schedule-header">
            <h1>My Schedule</h1>
            <p>Manage your working hours. Locked slots have booked appointments.</p>
        </div>

        <div v-if="loading" class="loading-state">Loading schedule...</div>
        
        <div v-else class="schedule-content">
            <!-- Day of Week Headers -->
            <div class="schedule-grid">
                <div class="time-column">
                    <div class="header-cell">Time</div>
                    <div v-for="hour in hours" :key="hour" class="time-cell">
                        {{ formatHour(hour) }}
                    </div>
                </div>

                <div v-for="day in days" :key="day.value" class="day-column">
                    <div class="header-cell">{{ day.name }}</div>
                    <div 
                        v-for="hour in hours" 
                        :key="hour"
                        class="slot-cell"
                        :class="getSlotClass(day.value, hour)"
                        @click="toggleSlot(day.value, hour)"
                        :title="getSlotTooltip(day.value, hour)"
                    >
                        <span v-if="isSlotLocked(day.value, hour)" class="lock-icon">🔒</span>
                        <span v-else-if="isSlotWorking(day.value, hour)" class="check-icon">✓</span>
                    </div>
                </div>
            </div>

            <div v-if="!canSave && validationMessage" class="warning-message">{{ validationMessage }}</div>
            <div v-if="error" class="error-message">{{ error }}</div>
            <div v-if="successMessage" class="success-message">{{ successMessage }}</div>

            <div class="schedule-actions">
                <button class="btn btn-secondary" @click="resetChanges" :disabled="!hasChanges">Reset</button>
                <button class="btn btn-primary" @click="saveSchedule" :disabled="!hasChanges || !canSave || saving">
                    {{ saving ? 'Saving...' : 'Save Schedule' }}
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
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    data() {
        return {
            mechanicId: null,
            mechanic: null,
            loading: true,
            saving: false,
            error: '',
            successMessage: '',
            days: [
                { name: 'Monday', value: 1 },
                { name: 'Tuesday', value: 2 },
                { name: 'Wednesday', value: 3 },
                { name: 'Thursday', value: 4 },
                { name: 'Friday', value: 5 },
                { name: 'Saturday', value: 6 },
                { name: 'Sunday', value: 7 }
            ],
            hours: [8, 9, 10, 11, 12, 13, 14, 15, 16, 17], // 8 AM to 5 PM
            currentSchedule: {}, // { dayOfWeek: { hour: true/false } }
            originalSchedule: {},
            lockedSlots: {}, // { dayOfWeek: { hour: appointmentDetails } }
        }
    },
    computed: {
        hasChanges() {
            return JSON.stringify(this.currentSchedule) !== JSON.stringify(this.originalSchedule);
        },
        canSave() {
            // Check if all locked slots are covered by current schedule
            for (const dayOfWeek in this.lockedSlots) {
                for (const hour in this.lockedSlots[dayOfWeek]) {
                    // If there's a locked slot, it must be in the current schedule
                    if (!this.currentSchedule[dayOfWeek] || !this.currentSchedule[dayOfWeek][hour]) {
                        return false;
                    }
                }
            }
            return true;
        },
        validationMessage() {
            // Build a message showing which appointments need to be covered
            const missing = [];
            for (const dayOfWeek in this.lockedSlots) {
                for (const hour in this.lockedSlots[dayOfWeek]) {
                    if (!this.currentSchedule[dayOfWeek] || !this.currentSchedule[dayOfWeek][hour]) {
                        const day = this.days.find(d => d.value == dayOfWeek);
                        const dayName = day ? day.name : 'Unknown';
                        const app = this.lockedSlots[dayOfWeek][hour];
                        missing.push(`${dayName} ${hour}:00 (${app.customer})`);
                    }
                }
            }
            if (missing.length > 0) {
                return 'You must include these appointment times: ' + missing.join(', ');
            }
            return '';
        }
    },
    created() {
        this.mechanicId = this.$route.params.userId;
        this.loadMechanicData();
    },
    methods: {
        loadMechanicData() {
            this.loading = true;
            this.error = '';
            
            AXIOS.get('/mechanic/'.concat(this.mechanicId))
                .then(response => {
                    this.mechanic = response.data;
                    this.parseSchedule();
                    this.findLockedSlots();
                    this.loading = false;
                })
                .catch(e => {
                    this.error = "Error loading schedule: " + (e.response ? e.response.data.message : e.message);
                    this.loading = false;
                });
        },
        parseSchedule() {
            // Parse mechanic's TimeSlots into a grid structure
            const schedule = {};
            this.days.forEach(day => {
                schedule[day.value] = {};
                this.hours.forEach(hour => {
                    schedule[day.value][hour] = false;
                });
            });

            console.log('Parsing schedule, mechanic data:', this.mechanic);
            console.log('TimeSlots:', this.mechanic.timeSlots);
            console.log('TimeSlots type:', typeof this.mechanic.timeSlots);
            console.log('TimeSlots is array:', Array.isArray(this.mechanic.timeSlots));
            console.log('TimeSlots length:', this.mechanic.timeSlots ? this.mechanic.timeSlots.length : 'null');

            if (this.mechanic.timeSlots && Array.isArray(this.mechanic.timeSlots) && this.mechanic.timeSlots.length > 0) {
                console.log('Processing', this.mechanic.timeSlots.length, 'time slots');
                this.mechanic.timeSlots.forEach(slot => {
                    if (!slot || !slot.startTime || !slot.endTime) {
                        console.warn('Skipping invalid slot:', slot);
                        return;
                    }
                    const startTime = new Date(slot.startTime);
                    const endTime = new Date(slot.endTime);
                    const dayOfWeek = startTime.getDay() === 0 ? 7 : startTime.getDay(); // Convert Sunday from 0 to 7
                    const startHour = startTime.getHours();
                    const endHour = endTime.getHours();

                    console.log(`Processing slot: Day ${dayOfWeek}, ${startHour}:00 - ${endHour}:00`);

                    for (let hour = startHour; hour < endHour; hour++) {
                        if (schedule[dayOfWeek] && this.hours.includes(hour)) {
                            schedule[dayOfWeek][hour] = true;
                            console.log(`  Marked ${dayOfWeek} ${hour}:00 as working`);
                        }
                    }
                });
            } else {
                console.warn('No time slots found or empty array');
            }

            console.log('Final schedule:', schedule);
            this.currentSchedule = schedule;
            this.originalSchedule = JSON.parse(JSON.stringify(schedule));
        },
        findLockedSlots() {
            // Find slots that have booked appointments
            const locked = {};
            this.days.forEach(day => {
                locked[day.value] = {};
            });

            if (this.mechanic.appointments && this.mechanic.appointments.length > 0) {
                this.mechanic.appointments.forEach(app => {
                    if (!app || !app.timeSlot || !app.timeSlot.startTime) return;
                    const startTime = new Date(app.timeSlot.startTime);
                    const dayOfWeek = startTime.getDay() === 0 ? 7 : startTime.getDay();
                    const hour = startTime.getHours();

                    if (locked[dayOfWeek] && this.hours.includes(hour)) {
                        locked[dayOfWeek][hour] = {
                            customer: app.customer.name,
                            service: app.services && app.services.length > 0 ? app.services[0].serviceType : 'Service',
                            time: startTime.toLocaleString()
                        };
                    }
                });
            }

            this.lockedSlots = locked;
        },
        isSlotWorking(dayOfWeek, hour) {
            return this.currentSchedule[dayOfWeek] && this.currentSchedule[dayOfWeek][hour];
        },
        isSlotLocked(dayOfWeek, hour) {
            return this.lockedSlots[dayOfWeek] && this.lockedSlots[dayOfWeek][hour];
        },
        getSlotClass(dayOfWeek, hour) {
            if (this.isSlotLocked(dayOfWeek, hour)) {
                return 'locked';
            }
            if (this.isSlotWorking(dayOfWeek, hour)) {
                return 'working';
            }
            return 'available';
        },
        getSlotTooltip(dayOfWeek, hour) {
            if (this.isSlotLocked(dayOfWeek, hour)) {
                const app = this.lockedSlots[dayOfWeek][hour];
                return `Locked: ${app.customer} - ${app.service}\n${app.time}`;
            }
            if (this.isSlotWorking(dayOfWeek, hour)) {
                return 'Click to remove from schedule';
            }
            return 'Click to add to schedule';
        },
        toggleSlot(dayOfWeek, hour) {
            // Don't allow toggling locked slots
            if (this.isSlotLocked(dayOfWeek, hour)) {
                this.error = 'Cannot modify this time slot - it has a booked appointment.';
                setTimeout(() => this.error = '', 3000);
                return;
            }

            this.currentSchedule[dayOfWeek][hour] = !this.currentSchedule[dayOfWeek][hour];
            this.error = '';
            this.successMessage = '';
        },
        resetChanges() {
            this.currentSchedule = JSON.parse(JSON.stringify(this.originalSchedule));
            this.error = '';
            this.successMessage = '';
        },
        saveSchedule() {
            this.saving = true;
            this.error = '';
            this.successMessage = '';

            // Convert grid back to TimeSlot format
            const timeSlots = [];
            this.days.forEach(day => {
                let startHour = null;
                let endHour = null;

                this.hours.forEach((hour, index) => {
                    const isWorking = this.currentSchedule[day.value][hour];

                    if (isWorking && startHour === null) {
                        startHour = hour;
                    }

                    if (!isWorking && startHour !== null) {
                        endHour = hour;
                        timeSlots.push({
                            dayOfWeek: day.value,
                            startHour: startHour,
                            endHour: endHour
                        });
                        startHour = null;
                    }

                    // Handle end of day
                    if (index === this.hours.length - 1 && startHour !== null) {
                        timeSlots.push({
                            dayOfWeek: day.value,
                            startHour: startHour,
                            endHour: hour + 1
                        });
                    }
                });
            });

            console.log('Saving schedule, generated timeSlots:', timeSlots);

            // Send to backend
            AXIOS.put('/mechanic/'.concat(this.mechanicId + '/timeslots'), timeSlots)
                .then(() => {
                    this.successMessage = 'Schedule updated successfully!';
                    this.saving = false;
                    // Reload data from backend to confirm changes
                    return this.loadMechanicData();
                })
                .then(() => {
                    setTimeout(() => this.successMessage = '', 3000);
                })
                .catch(e => {
                    this.error = "Error saving schedule: " + (e.response && e.response.data ? e.response.data.message : e.message);
                    this.saving = false;
                    // Reload to reset to last saved state
                    this.loadMechanicData();
                });
        },
        formatHour(hour) {
            const period = hour >= 12 ? 'PM' : 'AM';
            const displayHour = hour > 12 ? hour - 12 : (hour === 0 ? 12 : hour);
            return `${displayHour} ${period}`;
        }
    }
}
</script>

<style scoped>
.schedule-page {
    padding: 2rem;
    min-height: 100%;
}

.schedule-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 2rem;
}

.schedule-header {
    text-align: center;
    margin-bottom: 2rem;
}

.schedule-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.loading-state {
    text-align: center;
    padding: 3rem;
    color: var(--text-secondary);
}

.schedule-grid {
    display: grid;
    grid-template-columns: 100px repeat(7, 1fr);
    gap: 0.5rem;
    margin-bottom: 2rem;
    overflow-x: auto;
}

.time-column, .day-column {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.header-cell {
    background: rgba(245, 158, 11, 0.2);
    padding: 1rem 0.5rem;
    text-align: center;
    font-weight: 700;
    border-radius: var(--radius-sm);
    color: var(--primary);
}

.time-cell {
    padding: 0.75rem 0.5rem;
    text-align: right;
    font-size: 0.875rem;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 60px;
}

.slot-cell {
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    padding: 0.75rem;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60px;
    font-size: 1.25rem;
}

.slot-cell.available:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.5);
}

.slot-cell.working {
    background: rgba(34, 197, 94, 0.2);
    border-color: rgba(34, 197, 94, 0.5);
}

.slot-cell.working:hover {
    background: rgba(34, 197, 94, 0.3);
}

.slot-cell.locked {
    background: rgba(239, 68, 68, 0.2);
    border-color: rgba(239, 68, 68, 0.5);
    cursor: not-allowed;
}

.lock-icon {
    opacity: 0.7;
}

.check-icon {
    color: #22c55e;
}

.schedule-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    padding-top: 1.5rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.error-message {
    color: var(--error);
    text-align: center;
    margin: 1rem 0;
    padding: 1rem;
    background: rgba(239, 68, 68, 0.1);
    border-radius: var(--radius-sm);
}

.warning-message {
    color: #f59e0b;
    text-align: center;
    margin: 1rem 0;
    padding: 1rem;
    background: rgba(245, 158, 11, 0.1);
    border: 1px solid rgba(245, 158, 11, 0.3);
    border-radius: var(--radius-sm);
    font-weight: 500;
}

.success-message {
    color: #22c55e;
    text-align: center;
    margin: 1rem 0;
    padding: 1rem;
    background: rgba(34, 197, 94, 0.1);
    border-radius: var(--radius-sm);
}

@media (max-width: 1200px) {
    .schedule-grid {
        font-size: 0.875rem;
    }
    
    .slot-cell {
        height: 50px;
        font-size: 1rem;
    }
}
</style>
