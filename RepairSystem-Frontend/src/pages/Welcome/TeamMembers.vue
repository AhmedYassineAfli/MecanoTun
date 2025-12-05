<template>
<div class="team-members-page">
    <div class="page-header">
        <div class="header-content">
            <h1>Mechanics Management</h1>
            <p>Manage mechanics and their schedules</p>
        </div>
        <button class="btn btn-primary" @click="openAddModal">
            <span class="icon-plus">+</span> Add Mechanic
        </button>
    </div>

    <!-- Search Bar -->
    <div class="search-bar glass-card">
        <span class="search-icon">🔍</span>
        <input 
            type="text" 
            v-model="searchQuery" 
            @input="searchMechanics" 
            placeholder="Search mechanics by name, email, or phone..." 
            class="search-input"
        >
    </div>

    <!-- Mechanics Grid -->
    <div class="mechanics-grid">
        <div v-for="mechanic in mechanics" :key="mechanic.id" class="mechanic-card glass-card">
            <div class="card-header">
                <div class="avatar-circle">
                    {{ mechanic.name ? mechanic.name.charAt(0).toUpperCase() : 'M' }}
                </div>
                <div class="mechanic-info">
                    <h3>{{ mechanic.name }}</h3>
                    <span class="role-badge">Mechanic</span>
                </div>
                <div class="card-actions-top">
                    <button class="btn-icon" @click="openEditModal(mechanic)" title="Edit">✏️</button>
                    <button class="btn-icon delete" @click="removeMechanic(mechanic.id)" title="Remove">🗑️</button>
                </div>
            </div>

            <div class="card-body">
                <div class="info-row">
                    <span class="icon">✉️</span>
                    {{ mechanic.email }}
                </div>
                <div class="info-row">
                    <span class="icon">📱</span>
                    {{ mechanic.phone }}
                </div>
                
                <div class="capabilities-section">
                    <span class="label">Capabilities:</span>
                    <div class="tags">
                        <span v-for="service in mechanic.services" :key="service.serviceType" class="tag">
                            {{ service.serviceType }} - {{ service.price }} DT
                        </span>
                    </div>
                </div>

                <div class="schedule-section">
                    <span class="label">Schedule:</span>
                    <div class="schedule-grid">
                        <div v-for="(slot, index) in mechanic.timeSlots" :key="index" class="schedule-day">
                            <span class="day-name">{{ getDayName(index) }}</span>
                            <span class="day-time">{{ slot.startTimeFormat }} - {{ slot.endTimeFormat }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Add/Edit Modal -->
    <b-modal
        v-model="modalShow"
        :title="isEditing ? 'Edit Mechanic' : 'Add New Mechanic'"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        size="lg"
        centered
        hide-footer
    >
        <form @submit.prevent="isEditing ? updateMechanic() : createMechanic()">
            <div class="form-section">
                <h4>Personal Information</h4>
                <div class="form-row">
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" v-model="form.name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" v-model="form.email" class="form-control" :disabled="isEditing" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Phone</label>
                        <input type="tel" v-model="form.phone" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" v-model="form.password" class="form-control" required>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h4>Capabilities</h4>
                <multiselect 
                    v-model="form.services" 
                    :options="serviceOptions" 
                    :multiple="true" 
                    placeholder="Select services" 
                    label="name" 
                    track-by="name"
                    class="custom-multiselect"
                    @input="ensurePrices"
                ></multiselect>
                
                <!-- Service Prices Inputs -->
                <div v-if="form.services.length > 0" class="service-prices-list">
                    <h5 class="mt-3 mb-2">Service Prices (DT)</h5>
                    <div v-for="(service, index) in form.services" :key="index" class="service-price-row">
                        <span class="service-name">{{ service.name }}</span>
                        <input 
                            type="number" 
                            v-model.number="service.price" 
                            class="form-control price-input" 
                            placeholder="Enter price"
                            min="0"
                            step="0.01"
                            required
                        >
                    </div>
                </div>
            </div>

            <div class="form-section">
                <div class="schedule-header">
                    <h4>Work Schedule</h4>
                    <div class="schedule-actions">
                        <button type="button" class="btn btn-outline" @click="setDefaultSchedule">Default 9-5</button>
                        <button type="button" class="btn btn-outline" @click="copyMondayToAll">Copy Mon to All</button>
                    </div>
                </div>
                
                <div v-for="(day, index) in days" :key="index" class="schedule-row">
                    <div class="day-col">
                        <span class="day-label">{{ day }}</span>
                        <label class="checkbox-label">
                            <input type="checkbox" v-model="form.schedule[index].isOff">
                            Day Off
                        </label>
                    </div>
                    
                    <div class="time-inputs" :class="{ disabled: form.schedule[index].isOff }">
                        <input 
                            type="time" 
                            v-model="form.schedule[index].start" 
                            class="form-control time-input"
                            :disabled="form.schedule[index].isOff"
                        >
                        <span class="separator">-</span>
                        <input 
                            type="time" 
                            v-model="form.schedule[index].end" 
                            class="form-control time-input"
                            :disabled="form.schedule[index].isOff"
                        >
                    </div>
                </div>
            </div>

            <div v-if="error" class="error-message">{{ error }}</div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" @click="modalShow = false">Cancel</button>
                <button type="submit" class="btn btn-primary">{{ isEditing ? 'Save Changes' : 'Create Mechanic' }}</button>
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
            mechanics: [],
            allMechanics: [], // For search filtering
            searchQuery: '',
            modalShow: false,
            isEditing: false,
            currentMechanicId: null,
            days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
            serviceOptions: [
                {name: "CLIMATISATION", defaultPrice: 80}, 
                {name: "BATTERIE", defaultPrice: 20}, 
                {name: "FREINAGE", defaultPrice: 60}, 
                {name: "EMBRAYAGE", defaultPrice: 100}, 
                {name: "ECHAPPEMENT", defaultPrice: 90}, 
                {name: "MECANIQUE", defaultPrice: 70}, 
                {name: "VIDANGE", defaultPrice: 30}, 
                {name: "DIAGNOSTIC", defaultPrice: 50}, 
                {name: "REVISION", defaultPrice: 120}, 
                {name: "SUSPENSION", defaultPrice: 85}
            ],
            form: {
                name: '',
                email: '',
                phone: '',
                password: '',
                services: [],
                schedule: Array(6).fill().map(() => ({ start: '', end: '', isOff: false }))
            },
            error: ''
        }
    },
    created() {
        this.fetchMechanics();
    },
    methods: {
        fetchMechanics() {
            AXIOS.get('/mechanics')
                .then(response => {
                    this.mechanics = response.data;
                    this.allMechanics = [...response.data]; // Create a copy
                })
                .catch(e => this.error = e);
        },
        searchMechanics() {
            const query = this.searchQuery.toLowerCase().trim();
            if (!query) {
                this.mechanics = [...this.allMechanics];
            } else {
                this.mechanics = this.allMechanics.filter(m => 
                    m.name.toLowerCase().includes(query) ||
                    m.email.toLowerCase().includes(query) ||
                    m.phone.includes(query)
                );
            }
        },
        openAddModal() {
            this.isEditing = false;
            this.resetForm();
            this.modalShow = true;
        },
        openEditModal(mechanic) {
            this.isEditing = true;
            this.currentMechanicId = mechanic.id;
            this.form.name = mechanic.name;
            this.form.email = mechanic.email;
            this.form.phone = mechanic.phone;
            this.form.password = mechanic.password;
            
            // Map services with price - preserve existing prices
            console.log('Mechanic services:', mechanic.services);
            this.form.services = mechanic.services.map(s => {
                console.log('Service:', s.serviceType, 'Price:', s.price);
                return { 
                    name: s.serviceType,
                    price: s.price // Keep the actual price from database
                };
            });
            console.log('Form services after mapping:', this.form.services);
            
            // Only fill defaults for services that truly have no price set
            this.ensurePrices();
            
            // Map schedule
            this.form.schedule = Array(6).fill().map(() => ({ start: '', end: '', isOff: true }));
            
            if (mechanic.timeSlots && mechanic.timeSlots.length > 0) {
                mechanic.timeSlots.forEach((slot, index) => {
                    if (index < 6) {
                        this.form.schedule[index] = {
                            start: slot.startTimeFormat,
                            end: slot.endTimeFormat,
                            isOff: false
                        };
                    }
                });
            }
            
            this.modalShow = true;
        },
        resetForm() {
            this.form = {
                name: '',
                email: '',
                phone: '',
                password: '',
                services: [],
                schedule: Array(6).fill().map(() => ({ start: '', end: '', isOff: false }))
            };
        },
        ensurePrices() {
            // Ensure every selected service has a price property
            // Only set default if price is truly missing (undefined/null/empty string)
            // Do NOT replace 0 or existing valid prices
            this.form.services.forEach(s => {
                if (s.price === undefined || s.price === null || s.price === '') {
                    // Find the default price for this service type
                    const serviceOption = this.serviceOptions.find(opt => opt.name === s.name);
                    const defaultPrice = serviceOption ? serviceOption.defaultPrice : 50;
                    this.$set(s, 'price', defaultPrice);
                }
            });
        },
        setDefaultSchedule() {
            // Set default 9-5 schedule for all days
            this.form.schedule = this.form.schedule.map(() => ({
                start: '09:00',
                end: '17:00',
                isOff: false
            }));
            // Set Saturday to off by default?
            this.form.schedule[5].isOff = true;
        },
        copyMondayToAll() {
            // Copy Monday's schedule to all other days
            const monday = this.form.schedule[0];
            this.form.schedule = this.form.schedule.map((day, index) => {
                if (index === 0) return day; // Skip Monday itself
                return {
                    start: monday.start,
                    end: monday.end,
                    isOff: monday.isOff
                };
            });
        },
        handleSubmit() {
            if (this.isEditing) {
                this.updateMechanic();
            } else {
                this.createMechanic();
            }
        },
        createMechanic() {
            const { name, password, phone, email } = this.form;
            
            AXIOS.post('/mechanic/'.concat(name), {}, {
                params: { name, phone, password, email }
            }).then(async () => {
                await this.updateServicesAndSchedule(email);
            }).catch(e => {
                this.error = e;
                console.error('Create mechanic error:', e);
            });
        },
        updateMechanic() {
            const { name, password, phone, email } = this.form;
            
            AXIOS.put('/mechanic/'.concat(email + "?name=" + name + "&password=" + password + "&phone=" + phone))
                .then(async () => {
                    await this.updateServicesAndSchedule(email);
                }).catch(e => {
                    this.error = e;
                    console.error('Update mechanic error:', e);
                });
        },
        async updateServicesAndSchedule(email) {
            try {
                // 1. Get the mechanic to get ID and current services
                const response = await AXIOS.get('/mechanics');
                const mechanic = response.data.find(m => m.email === email);
                if (!mechanic) throw new Error("Mechanic not found");
                
                const mechanicId = mechanic.id;
                const currentServices = mechanic.services || [];
                const newServices = this.form.services;

                // 2. Calculate diffs
                const toRemove = currentServices.filter(curr => 
                    !newServices.find(ns => ns.name === curr.serviceType)
                );
                
                const toAdd = newServices.filter(ns => 
                    !currentServices.find(curr => curr.serviceType === ns.name)
                );
                
                const toUpdate = newServices.filter(ns => {
                    const curr = currentServices.find(c => c.serviceType === ns.name);
                    return curr && curr.price !== ns.price;
                });

                // 3. Execute updates
                // Remove services
                for (const s of toRemove) {
                    await AXIOS.put('/mechanic/removeService/'.concat(mechanicId), null, {
                        params: { serviceType: s.serviceType }
                    });
                }

                // Add new services
                for (const s of toAdd) {
                    await AXIOS.post('/service/'.concat(mechanicId), null, {
                        params: { type: s.name, price: s.price }
                    });
                }

                // Update existing services (Remove then Add)
                for (const s of toUpdate) {
                    await AXIOS.put('/mechanic/removeService/'.concat(mechanicId), null, {
                        params: { serviceType: s.name }
                    });
                    await AXIOS.post('/service/'.concat(mechanicId), null, {
                        params: { type: s.name, price: s.price }
                    });
                }

                // 4. Update Schedule
                const timeSlots = [];
                const dayMapping = { 0: 1, 1: 2, 2: 3, 3: 4, 4: 5, 5: 6 }; // Mon=1, Tue=2, etc.
                
                this.form.schedule.forEach((daySchedule, index) => {
                    if (!daySchedule.isOff && daySchedule.start && daySchedule.end) {
                        const startHour = parseInt(daySchedule.start.split(':')[0]);
                        const endHour = parseInt(daySchedule.end.split(':')[0]);
                        
                        const slot = {
                            dayOfWeek: dayMapping[index],
                            startHour: startHour,
                            endHour: endHour
                        };
                        timeSlots.push(slot);
                    }
                });
                
                await AXIOS.put("/mechanic/".concat(mechanicId + "/timeslots"), timeSlots);

                // 5. Refresh and close
                this.fetchMechanics();
                this.modalShow = false;
                this.error = '';

            } catch (e) {
                const errorMsg = e.response && e.response.data && e.response.data.message 
                    ? e.response.data.message 
                    : e.message;
                alert('Error: ' + errorMsg);
                this.error = errorMsg;
                console.error('Update error:', e);
            }
        },
        removeMechanic(id) {
            if(confirm("Are you sure you want to remove this mechanic?")) {
                AXIOS.delete('/mechanic/' + id)
                    .then(() => this.fetchMechanics())
                    .catch(e => this.error = e);
            }
        },
        getDayName(index) {
            return ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'][index];
        }
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style scoped>
.team-members-page {
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

.mechanics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 1.5rem;
}

.mechanic-card {
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

.mechanic-info h3 {
    font-size: 1.2rem;
    margin: 0;
}

.role-badge {
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

.capabilities-section {
    margin-top: 1rem;
}

.tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-top: 0.5rem;
}

.tag {
    background: rgba(255, 255, 255, 0.1);
    padding: 0.2rem 0.6rem;
    border-radius: 12px;
    font-size: 0.8rem;
}

.schedule-section {
    margin-top: 1rem;
}

.schedule-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.5rem;
    margin-top: 0.5rem;
}

.schedule-day {
    background: rgba(255, 255, 255, 0.03);
    padding: 0.4rem;
    border-radius: 4px;
    text-align: center;
}

.day-name {
    display: block;
    font-size: 0.75rem;
    color: var(--primary);
    font-weight: 600;
}

.day-time {
    display: block;
    font-size: 0.7rem;
    color: var(--text-secondary);
}

/* Modal Styles */
.form-section {
    margin-bottom: 2rem;
}

.section-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 0.5rem;
}

