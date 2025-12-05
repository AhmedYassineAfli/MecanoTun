<template>
<div class="booking-page">
    <div class="booking-container glass-card">
        <div class="booking-header">
            <h1>Book Appointment</h1>
            <p>Schedule your next service with our certified experts.</p>
        </div>

        <!-- Progress Steps -->
        <div class="steps-indicator">
            <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
                <div class="step-circle">1</div>
                <span>Vehicle</span>
            </div>
            <div class="line" :class="{ active: currentStep > 1 }"></div>
            <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
                <div class="step-circle">2</div>
                <span>Service</span>
            </div>
            <div class="line" :class="{ active: currentStep > 2 }"></div>
            <div class="step" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
                <div class="step-circle">3</div>
                <span>Mechanic</span>
            </div>
            <div class="line" :class="{ active: currentStep > 3 }"></div>
            <div class="step" :class="{ active: currentStep >= 4 }">
                <div class="step-circle">4</div>
                <span>Confirm</span>
            </div>
        </div>

        <div class="step-content">
            <!-- Step 1: Vehicle Selection -->
            <div v-if="currentStep === 1" class="step-pane">
                <h2>Select Your Vehicle</h2>
                
                <div v-if="customerCars.length > 0" class="cars-grid">
                    <div 
                        v-for="car in customerCars" 
                        :key="car.id" 
                        class="car-card glass-card"
                        :class="{ 
                            selected: selectedCar && selectedCar.id === car.id,
                            disabled: !car.available
                        }"
                        @click="car.available && (selectedCar = car)"
                    >
                        <div class="car-icon-wrapper">
                            <div class="car-icon">🚗</div>
                        </div>
                        <div class="car-details">
                            <h3 class="car-title">{{ car.brand }} {{ car.model }}</h3>
                            <p class="car-subtitle">{{ car.carType }} • {{ car.engine }}</p>
                            <div class="car-stat">
                                <span class="stat-label">Mileage</span>
                                <span class="stat-value">{{ car.numOfKilometers }} km</span>
                            </div>
                            <div v-if="!car.available && car.serviceMechanic" class="unavailable-badge">
                                🔧 In Service with {{ car.serviceMechanic }}
                            </div>
                        </div>
                    </div>
                    
                    <!-- Add New Car Card -->
                    <div class="car-card glass-card add-car-card" @click="showAddCarModal = true">
                        <div class="add-icon">+</div>
                        <p>Add New Vehicle</p>
                    </div>
                </div>
                
                <div v-else class="empty-state glass-card">
                    <div class="empty-icon">🏎️</div>
                    <h3>No vehicles in your garage yet</h3>
                    <p>Add your first vehicle to book an appointment.</p>
                    <button @click="showAddCarModal = true" class="btn btn-primary">
                        Add Vehicle
                    </button>
                </div>

                <div class="step-actions">
                    <button class="btn btn-primary" @click="nextStep" :disabled="!selectedCar">Next: Select Service</button>
                </div>
            </div>

            <!-- Step 2: Service Selection -->
            <div v-if="currentStep === 2" class="step-pane">
                <h2>Select a Service</h2>
                <div class="services-list">
                    <div 
                        v-for="srv in availableServices" 
                        :key="srv.serviceType" 
                        class="service-item glass-card"
                        :class="{ selected: selectedService && selectedService.serviceType === srv.serviceType }"
                        @click="selectService(srv)"
                    >
                        <div class="service-icon">{{ getServiceIcon(srv.serviceType) }}</div>
                        <div class="service-info">
                            <h3>{{ formatServiceName(srv.serviceType) }}</h3>
                            <span class="mechanic-count">{{ getMechanicCountForService(srv.serviceType) }} mechanics available</span>
                        </div>
                    </div>
                </div>
                <div class="step-actions">
                    <button class="btn btn-secondary" @click="prevStep">Back</button>
                    <button class="btn btn-primary" @click="nextStep" :disabled="!selectedService">Next: Select Mechanic</button>
                </div>
            </div>

            <!-- Step 3: Mechanic Selection -->
            <div v-if="currentStep === 3" class="step-pane">
                <h2>Select a Mechanic</h2>
                <div class="mechanics-list">
                    <div 
                        v-for="mech in filteredMechanics" 
                        :key="mech.id" 
                        class="mechanic-item glass-card"
                        :class="{ 
                            selected: selectedMechanic && selectedMechanic.id === mech.id,
                            disabled: !mech.available
                        }"
                        @click="mech.available && selectMechanic(mech)"
                    >
                        <div class="avatar-circle">{{ mech.name.charAt(0).toUpperCase() }}</div>
                        <div class="mechanic-info">
                            <h3>{{ mech.name }}</h3>
                            <StarRating 
                                v-if="mech.rating !== undefined"
                                :rating="mech.rating" 
                                :show-count="true"
                                :rating-count="mech.ratingCount"
                                size="small"
                            />
                            <p v-if="mech.available" class="service-price">{{ getMechanicServicePrice(mech) }} DT</p>
                            <p v-else class="unavailable-text">⚠️ You have an incomplete appointment with this mechanic</p>
                        </div>
                    </div>
                    <div v-if="filteredMechanics.length === 0" class="no-results">
                        No mechanics available for this service.
                    </div>
                </div>
                <div class="step-actions">
                    <button class="btn btn-secondary" @click="prevStep">Back</button>
                    <button class="btn btn-primary" @click="nextStep" :disabled="!selectedMechanic">Next: Date & Time</button>
                </div>
            </div>

            <!-- Step 4: Date & Time Selection -->
            <div v-if="currentStep === 4" class="step-pane">
                <h2>Select Date & Time</h2>
                
                <!-- Date Selection -->
                <div class="form-group">
                    <label>Select Date</label>
                    <div class="date-grid">
                        <div 
                            v-for="dateOption in availableDates" 
                            :key="dateOption.date"
                            class="date-option"
                            :class="{ selected: selectedDate === dateOption.date }"
                            @click="selectDate(dateOption.date)"
                        >
                            <div class="date-day">{{ dateOption.dayName }}</div>
                            <div class="date-number">{{ dateOption.dayNumber }}</div>
                            <div class="date-month">{{ dateOption.monthName }}</div>
                        </div>
                    </div>
                </div>

                <!-- Time Slot Selection -->
                <div v-if="selectedDate" class="form-group">
                    <label>Select Time</label>
                    <div v-if="loadingSlots" class="loading-message">Loading available times...</div>
                    <div v-else-if="availableTimeSlots.length === 0" class="no-slots-message">
                        No available time slots for this date. Please select another date.
                    </div>
                    <div v-else class="time-grid">
                        <div 
                            v-for="slot in availableTimeSlots" 
                            :key="slot.time"
                            class="time-slot"
                            :class="{ selected: selectedTime === slot.time }"
                            @click="selectedTime = slot.time"
                        >
                            {{ slot.time }}
                        </div>
                    </div>
                </div>
                
                <div class="form-group mt-4">
                    <label>Notes (Optional)</label>
                    <textarea v-model="note" class="input-textarea" placeholder="Any specific requests?"></textarea>
                </div>

                <div v-if="error" class="error-message">{{ error }}</div>

                <div class="step-actions">
                    <button class="btn btn-secondary" @click="prevStep">Back</button>
                    <button class="btn btn-primary" @click="confirmBooking" :disabled="!selectedDate || !selectedTime">Confirm Booking</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Add Vehicle Modal -->
    <b-modal
        id="modal-add-vehicle"
        v-model="showAddCarModal"
        title="Add New Vehicle"
        @ok="createCarFromModal"
        @hidden="resetModal"
        header-bg-variant="dark"
        header-text-variant="light"
        body-bg-variant="dark"
        body-text-variant="light"
        footer-bg-variant="dark"
        footer-text-variant="light"
    >
        <b-form @submit.stop.prevent="createCarFromModal">
            <!-- Vehicle Selector Component -->
            <VehicleSelector @vehicle-selected="handleVehicleSelected" />
        </b-form>
    </b-modal>
