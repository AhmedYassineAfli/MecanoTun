<template>
<div class="customers-page">
    <div class="page-header">
        <div class="header-content">
            <h1>Customer Management</h1>
            <p>View and manage your customer base</p>
        </div>
        <button class="btn btn-primary" @click="openAddModal">
            <span class="icon-plus">+</span> Add Customer
        </button>
    </div>

    <!-- Search Bar -->
    <div class="search-bar glass-card">
        <span class="search-icon">🔍</span>
        <input 
            type="text" 
            v-model="searchQuery" 
            @input="searchCustomers" 
            placeholder="Search by name, email, phone, or address..." 
            class="search-input"
        >
    </div>

    <!-- Customers Grid -->
    <div class="customers-grid">
        <div v-for="customer in customers" :key="customer.id" class="customer-card glass-card">
            <div class="card-header">
                <div class="avatar-circle">
                    {{ customer.name ? customer.name.charAt(0).toUpperCase() : 'C' }}
                </div>
                <div class="customer-info">
                    <h3>{{ customer.name }}</h3>
                    <span class="id-badge">#{{ customer.id }}</span>
                </div>
                <div class="card-actions-top">
                    <button class="btn-icon" @click="openEditModal(customer)" title="Edit Profile">✏️</button>
                    <button class="btn-icon delete" @click="removeCustomer(customer.id)" title="Remove Customer">🗑️</button>
                </div>
            </div>

            <div class="card-body">
                <div class="info-row">
                    <span class="icon">✉️</span>
                    {{ customer.email }}
                </div>
                <div class="info-row">
                    <span class="icon">📱</span>
                    {{ customer.phone }}
                </div>
                <div class="info-row">
                    <span class="icon">🏠</span>
                    {{ customer.address }}
                </div>

                <div class="cars-section">
                    <div class="section-header">
                        <span class="label">Vehicles</span>
                        <button class="btn-icon-small" @click="openAddCarModal(customer)" title="Add Car">+</button>
                    </div>
                    <div class="tags">
                        <span v-for="car in customer.cars" :key="car.id" class="tag">
                            🚗 {{ car.carType }}
                        </span>
                        <span v-if="!customer.cars || customer.cars.length === 0" class="no-data">No vehicles</span>
                    </div>
                </div>
            </div>


        </div>
    </div>

    <!-- Add/Edit Customer Modal -->
    <b-modal
        v-model="modalShow"
        :title="isEditing ? 'Edit Customer' : 'Add New Customer'"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        centered
        hide-footer
    >
        <form @submit.prevent="handleSubmit">
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" v-model="form.name" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" v-model="form.email" class="form-control" :disabled="isEditing" required>
            </div>
            <div class="form-group">
                <label>Phone</label>
                <input type="tel" v-model="form.phone" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Address</label>
                <input type="text" v-model="form.address" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" v-model="form.password" class="form-control" required>
            </div>
            
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" @click="modalShow = false">Cancel</button>
                <button type="submit" class="btn btn-primary">{{ isEditing ? 'Save Changes' : 'Create Customer' }}</button>
            </div>
        </form>
    </b-modal>

    <!-- Add Car Modal -->
    <b-modal
        v-model="modalCar"
        title="Add Vehicle"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        centered
        hide-footer
    >
        <VehicleSelector 
            @vehicle-selected="handleVehicleSelected"
            @cancel="modalCar = false"
        />
    </b-modal>

    <!-- Book Appointment Modal -->
    <b-modal
        v-model="modalApp"
        title="Book Appointment"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        size="lg"
        centered
        hide-footer
    >
        <form @submit.prevent="handleAddApp">
            <div class="form-group">
                <label>Vehicle</label>
                <multiselect 
                    v-model="appForm.car" 
                    :options="carOptions" 
                    label="carType" 
                    track-by="id"
                    placeholder="Select vehicle"
                    class="custom-multiselect"
                ></multiselect>
            </div>
            <div class="form-group">
                <label>Service</label>
                <multiselect 
                    v-model="appForm.service" 
                    :options="serviceOptions" 
                    label="serviceType" 
                    track-by="serviceType"
                    placeholder="Select service"
                    class="custom-multiselect"
                    @input="onServiceSelect"
                ></multiselect>
            </div>
            <div class="form-group">
                <label>Mechanic</label>
                <multiselect 
                    v-model="appForm.mechanic" 
                    :options="filteredMechanics" 
                    label="name" 
                    track-by="id"
                    placeholder="Select mechanic"
                    class="custom-multiselect"
                    @input="onMechanicSelect"
                ></multiselect>
            </div>
            
            <!-- Date Selection -->
            <div v-if="appForm.mechanic" class="form-group">
                <label>Select Date</label>
                <div class="date-grid-compact">
                    <div 
                        v-for="dateOption in availableDates" 
                        :key="dateOption.date"
                        class="date-option-compact"
                        :class="{ selected: appForm.selectedDate === dateOption.date }"
                        @click="selectAppDate(dateOption.date)"
                    >
                        <div class="date-day-compact">{{ dateOption.dayName }}</div>
                        <div class="date-number-compact">{{ dateOption.dayNumber }}</div>
                        <div class="date-month-compact">{{ dateOption.monthName }}</div>
                    </div>
                </div>
            </div>
            
            <!-- Time Selection -->
            <div v-if="appForm.selectedDate" class="form-group">
                <label>Select Time</label>
                <div v-if="loadingTimeSlots" class="loading-message-compact">Loading available times...</div>
                <div v-else-if="availableTimeSlots.length === 0" class="no-slots-message-compact">
                    No available time slots for this date. Please select another date.
                </div>
                <div v-else class="time-grid-compact">
                    <div 
                        v-for="slot in availableTimeSlots" 
                        :key="slot.time"
                        class="time-slot-compact"
                        :class="{ selected: appForm.selectedTime === slot.time }"
                        @click="appForm.selectedTime = slot.time"
                    >
                        {{ slot.time }}
                    </div>
                </div>
            </div>
            
            <div class="form-group">
                <label>Note</label>
                <textarea v-model="appForm.note" class="form-control" rows="2"></textarea>
            </div>
            
            <div v-if="appError" class="error-message-compact">{{ appError }}</div>
            
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" @click="modalApp = false">Cancel</button>
                <button type="submit" class="btn btn-primary" :disabled="!isAppFormValid">Book Appointment</button>
            </div>
        </form>
    </b-modal>