.form-section h4 {
    font-size: 1.1rem;
    margin: 0;
    color: var(--primary);
}

/* Work Schedule Styling */
.schedule-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.schedule-header h4 {
    margin: 0;
}

.schedule-actions {
    display: flex;
    gap: 0.5rem;
}

.schedule-row {
    display: grid;
    grid-template-columns: 140px 1fr;
    align-items: center;
    gap: 1.5rem;
    margin-bottom: 1rem;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.02);
    border-radius: var(--radius-sm);
}

.day-col {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;
}

.day-label {
    font-weight: 600;
    font-size: 1rem;
    color: var(--text-primary);
}

.checkbox-label {
    font-size: 0.8rem;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    gap: 0.4rem;
    cursor: pointer;
    user-select: none;
}

.checkbox-label input[type="checkbox"] {
    cursor: pointer;
}

.time-inputs {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.time-inputs.disabled {
    opacity: 0.3;
    pointer-events: none;
}

.time-input {
    flex: 1;
    max-width: 150px;
}

.separator {
    color: var(--text-secondary);
    font-weight: 600;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
}

.btn-outline {
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: var(--text-primary);
    background: transparent;
    padding: 0.2rem 0.6rem;
    font-size: 0.8rem;
}

.btn-outline:hover {
    border-color: var(--primary);
    color: var(--primary);
}

.error-message {
    background: rgba(239, 68, 68, 0.1);
    border: 1px solid rgba(239, 68, 68, 0.3);
    color: #ef4444;
    padding: 1rem;
    border-radius: 4px;
    margin-bottom: 1rem;
    font-size: 0.9rem;
}

/* Service Price Inputs Styling */
.service-prices-list {
    background: rgba(255, 255, 255, 0.03);
    padding: 1.5rem;
    border-radius: var(--radius-md);
    border: 1px solid rgba(255, 255, 255, 0.1);
    margin-top: 1.5rem;
}

.service-prices-list h5 {
    color: var(--primary);
    font-size: 1rem;
    font-weight: 600;
    margin-bottom: 1rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.service-price-row {
    display: grid;
    grid-template-columns: 1fr auto;
    align-items: center;
    gap: 1rem;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-sm);
    margin-bottom: 0.75rem;
    transition: all 0.2s ease;
}

.service-price-row:hover {
    background: rgba(255, 255, 255, 0.08);
    transform: translateX(4px);
}

.service-price-row:last-child {
    margin-bottom: 0;
}

.service-name {
    color: var(--text-primary);
    font-weight: 500;
    font-size: 0.95rem;
}

.price-input {
    width: 120px;
    padding: 0.5rem 0.75rem;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(243, 190, 53, 0.3);
    border-radius: var(--radius-sm);
    color: var(--primary);
    font-size: 1rem;
    font-weight: 600;
    text-align: right;
    transition: all 0.2s ease;
}

.price-input:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(243, 190, 53, 0.1);
    background: rgba(0, 0, 0, 0.4);
}