</div>
</template>

<script>
import Multiselect from 'vue-multiselect'
import DatePick from 'vue-date-pick';
import 'vue-date-pick/dist/vueDatePick.css';
import axios from 'axios'
import VehicleSelector from '../../components/VehicleSelector.vue'
import StarRating from '../../components/StarRating.vue'

var config = require('../../../config')
var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    components: {DatePick, Multiselect, VehicleSelector, StarRating},
    data() {
        return {
            currentStep: 1,
            customer: null,
            
            // Step 1 Data - Vehicle Selection
            customerCars: [],
            selectedCar: null,
            showAddCarModal: false,
            // Modal form fields
            brand: '',
            model: '',
            engine: '',
            vinNumber: '',
            yearModel: null,
            
            // Step 2 Data
            availableServices: [],
            selectedService: null,
            
            // Step 3 Data
            availableMechanics: [],
            selectedMechanic: null,
            
            // Step 4 Data
            availableDates: [],
            selectedDate: null,
            availableTimeSlots: [],
            selectedTime: null,
            loadingSlots: false,
            note: '',
            error: ''
        }
    },
    computed: {
        isStep1Valid() {
            return this.selectedCar !== null;
        },
        filteredMechanics() {
            if (!this.selectedService) return [];
            return this.availableMechanics.filter(mech => {
                return mech.services && mech.services.some(s => s.serviceType === this.selectedService.serviceType);
            });
        }
    },
    created() {
        var id = this.$route.params.userId
        AXIOS.get('/customer/'.concat(id)).then(response => {
            this.customer = response.data;
            // Fetch customer's cars
            this.fetchCustomerCars(id);
        }).catch(e => console.log(e));

        AXIOS.get('/services').then(response => {
            this.availableServices = response.data;
        }).catch(e => console.log(e));

        AXIOS.get('/mechanics').then(response => {
            this.availableMechanics = response.data;
            this.checkMechanicAvailability();
            this.fetchMechanicRatings();
        }).catch(e => console.log(e));
        
        // Generate next 14 days
        this.generateAvailableDates();
    },
    methods: {
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
        selectDate(date) {
            this.selectedDate = date;
            this.selectedTime = null;
            this.loadAvailableTimeSlots();
        },
        loadAvailableTimeSlots() {
            if (!this.selectedMechanic || !this.selectedDate) return;
            
            this.loadingSlots = true;
            this.availableTimeSlots = [];
            
            // Calculate day of week from selectedDate string (YYYY-MM-DD) to avoid timezone issues
            // new Date("YYYY-MM-DD") creates UTC midnight, which might be a different local day
            const parts = this.selectedDate.split('-');
            const year = parseInt(parts[0]);
            const month = parseInt(parts[1]) - 1; // Months are 0-11
            const day = parseInt(parts[2]);
            
            const selectedDateObj = new Date(year, month, day);
            const dayOfWeek = selectedDateObj.getDay() === 0 ? 7 : selectedDateObj.getDay();
            
            console.log('Loading slots for date:', this.selectedDate);
            console.log('Day of week (calculated locally):', dayOfWeek);
            console.log('Selected mechanic:', this.selectedMechanic);
            console.log('Mechanic timeSlots:', this.selectedMechanic.timeSlots);
            
            // Find all mechanic's time slots for this day
            const mechanicSlots = this.selectedMechanic.timeSlots || [];
            console.log('Mechanic slots count:', mechanicSlots.length);
            
            // Filter to find ALL slots that match the selected day
            const workingSlots = mechanicSlots.filter(slot => {
                if (slot && slot.startTime) {
                    // Manual parsing to avoid timezone issues
                    // Format is expected to be YYYY-MM-DDTHH:mm:ss
                    const datePart = slot.startTime.split('T')[0];
                    const timePart = slot.startTime.split('T')[1];
                    
                    // We know the backend uses a template week starting 2021-04-05 (Monday)
                    // 2021-04-05 = Monday (1)
                    // 2021-04-06 = Tuesday (2)
                    // ...
                    // 2021-04-11 = Sunday (7)
                    
                    let slotDay = 0;
                    if (datePart === '2021-04-05') slotDay = 1;
                    else if (datePart === '2021-04-06') slotDay = 2;
                    else if (datePart === '2021-04-07') slotDay = 3;
                    else if (datePart === '2021-04-08') slotDay = 4;
                    else if (datePart === '2021-04-09') slotDay = 5;
                    else if (datePart === '2021-04-10') slotDay = 6;
                    else if (datePart === '2021-04-11') slotDay = 7;
                    
                    // Fallback to Date object if not in template week (shouldn't happen for template slots)
                    if (slotDay === 0) {
                         const slotDate = new Date(slot.startTime);
                         slotDay = slotDate.getDay() === 0 ? 7 : slotDate.getDay();
                    }
                    
                    if (slotDay === dayOfWeek) {
                        console.log(`MATCH! Slot: ${slot.startTime} (Day ${slotDay}) matches Target Day ${dayOfWeek}`);
                        return true;
                    } else {
                        // Only log mismatches if they are close (e.g. off by 1 day) to reduce noise
                        if (Math.abs(slotDay - dayOfWeek) <= 1 || (slotDay === 1 && dayOfWeek === 7) || (slotDay === 7 && dayOfWeek === 1)) {
                             console.log(`MISMATCH: Slot: ${slot.startTime} (Day ${slotDay}) != Target Day ${dayOfWeek}`);
                        }
                        return false;
                    }
                }
                return false;
            });
            
            console.log('Found working slots for day', dayOfWeek, ':', workingSlots);
            
            if (workingSlots.length === 0) {
                console.warn('No valid working slots found for day', dayOfWeek);
                this.loadingSlots = false;
                return; // Mechanic doesn't work on this day
            }
            
            // Get mechanic's existing appointments for this date
            AXIOS.get('/mechanic/'.concat(this.selectedMechanic.id))
                .then(response => {
                    const mechanic = response.data;
                    const appointments = mechanic.appointments || [];
                    
                    // Filter appointments for selected date
                    const dateAppointments = appointments.filter(app => {
                        if (!app.timeSlot || !app.timeSlot.startTime) return false;
                        const appDate = new Date(app.timeSlot.startTime);
                        return appDate.toISOString().split('T')[0] === this.selectedDate;
                    });
                    
                    // Generate time slots for ALL working slots
                    let allSlots = [];
                    workingSlots.forEach(slot => {
                        const slotsFromChunk = this.generateTimeSlots(slot, dateAppointments);
                        allSlots = [...allSlots, ...slotsFromChunk];
                    });
                    
                    // Sort slots by time
                    allSlots.sort((a, b) => a.time.localeCompare(b.time));
                    
                    // Remove duplicates (just in case)
                    this.availableTimeSlots = allSlots.filter((slot, index, self) => 
                        index === self.findIndex((t) => t.time === slot.time)
                    );
                    
                    this.loadingSlots = false;
                })
                .catch(e => {
                    console.log(e);
                    this.loadingSlots = false;
                });
        },
        generateTimeSlots(workingSlot, existingAppointments) {
            const slots = [];
            const startTime = new Date(workingSlot.startTime);
            const endTime = new Date(workingSlot.endTime);
            const now = new Date();
            const minBookingTime = new Date(now.getTime() + 60 * 60 * 1000); // 1 hour from now
            
            console.log('Generating time slots:');
            console.log('Working slot start:', startTime, 'end:', endTime);
            console.log('Selected date:', this.selectedDate);
            console.log('Min booking time:', minBookingTime);
            
            // Extract hours from string to avoid timezone shifts
            // Format: YYYY-MM-DDTHH:mm:ss
            const startHour = parseInt(workingSlot.startTime.split('T')[1].split(':')[0]);
            const startMinute = parseInt(workingSlot.startTime.split('T')[1].split(':')[1]);
            const endHour = parseInt(workingSlot.endTime.split('T')[1].split(':')[0]);
            
            let currentHour = startHour;
            let currentMinute = startMinute;
            
            console.log('Hour range (parsed):', currentHour, 'to', endHour);
            
            while (currentHour < endHour) {
                const slotTime = `${String(currentHour).padStart(2, '0')}:${String(currentMinute).padStart(2, '0')}`;
                const slotDateTime = new Date(this.selectedDate + 'T' + slotTime);
                
                console.log('Checking slot:', slotTime, 'DateTime:', slotDateTime, 'vs minBooking:', minBookingTime, 'Future?:', slotDateTime >= minBookingTime);
                
                // Check if slot is in the future with 1-hour minimum notice
                if (slotDateTime >= minBookingTime) {
                    // Check if slot conflicts with existing appointments (with 1-hour buffer)
                    const hasConflict = existingAppointments.some(app => {
                        if (!app.timeSlot || !app.timeSlot.startTime || !app.timeSlot.endTime) return false;
                        
                        const appStart = new Date(app.timeSlot.startTime);
                        const appEnd = new Date(app.timeSlot.endTime);
                        
                        // Add 1-hour buffer before and after
                        const bufferStart = new Date(appStart.getTime() - 60 * 60 * 1000);
                        const bufferEnd = new Date(appEnd.getTime() + 60 * 60 * 1000);
                        
                        return slotDateTime >= bufferStart && slotDateTime < bufferEnd;
                    });
                    
                    if (!hasConflict) {
                        slots.push({ time: slotTime });
                    }
                }
                
                // Increment by 1 hour
                currentHour++;
            }
            
            console.log('Generated slots:', slots);
            return slots;
        },
        async fetchCustomerCars(customerId) {
            try {
                const response = await AXIOS.get('/cars/'.concat(customerId));
                const cars = response.data;
                
                // Check availability for each car
                for (let car of cars) {
                    // Default to available
                    this.$set(car, 'available', true);
                    this.$set(car, 'serviceMechanic', '');
                    
                    try {
                        const availResponse = await AXIOS.get(`/appointment/car-available/${car.id}`);
                        this.$set(car, 'available', availResponse.data);
                        
                        if (!availResponse.data) {
                            const mechanicResponse = await AXIOS.get(`/appointment/car-service-mechanic/${car.id}`);
                            this.$set(car, 'serviceMechanic', mechanicResponse.data || 'Unknown Mechanic');
                        }
                    } catch (e) {
                        console.log('Error checking car availability for car', car.id, ':', e);
                        // Keep default: available = true
                    }
                }
                
                this.customerCars = cars;
            } catch (e) {
                console.log('Error fetching cars:', e);
            }
        },
        async checkMechanicAvailability() {
            if (!this.customer || !this.customer.id) return;
            
            for (let mech of this.availableMechanics) {
                // Default to available
                this.$set(mech, 'available', true);
                
                try {
                    const response = await AXIOS.get(`/appointment/mechanic-available/${mech.id}/${this.customer.id}`);
                    this.$set(mech, 'available', response.data);
                } catch (e) {
                    console.log('Error checking mechanic availability for', mech.name, ':', e);
                    // Keep default: available = true
                }
            }
        },
        async fetchMechanicRatings() {
            for (let mech of this.availableMechanics) {
                try {
                    const response = await AXIOS.get(`/rating/mechanic/${mech.id}/average`);
                    this.$set(mech, 'rating', response.data.averageRating || 0);
                    this.$set(mech, 'ratingCount', response.data.ratingCount || 0);
                } catch (e) {
                    console.log('Error fetching rating for mechanic', mech.name, ':', e);
                    this.$set(mech, 'rating', 0);
                    this.$set(mech, 'ratingCount', 0);
                }
            }
        },
        handleVehicleSelected(vehicleData) {
            this.brand = vehicleData.brand
            this.model = vehicleData.model
            this.engine = vehicleData.engine
            this.vinNumber = vehicleData.vin || ''
            this.yearModel = vehicleData.year
        },
        createCarFromModal() {
            if (!this.checkFormValidity()) {
                return;
            }
            
            AXIOS.post('/car/'.concat(this.customer.id), null, {
                params: {
                    brand: this.brand,
                    model: this.model,
                    engine: this.engine,
                    vin: this.vinNumber || null,
                    year: this.yearModel || null
                }
            })
            .then(response => {
                this.customerCars.push(response.data);
                this.selectedCar = response.data; // Auto-select the new car
                this.showAddCarModal = false;
                this.resetModal();
            })
            .catch(e => {
                this.error = 'Error creating vehicle: ' + (e.response ? e.response.data.message : e.message);
                console.log(e);
            });
        },
        resetModal() {
            this.brand = '';
            this.model = '';
            this.engine = '';
            this.vinNumber = '';
            this.yearModel = null;
        },
        checkFormValidity() {
            const valid = this.brand && this.model && this.engine;
            return valid;
        },
        selectService(service) {
            this.selectedService = service;
        },
        selectMechanic(mech) {
            // Fetch full mechanic details including timeSlots
            AXIOS.get('/mechanic/'.concat(mech.id))
                .then(response => {
                    this.selectedMechanic = response.data;
                    console.log('Loaded full mechanic data:', this.selectedMechanic);
                    console.log('Mechanic timeSlots:', this.selectedMechanic.timeSlots);
                })
                .catch(e => {
                    console.error('Error loading mechanic details:', e);
                    // Fallback to basic data if fetch fails
                    this.selectedMechanic = mech;
                });
        },
        nextStep() {
            if (this.currentStep === 3) {
                // When moving to step 4, generate dates filtered by mechanic's availability
                this.generateAvailableDatesForMechanic();
            }
            this.currentStep++;
        },
        generateAvailableDatesForMechanic() {
            if (!this.selectedMechanic) {
                this.availableDates = [];
                return;
            }
            
            const dates = [];
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            
            // Get the days the mechanic works (1-7 format, Sunday=7)
            const mechanicWorkDays = new Set();
            if (this.selectedMechanic.timeSlots && this.selectedMechanic.timeSlots.length > 0) {
                this.selectedMechanic.timeSlots.forEach(slot => {
                    const slotDate = new Date(slot.startTime);
                    const dayOfWeek = slotDate.getDay() === 0 ? 7 : slotDate.getDay();
                    mechanicWorkDays.add(dayOfWeek);
                });
            }
            
            console.log('Mechanic works on days:', Array.from(mechanicWorkDays));
            
            // If mechanic has no schedule, show no dates
            if (mechanicWorkDays.size === 0) {
                console.warn('Mechanic has no schedule!');
                this.availableDates = [];
                return;
            }
            
            const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
            const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            
            // Look ahead up to 30 days to find dates when mechanic works
            let daysChecked = 0;
            let daysFound = 0;
            const maxDaysToCheck = 30;
            const maxDatesToShow = 14;
            
            while (daysFound < maxDatesToShow && daysChecked < maxDaysToCheck) {
                daysChecked++;
                const date = new Date(today);
                date.setDate(today.getDate() + daysChecked);
                
                // Convert to our day format (1-7, Sunday=7)
                const dayOfWeek = date.getDay() === 0 ? 7 : date.getDay();
                
                console.log('Checking date:', date.toISOString().split('T')[0], 'Day:', dayOfWeek, 'Mechanic works?:', mechanicWorkDays.has(dayOfWeek));
                
                // Only include dates when mechanic works
                if (mechanicWorkDays.has(dayOfWeek)) {
                    // Format date manually to avoid timezone shifts from toISOString() (which uses UTC)
                    const year = date.getFullYear();
                    const month = String(date.getMonth() + 1).padStart(2, '0');
                    const day = String(date.getDate()).padStart(2, '0');
                    const dateString = `${year}-${month}-${day}`;
                    
                    dates.push({
                        date: dateString,
                        dayName: dayNames[date.getDay()],
                        dayNumber: date.getDate(),
                        monthName: monthNames[date.getMonth()]
                    });
                    daysFound++;
                }
            }
            
            console.log('Generated', dates.length, 'available dates:', dates);
            this.availableDates = dates;
        },
        prevStep() {
            this.currentStep--;
        },
        formatServiceName(name) {
            if (!name) return '';
            // Replace underscores with spaces and capitalize first letter
            return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
        },
        getServiceIcon(name) {
            const icons = {
                'CLIMATISATION': '❄️',
                'BATTERIE': '🔋',
                'FREINAGE': '🛑',
                'EMBRAYAGE': '⚙️',
                'ECHAPPEMENT': '💨',
                'MECANIQUE': '🔧',
                'VIDANGE': '🛢️',
                'DIAGNOSTIC': '🔍',
                'REVISION': '✅',
                'SUSPENSION': '🔩'
            };
            return icons[name] || '🔧';
        },
        getMechanicCountForService(serviceType) {
            if (!this.availableMechanics) return 0;
            return this.availableMechanics.filter(mech => 
                mech.services && mech.services.some(s => s.serviceType === serviceType)
            ).length;
        },
        getMechanicServicePrice(mechanic) {
            if (!this.selectedService || !mechanic.services) return 0;
            const service = mechanic.services.find(s => s.serviceType === this.selectedService.serviceType);
            return service ? service.price : 0;
        },
        confirmBooking() {
            this.error = "";
            
            // Use the selected car directly (no need to create a new one)
            this.createAppointment(this.selectedCar);
        },
        createAppointment(car) {
            // Format date and time
            const startDateTime = this.selectedDate + '-' + this.selectedTime;
            const [hours, minutes] = this.selectedTime.split(':');
            const endHours = (parseInt(hours) + 1).toString().padStart(2, '0');
            const endDateTime = this.selectedDate + '-' + endHours + ':' + minutes;

            AXIOS.post('/timeslot/'.concat(startDateTime + "?endTime=" + endDateTime))
            .then(response => {
                const timeslot = response.data;
                AXIOS.post('/appointment/'.concat(this.customer.id + "?timeSlotId="+timeslot.id + "&carId="+car.id + "&services="+this.selectedService.serviceType + "&note="+this.note))
                .then(response => {
                    const appointment = response.data;
                    AXIOS.put('/appointment/addMechanic/'.concat(this.selectedMechanic.id + "?appointmentId=" + appointment.id))
                    .then(() => {
                        // Success! Redirect
                        this.$router.push('/customerDashboard/Overview/' + this.customer.id);
                    })
                    .catch(e => {
                        this.error = "Error assigning mechanic: " + (e.response ? e.response.data.message : e.message);
                        // Cleanup
                        AXIOS.delete('/appointment/' + appointment.id);
                    });
                })
                .catch(e => {
                    this.error = "Error creating appointment: " + (e.response ? e.response.data.message : e.message);
                });
            })
            .catch(e => {
                this.error = "Error creating timeslot: " + (e.response ? e.response.data.message : e.message);
            });
        }
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>

<style scoped>
.booking-page {
    padding: 2rem;
    min-height: 100%;
    display: flex;
    justify-content: center;
}

.booking-container {
    width: 100%;
    max-width: 800px;
    padding: 2rem;
}

.booking-header {
    text-align: center;
    margin-bottom: 2rem;
}

.booking-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

/* Steps Indicator */
.steps-indicator {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 3rem;
    padding: 0 1rem;
}

.step {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    color: var(--text-secondary);
    position: relative;
    z-index: 1;
}

.step-circle {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    transition: all 0.3s ease;
}

.step.active .step-circle {
    background: var(--primary);
    color: #000;
}

.step.completed .step-circle {
    background: var(--primary);
    color: #000;
}

.line {
    flex: 1;
    height: 2px;
    background: rgba(255, 255, 255, 0.1);
    margin: 0 1rem;
    margin-bottom: 1.5rem; /* Align with circle */
    transition: all 0.3s ease;
}

.line.active {
    background: var(--primary);
}

/* Step Content */
.step-pane h2 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    color: var(--primary);
}