</div>
</template>

<script>
import axios from 'axios'
import Multiselect from 'vue-multiselect'
import VehicleSelector from '../../components/VehicleSelector.vue'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    components: { Multiselect, VehicleSelector },
    data() {
        return {
            customers: [],
            allCustomers: [],
            searchQuery: '',
            modalShow: false,
            modalCar: false,
            modalApp: false,
            isEditing: false,
            currentCustomer: null,
            
            // Forms
            form: { name: '', email: '', phone: '', address: '', password: '' },
            carForm: { type: '', km: '', winterTires: 'false' },
            appForm: { 
                car: null, 
                service: null, 
                mechanic: null, 
                selectedDate: null,
                selectedTime: null,
                note: '' 
            },
            
            // Options for Appointment
            carOptions: [],
            serviceOptions: [],
            mechanicOptions: [],
            availableDates: [],
            availableTimeSlots: [],
            loadingTimeSlots: false,
            appError: '',
            
            error: ''
        }
    },
    created() {
        this.fetchCustomers();
        this.generateAvailableDates();
    },
    computed: {
        filteredMechanics() {
            if (!this.appForm.service) return this.mechanicOptions;
            return this.mechanicOptions.filter(mech => {
                return mech.services && mech.services.some(s => s.serviceType === this.appForm.service.serviceType);
            });
        },
        isAppFormValid() {
            return this.appForm.car && 
                   this.appForm.service && 
                   this.appForm.mechanic && 
                   this.appForm.selectedDate && 
                   this.appForm.selectedTime;
        }
    },
    methods: {
        fetchCustomers() {
            AXIOS.get('/customer')
                .then(response => {
                    this.customers = response.data;
                    this.allCustomers = [...response.data]; // Create a copy
                })
                .catch(e => this.error = e);
        },
        searchCustomers() {
            const query = this.searchQuery.toLowerCase().trim();
            if (!query) {
                this.customers = [...this.allCustomers];
            } else {
                this.customers = this.allCustomers.filter(c => 
                    c.name.toLowerCase().includes(query) || 
                    c.email.toLowerCase().includes(query) || 
                    c.phone.includes(query) ||
                    c.address.toLowerCase().includes(query)
                );
            }
        },
        openAddModal() {
            this.isEditing = false;
            this.form = { name: '', email: '', phone: '', address: '', password: '' };
            this.modalShow = true;
        },
        openEditModal(customer) {
            this.isEditing = true;
            this.currentCustomer = customer;
            this.form = { ...customer }; // Copy data
            this.modalShow = true;
        },
        handleSubmit() {
            if (this.isEditing) {
                this.updateCustomer();
            } else {
                this.createCustomer();
            }
        },
        createCustomer() {
            const { name, password, phone, email, address } = this.form;
            AXIOS.post('/customer/'.concat(name), {}, {
                params: { phone, password, email, address, credit: '', debit: '' }
            }).then(() => {
                this.fetchCustomers();
                this.modalShow = false;
            }).catch(e => this.error = e);
        },
        updateCustomer() {
            const { name, password, phone, email, address } = this.form;
            // Note: API might require credit/debit params even if empty
            AXIOS.put('/customer/'.concat(email + "?newName=" + name + "&newPassword=" + password + "&newPhone=" + phone + "&newAddress=" + address + "&newCredit=&newDebit="))
                .then(() => {
                    this.fetchCustomers();
                    this.modalShow = false;
                }).catch(e => this.error = e);
        },
        removeCustomer(id) {
            if(confirm("Are you sure you want to remove this customer?")) {
                AXIOS.delete('/customer/' + id)
                    .then(() => this.fetchCustomers())
                    .catch(e => this.error = e);
            }
        },
        
        // Car Logic
        openAddCarModal(customer) {
            this.currentCustomer = customer;
            this.modalCar = true;
        },
        handleVehicleSelected(vehicleData) {
            // Create car with data from VehicleSelector
            AXIOS.post('/car/'.concat(this.currentCustomer.id), null, {
                params: {
                    carType: `${vehicleData.brand} ${vehicleData.model}`,
                    winterTires: vehicleData.winterTires,
                    numOfKilometers: vehicleData.kilometers,
                    brand: vehicleData.brand,
                    model: vehicleData.model,
                    engine: vehicleData.engine
                }
            })
                .then(() => {
                    this.fetchCustomers();
                    this.modalCar = false;
                }).catch(e => this.error = e);
        },
        
        // Appointment Logic
        generateAvailableDates() {
            const dates = [];
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            for (let i = 1; i <= 14; i++) {
                const date = new Date(today);
                date.setDate(today.getDate() + i);
                
                const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
                const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
                
                dates.push({
                    date: date.toISOString().split('T')[0],
                    dayName: dayNames[date.getDay()],
                    dayNumber: date.getDate(),
                    monthName: monthNames[date.getMonth()]
                });
            }
            
            this.availableDates = dates;
        },
        openAddAppModal(customer) {
            this.currentCustomer = customer;
            this.carOptions = customer.cars || [];
            this.appForm = { 
                car: null, 
                service: null, 
                mechanic: null, 
                selectedDate: null,
                selectedTime: null,
                note: '' 
            };
            this.availableTimeSlots = [];
            this.appError = '';
            
            // Fetch options
            AXIOS.get('/mechanics').then(r => this.mechanicOptions = r.data);
            AXIOS.get('/services').then(r => this.serviceOptions = r.data);
            
            this.modalApp = true;
        },
        onServiceSelect() {
            // Reset mechanic when service changes
            this.appForm.mechanic = null;
            this.appForm.selectedDate = null;
            this.appForm.selectedTime = null;
            this.availableTimeSlots = [];
        },
        onMechanicSelect() {
            // Reset date/time when mechanic changes
            this.appForm.selectedDate = null;
            this.appForm.selectedTime = null;
            this.availableTimeSlots = [];
        },
        selectAppDate(date) {
            this.appForm.selectedDate = date;
            this.appForm.selectedTime = null;
            this.loadAvailableTimeSlots();
        },
        loadAvailableTimeSlots() {
            if (!this.appForm.mechanic || !this.appForm.selectedDate) return;
            
            this.loadingTimeSlots = true;
            this.availableTimeSlots = [];
            
            const selectedDateObj = new Date(this.appForm.selectedDate);
            const dayOfWeek = selectedDateObj.getDay();
            
            const mechanicSlots = this.appForm.mechanic.timeSlots || [];
            const workingSlot = mechanicSlots.find(slot => {
                const slotDate = new Date(slot.startTime);
                const slotDay = slotDate.getDay();
                return slotDay === dayOfWeek;
            });
            
            if (!workingSlot) {
                this.loadingTimeSlots = false;
                return;
            }
            
            // Get mechanic's existing appointments
            AXIOS.get('/mechanic/'.concat(this.appForm.mechanic.id))
                .then(response => {
                    const mechanic = response.data;
                    const appointments = mechanic.appointments || [];
                    
                    // Filter appointments for selected date
                    const dateAppointments = appointments.filter(app => {
                        const appDate = new Date(app.timeSlot.startTime);
                        return appDate.toISOString().split('T')[0] === this.appForm.selectedDate;
                    });
                    
                    // Generate time slots
                    const slots = this.generateTimeSlots(workingSlot, dateAppointments);
                    this.availableTimeSlots = slots;
                    this.loadingTimeSlots = false;
                })
                .catch(e => {
                    console.log(e);
                    this.loadingTimeSlots = false;
                });
        },
        generateTimeSlots(workingSlot, existingAppointments) {
            const slots = [];
            const startTime = new Date(workingSlot.startTime);
            const endTime = new Date(workingSlot.endTime);
            const now = new Date();
            const minBookingTime = new Date(now.getTime() + 60 * 60 * 1000);
            
            let currentHour = startTime.getHours();
            let currentMinute = startTime.getMinutes();
            const endHour = endTime.getHours();
            
            while (currentHour < endHour) {
                const slotTime = `${String(currentHour).padStart(2, '0')}:${String(currentMinute).padStart(2, '0')}`;
                const slotDateTime = new Date(this.appForm.selectedDate + 'T' + slotTime);
                
                if (slotDateTime >= minBookingTime) {
                    const hasConflict = existingAppointments.some(app => {
                        const appStart = new Date(app.timeSlot.startTime);
                        const appEnd = new Date(app.timeSlot.endTime);
                        const bufferStart = new Date(appStart.getTime() - 60 * 60 * 1000);
                        const bufferEnd = new Date(appEnd.getTime() + 60 * 60 * 1000);
                        return slotDateTime >= bufferStart && slotDateTime < bufferEnd;
                    });
                    
                    if (!hasConflict) {
                        slots.push({ time: slotTime });
                    }
                }
                
                currentHour++;
            }
            
            return slots;
        },
        handleAddApp() {
            const { car, service, mechanic, selectedDate, selectedTime, note } = this.appForm;
            if (!car || !service || !mechanic || !selectedDate || !selectedTime) return;
            
            this.appError = '';
            
            // Format date for backend: YYYY-MM-DD-HH:mm
            const startDateTime = selectedDate + '-' + selectedTime;
            const [hours, minutes] = selectedTime.split(':');
            const endHours = (parseInt(hours) + 1).toString().padStart(2, '0');
            const endDateTime = selectedDate + '-' + endHours + ':' + minutes;

            // Create TimeSlot first
            AXIOS.post('/timeslot/'.concat(startDateTime + "?endTime=" + endDateTime))
                .then(response => {
                    const timeslot = response.data;
                    // Create Appointment
                    AXIOS.post('/appointment/'.concat(this.currentCustomer.id + "?timeSlotId=" + timeslot.id + "&carId=" + car.id + "&services=" + service.serviceType + "&note=" + note))
                        .then(appResponse => {
                            const appointment = appResponse.data;
                            // Add Mechanic
                            AXIOS.put('/appointment/addMechanic/'.concat(mechanic.id + "?appointmentId=" + appointment.id))
                                .then(() => {
                                    this.modalApp = false;
                                    alert("Appointment booked successfully!");
                                    this.fetchCustomers();
                                })
                                .catch(e => {
                                    this.appError = "Error assigning mechanic: " + (e.response ? e.response.data.message : e.message);
                                });
                        })
                        .catch(e => {
                            this.appError = "Error creating appointment: " + (e.response ? e.response.data.message : e.message);
                        });
                })
                .catch(e => {
                    this.appError = "Error creating timeslot: " + (e.response ? e.response.data.message : e.message);
                });
        }
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>
.customers-page {
    padding: 2rem;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.page-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.search-bar {
    display: flex;
    align-items: center;
    padding: 1rem;
    margin-bottom: 2rem;
    gap: 1rem;
}

.search-input {
    background: transparent;
    border: none;
    color: var(--text-primary);
    width: 100%;
    font-size: 1rem;
    outline: none;
}

.customers-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 1.5rem;
}

.customer-card {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.avatar-circle {
    width: 50px;
    height: 50px;
    background: var(--primary);
    color: #000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
    font-size: 1.2rem;
}

.customer-info h3 {
    font-size: 1.2rem;
    margin: 0;
}

.id-badge {
    font-size: 0.8rem;
    color: var(--text-secondary);
}

.card-actions-top {
    margin-left: auto;
    display: flex;
    gap: 0.5rem;
}

.btn-icon {
    background: transparent;
    border: none;
    cursor: pointer;
    font-size: 1.2rem;
    opacity: 0.7;
    transition: opacity 0.2s;
}

.btn-icon:hover {
    opacity: 1;
}

.btn-icon.delete:hover {
    filter: drop-shadow(0 0 2px red);
}

.info-row {
    display: flex;
    align-items: center;
    gap: 0.8rem;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
}

.cars-section {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.5rem;
}

.btn-icon-small {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    color: var(--primary);
    border-radius: 50%;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}

.tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.tag {
    background: rgba(255, 255, 255, 0.1);
    padding: 0.2rem 0.6rem;
    border-radius: 12px;
    font-size: 0.8rem;
}

.no-data {
    font-size: 0.8rem;
    color: var(--text-muted);
    font-style: italic;
}

.card-footer {
    margin-top: auto;
    padding-top: 1rem;
    text-align: center;
}

.btn-outline {
    width: 100%;
    border: 1px solid var(--primary);
    color: var(--primary);
    background: transparent;
}

.btn-outline:hover {
    background: var(--primary);
    color: #000;
}

/* Modal Styles */
.form-group {
    margin-bottom: 1rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
}

.form-control {
    width: 100%;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    color: var(--text-primary);
}

.input-select {
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1em;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

/* Compact Date/Time Selection Styles */
.date-grid-compact {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
    gap: 0.5rem;
    margin-bottom: 1rem;
}

.date-option-compact {
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    padding: 0.75rem 0.25rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
}

.date-option-compact:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.5);
}

.date-option-compact.selected {
    background: rgba(245, 158, 11, 0.2);
    border-color: var(--primary);
}

.date-day-compact {
    font-size: 0.7rem;
    color: var(--text-secondary);
    margin-bottom: 0.2rem;
}

.date-number-compact {
    font-size: 1.2rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 0.2rem;
}

.date-month-compact {
    font-size: 0.7rem;
    color: var(--text-secondary);
}

.time-grid-compact {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 0.5rem;
}

.time-slot-compact {
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    padding: 0.6rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
    font-weight: 600;
    font-size: 0.9rem;
}

.time-slot-compact:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.5);
}

.time-slot-compact.selected {
    background: var(--primary);
    border-color: var(--primary);
    color: #000;
}

.loading-message-compact, .no-slots-message-compact {
    text-align: center;
    padding: 1.5rem;
    color: var(--text-secondary);
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-sm);
    font-size: 0.9rem;
}

.error-message-compact {
    background: rgba(239, 68, 68, 0.1);
    border: 1px solid rgba(239, 68, 68, 0.3);
    color: #ef4444;
    padding: 0.75rem;
    border-radius: var(--radius-sm);
    margin-bottom: 1rem;
    font-size: 0.9rem;
}
</style>