.price-input::placeholder {
    color: rgba(243, 190, 53, 0.4);
}

/* Enhanced Modal Styles */
::v-deep .modal-header-custom {
    background: linear-gradient(135deg, rgba(243, 190, 53, 0.1), rgba(243, 190, 53, 0.05));
    border-bottom: 1px solid rgba(243, 190, 53, 0.2);
    padding: 1.5rem;
}

::v-deep .modal-header-custom .modal-title {
    color: var(--primary);
    font-weight: 600;
    font-size: 1.3rem;
}

::v-deep .modal-body-custom {
    padding: 2rem;
    background: var(--bg-secondary);
}

::v-deep .modal-footer-custom {
    background: rgba(255, 255, 255, 0.02);
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding: 1.5rem;
}

/* Input Fields */
.form-control, .input {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: var(--text-primary);
    padding: 0.75rem 1rem;
    border-radius: var(--radius-sm);
    transition: all 0.2s ease;
}

.form-control:focus, .input:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(243, 190, 53, 0.1);
    background: rgba(255, 255, 255, 0.08);
}

/* Multiselect Styling */
::v-deep .custom-multiselect .multiselect__tags {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    min-height: 45px;
}

::v-deep .custom-multiselect .multiselect__tag {
    background: var(--primary);
    color: #000;
    font-weight: 500;
    padding: 0.4rem 1.5rem 0.4rem 0.75rem;
    border-radius: 20px;
}

::v-deep .custom-multiselect .multiselect__tag-icon:hover {
    background: rgba(0, 0, 0, 0.2);
}

::v-deep .custom-multiselect .multiselect__option--highlight {
    background: var(--primary);
    color: #000;
}

::v-deep .custom-multiselect .multiselect__option--selected {
    background: rgba(243, 190, 53, 0.2);
    color: var(--primary);
}

</style>