.form-group {
    margin-bottom: 1.5rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
}

/* Services List */
.services-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
}

.service-item {
    padding: 1rem;
    cursor: pointer;
    border: 1px solid transparent;
    transition: all 0.2s;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 1rem;
}

.service-item:hover {
    background: rgba(255, 255, 255, 0.1);
}

.service-item.selected {
    border-color: var(--primary);
    background: rgba(245, 158, 11, 0.1);
}

.service-icon {
    font-size: 2rem;
}

.service-info h3 {
    margin: 0 0 0.5rem 0;
    font-size: 1.1rem;
}

.service-info .price {
    color: var(--primary);
    font-weight: 600;
    font-size: 1rem;
}

.service-info .mechanic-count {
    color: var(--text-secondary);
    font-size: 0.9rem;
    font-style: italic;
}
/* Date Grid */
.date-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 0.75rem;
    margin-bottom: 2rem;
}

.date-option {
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-md);
    padding: 1rem 0.5rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
}

.date-option:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.5);
}

.date-option.selected {
    background: rgba(245, 158, 11, 0.2);
    border-color: var(--primary);
}

.date-day {
    font-size: 0.75rem;
    color: var(--text-secondary);
    margin-bottom: 0.25rem;
}

.date-number {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 0.25rem;
}

