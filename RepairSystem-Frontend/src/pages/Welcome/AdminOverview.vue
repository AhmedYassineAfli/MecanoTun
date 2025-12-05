<template>
<div class="admin-overview">
    <div class="page-header">
        <h1>Upcoming Appointments</h1>
        <p>Manage schedule and assignments</p>
    </div>

    <!-- Appointments Grid -->
    <div class="appointments-grid">
        <div v-for="appointment in appointments" :key="appointment.id" class="appointment-card glass-card">
            <div class="card-header">
                <div class="status-badge" :class="appointment.status ? appointment.status.toLowerCase() : ''">
                    {{ appointment.status || 'Unknown' }}
                </div>
                <div class="time-slot" v-if="appointment.timeSlot">
                    <span class="icon">🕒</span>
                    {{ appointment.timeSlot.startTime || 'N/A' }}
                </div>
            </div>

            <div class="card-body">
                <div class="info-row" v-if="appointment.customer">
                    <span class="label">Customer:</span>
                    <span class="value">{{ getCustomerName(appointment.customer.id) }}</span>
                </div>
                <div class="info-row" v-if="appointment.services && appointment.services.length > 0">
                    <span class="label">Service:</span>
                    <span class="value">{{ appointment.services[0].serviceType || 'N/A' }}</span>
                </div>
                <div class="info-row" v-if="appointment.car">
                    <span class="label">Vehicle:</span>
                    <span class="value">{{ appointment.car.brand || 'N/A' }} {{ appointment.car.model || '' }}</span>
                </div>
                <div class="info-row" v-if="appointment.mechanics && appointment.mechanics.length > 0">
                    <span class="label">Mechanic:</span>
                    <span class="value">{{ getMechanicName(appointment.mechanics[0].id) }}</span>
                </div>
            </div>

            <div class="card-actions">
                <button class="btn btn-secondary btn-sm" @click="openEditModal(appointment)">
                    Edit
                </button>
                <button class="btn btn-danger btn-sm" @click="removeApp(appointment.id)">
                    Cancel
                </button>
            </div>
        </div>
    </div>

    <!-- Edit Modal -->
    <b-modal
        v-model="modalShow"
        title="Edit Appointment"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        centered
        hide-footer
    >
        <form @submit.prevent="saveEdit">
            <div class="form-group">
                <label>Appointment Status</label>
                <multiselect 
                    v-model="specificStatus" 
                    :options="statusOptions" 
                    placeholder="Select Status" 
                    label="name" 
                    track-by="name"
                    class="custom-multiselect"
                ></multiselect>
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" @click="modalShow = false">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import Multiselect from 'vue-multiselect'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    components: { Multiselect },
    data() {
        return {
            appointments: [],
            customers: [],
            mechanics: [],
            modalShow: false,
            selectedAppointment: null,
            specificStatus: null,
            statusOptions: [
                { name: 'Booked' },
                { name: 'In Progress' },
                { name: 'Completed' },
                { name: 'Cancelled' }
            ],
            error: ''
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            AXIOS.get('/appointment').then(response => {
                this.appointments = response.data
            }).catch(e => this.error = e)

            AXIOS.get('/customers').then(response => {
                this.customers = response.data
            }).catch(e => this.error = e)

            AXIOS.get('/mechanics').then(response => {
                this.mechanics = response.data
            }).catch(e => this.error = e)
        },
        getCustomerName(id) {
            const customer = this.customers.find(c => c.id === id);
            return customer ? customer.name : 'Unknown';
        },
        getMechanicName(id) {
            const mechanic = this.mechanics.find(m => m.id === id);
            return mechanic ? mechanic.name : 'Unassigned';
        },
        openEditModal(appointment) {
            this.selectedAppointment = appointment;
            this.specificStatus = { name: appointment.status };
            this.modalShow = true;
        },
        saveEdit() {
            if (!this.selectedAppointment || !this.specificStatus) return;
            
            // Assuming backend expects just the status string or object? 
            // Original code passed specificStatus directly. 
            // Let's assume we need to call an update endpoint.
            // The original code used editAppointment(appointment, specificStatus)
            
            // Re-implementing the logic from original JS file but cleaner
            // Note: The original JS had a weird URL structure for PUT. I'll try to match it or improve it.
            // Original: AXIOS.put('/appointment/'.concat(appointment.id + "?newStatus=" + specificStatus.name)...
            
            AXIOS.put('/appointment/'.concat(this.selectedAppointment.id + "?newStatus=" + this.specificStatus.name))
                .then(response => {
                    this.fetchData(); // Refresh data
                    this.modalShow = false;
                })
                .catch(e => {
                    console.error(e);
                    this.error = e;
                })
        },
        removeApp(id) {
            if(confirm("Are you sure you want to cancel this appointment?")) {
                AXIOS.delete('/appointment/' + id)
                    .then(() => {
                        this.fetchData();
                    })
                    .catch(e => {
                        console.error(e);
                    })
            }
        }
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>
.admin-overview {
    padding: 2rem;
}

.page-header {
    margin-bottom: 2rem;
}

.page-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.appointments-grid {
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

.status-badge {
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    background: rgba(255, 255, 255, 0.1);
}

.status-badge.booked { background: rgba(16, 185, 129, 0.2); color: #10b981; }
.status-badge.cancelled { background: rgba(239, 68, 68, 0.2); color: #ef4444; }

.time-slot {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.info-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.5rem;
    font-size: 0.95rem;
}

.info-row .label {
    color: var(--text-secondary);
}

.info-row .value {
    font-weight: 500;
    color: var(--text-primary);
}

.card-actions {
    margin-top: auto;
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
}

.btn-sm {
    padding: 0.4rem 1rem;
    font-size: 0.85rem;
}

.btn-danger {
    background: rgba(239, 68, 68, 0.1);
    color: var(--error);
    border: 1px solid rgba(239, 68, 68, 0.2);
}

.btn-danger:hover {
    background: rgba(239, 68, 68, 0.2);
}

/* Modal Styles */
.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
}
</style>