.date-month {
    font-size: 0.75rem;
    color: var(--text-secondary);
}

/* Time Grid */
.time-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 0.75rem;
}

.time-slot {
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    padding: 0.75rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
    font-weight: 600;
}

.time-slot:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.5);
}

.time-slot.selected {
    background: var(--primary);
    border-color: var(--primary);
    color: #000;
}

.loading-message, .no-slots-message {
    text-align: center;
    padding: 2rem;
    color: var(--text-secondary);
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-md);
}

@media (max-width: 900px) {
    .booking-grid {
        grid-template-columns: 1fr;
    }
    
    .date-grid {
        grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
    }
    
    .time-grid {
        grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
    }
}
/* Mechanics List */
.mechanics-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
}

.mechanic-item {
    padding: 1rem;
    cursor: pointer;
    border: 1px solid transparent;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.mechanic-item:hover {
    background: rgba(255, 255, 255, 0.1);
}

.mechanic-item.selected {
    border-color: var(--primary);
    background: rgba(245, 158, 11, 0.1);
}

.mechanic-item.disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
}

.mechanic-item.disabled:hover {
    background: transparent;
}

.unavailable-text {
    color: #ef4444 !important;
    font-weight: 500;
    font-size: 0.9rem;
}

.service-price {
    color: var(--primary) !important;
    font-weight: 600;
    font-size: 1.1rem;
    margin-top: 0.25rem;
}

.avatar-circle {
    width: 40px;
    height: 40px;
    background: var(--primary);
    color: #000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
}

.mechanic-info h3 {
    font-size: 1rem;
    margin: 0;
}

.mechanic-info p {
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin: 0;
}

/* Actions */
.step-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 2rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding-top: 1.5rem;
}

.date-picker-wrapper {
    display: flex;
    justify-content: center;
    background: rgba(255, 255, 255, 0.05);
    padding: 1rem;
    border-radius: var(--radius-md);
}

.input-textarea {
    width: 100%;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    padding: 0.75rem;
    color: var(--text-primary);
    min-height: 100px;
}

.error-message {
    color: var(--error);
    margin-top: 1rem;
    text-align: center;
}

/* Vehicle Selection Styles */
.cars-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
}

.car-card {
    display: flex;
    flex-direction: column;
    padding: 1.5rem;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.2s ease;
    position: relative;
}

.car-card:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(245, 158, 11, 0.3);
}

.car-card.selected {
    border-color: var(--primary);
    background: rgba(245, 158, 11, 0.15);
}

.car-card.disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
}

.car-card.disabled:hover {
    background: transparent;
    border-color: transparent;
}

.unavailable-badge {
    margin-top: 0.75rem;
    padding: 0.5rem;
    background: rgba(239, 68, 68, 0.2);
    border: 1px solid rgba(239, 68, 68, 0.4);
    border-radius: 6px;
    color: #ef4444;
    font-size: 0.85rem;
    text-align: center;
    font-weight: 500;
}

.car-icon-wrapper {
    width: 60px;
    height: 60px;
    background: rgba(245, 158, 11, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 1rem;
}

.car-icon {
    font-size: 2rem;
}

.car-title {
    font-size: 1.25rem;
    margin-bottom: 0.5rem;
    color: var(--primary);
}

.car-subtitle {
    color: var(--text-secondary);
    font-size: 0.9rem;
    margin-bottom: 1rem;
}

.car-stat {
    display: flex;
    justify-content: space-between;
    padding-top: 0.75rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-label {
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.stat-value {
    font-weight: 600;
    color: var(--text-primary);
}

.add-car-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    min-height: 200px;
    border: 2px dashed rgba(245, 158, 11, 0.5);
    background: rgba(245, 158, 11, 0.05);
}

.add-car-card:hover {
    border-color: var(--primary);
    background: rgba(245, 158, 11, 0.15);
}

.add-icon {
    font-size: 3rem;
    color: var(--primary);
    font-weight: 300;
}

.add-car-card p {
    margin: 0;
    color: var(--text-primary);
    font-weight: 500;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem 2rem;
    text-align: center;
}

.empty-icon {
    font-size: 4rem;
    margin-bottom: 1.5rem;
    opacity: 0.5;
}

.empty-state h3 {
    margin-bottom: 0.5rem;
    color: var(--text-primary);
}

.empty-state p {
    color: var(--text-secondary);
    margin-bottom: 1.5rem;
}

/* Custom Modal Styles */
.custom-form-group label {
    color: var(--text-primary) !important;
}

.custom-input, .custom-select {
    background-color: #2d3748 !important;
    border: 1px solid #4a5568 !important;
    color: white !important;
}

.custom-input:focus, .custom-select:focus {
    background-color: #2d3748 !important;
    border-color: var(--primary) !important;
    box-shadow: 0 0 0 0.2rem rgba(245, 158, 11, 0.25) !important;
}
</style>