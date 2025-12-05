<template>
<div class="mechanic-services-page">
    <div class="page-header">
        <h1 class="page-title">My Services</h1>
        <button @click="openAddModal" class="btn btn-primary">
            <span class="btn-icon">+</span> Add Service
        </button>
    </div>

    <div class="services-grid" v-if="services.length > 0">
        <div v-for="service in services" :key="service.serviceType" class="service-card glass-card">
            <div class="card-header-actions">
                <button class="btn-icon edit" @click="openEditModal(service)" title="Edit Price">✏️</button>
                <button class="btn-icon delete" @click="deleteService(service)" title="Delete Service">🗑️</button>
            </div>
            <div class="service-icon">
                {{ getServiceIcon(service.serviceType) }}
            </div>
            <div class="service-info">
                <h3>{{ formatServiceName(service.serviceType) }}</h3>
                <span class="price-badge">{{ service.price }} DT</span>
            </div>
        </div>
    </div>
    
    <div v-else class="empty-state glass-card">
        <div class="empty-icon">🛠️</div>
        <h3>No services offered yet</h3>
        <p>Add services to your profile so customers can book you.</p>
        <button @click="openAddModal" class="btn btn-primary">
            Add Service
        </button>
    </div>

    <!-- Add/Edit Modal -->
    <b-modal
        v-model="modalShow"
        :title="editingService ? 'Edit Service Price' : 'Add New Service'"
        header-bg-variant="dark"
        header-text-variant="light"
        body-bg-variant="dark"
        body-text-variant="light"
        footer-bg-variant="dark"
        footer-text-variant="light"
        hide-footer
    >
        <form @submit.prevent="handleSubmit">
            <div class="form-group" v-if="!editingService">
                <label>Service Type</label>
                <select v-model="serviceType" class="form-control custom-select" required>
                    <option value="" disabled>Select a service</option>
                    <option v-for="type in availableServiceTypes" :key="type" :value="type">
                        {{ formatServiceName(type) }}
                    </option>
                </select>
            </div>
            <div class="form-group" v-else>
                <label>Service Type</label>
                <input type="text" :value="formatServiceName(serviceType)" class="form-control custom-input" disabled>
            </div>

            <div class="form-group">
                <label>Price (DT)</label>
                <input type="number" v-model="price" class="form-control custom-input" min="0" required>
            </div>
            
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" @click="modalShow = false">Cancel</button>
                <button type="submit" class="btn btn-primary">
                    {{ editingService ? 'Update Price' : 'Add Service' }}
                </button>
            </div>
        </form>
    </b-modal>
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
            services: [],
            modalShow: false,
            editingService: null,
            serviceType: '',
            price: '',
            error: '',
            allServiceTypes: [
                'CLIMATISATION', 'BATTERIE', 'FREINAGE', 'EMBRAYAGE', 
                'ECHAPPEMENT', 'MECANIQUE', 'VIDANGE', 'DIAGNOSTIC', 
                'REVISION', 'SUSPENSION'
            ]
        }
    },
    computed: {
        availableServiceTypes() {
            const existingTypes = this.services.map(s => s.serviceType);
            return this.allServiceTypes.filter(type => !existingTypes.includes(type));
        }
    },
    created() {
        this.mechanicId = this.$route.params.userId;
        if (!this.mechanicId || this.mechanicId === 'undefined') {
            this.error = "Error: Mechanic ID is missing. Please log in again.";
            return;
        }
        this.fetchServices();
    },
    methods: {
        fetchServices() {
            if (!this.mechanicId) return;
            AXIOS.get('/services/mechanic/'.concat(this.mechanicId))
                .then(response => {
                    this.services = response.data;
                })
                .catch(e => {
                    this.error = e;
                    console.error("Error fetching services:", e);
                });
        },
        formatServiceName(name) {
            if (!name) return '';
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
                'DIAGNOSTIC': '�',
                'REVISION': '✅',
                'SUSPENSION': '🔩'
            };
            return icons[name] || '🔧';
        },
        openAddModal() {
            this.editingService = null;
            this.serviceType = '';
            this.price = '';
            this.modalShow = true;
        },
        openEditModal(service) {
            this.editingService = service;
            this.serviceType = service.serviceType;
            this.price = service.price;
            this.modalShow = true;
        },
        handleSubmit() {
            if (this.editingService) {
                this.updateService();
            } else {
                this.createService();
            }
        },
        createService() {
            AXIOS.post('/service/'.concat(this.mechanicId), null, {
                params: {
                    type: this.serviceType,
                    price: this.price
                }
            })
            .then(response => {
                this.services.push(response.data);
                this.modalShow = false;
            })
            .catch(e => {
                this.error = e;
                console.log(e);
            });
        },
        updateService() {
            // Since we don't have a direct update endpoint for service price yet, 
            // we'll delete and recreate or we need to add one.
            // Wait, the plan said "Edit Price".
            // Let's use the create endpoint which might update if it exists? 
            // No, the backend creates a new one.
            // We need an update endpoint or delete/create.
            // For now, let's delete and create to simulate update since ID might change but type is unique per mechanic?
            // Actually, Service entity has an ID but we don't expose it easily.
            // Let's look at backend: createService creates a NEW service instance.
            // So we should probably delete the old one and create a new one for now, 
            // or add an update endpoint.
            // Given the constraints, let's try delete then create.
            
            this.deleteService(this.editingService, true);
        },
        deleteService(service, isUpdate = false) {
            if (!isUpdate && !confirm('Are you sure you want to remove this service?')) return;
            
            AXIOS.put('/mechanic/removeService/'.concat(this.mechanicId), null, {
                params: {
                    serviceType: service.serviceType
                }
            })
            .then(() => {
                const index = this.services.findIndex(s => s.serviceType === service.serviceType);
                if (index !== -1) {
                    this.services.splice(index, 1);
                }
                if (isUpdate) {
                    this.createService();
                }
            })
            .catch(e => {
                this.error = e;
                console.log(e);
            });
        }
    }
}
</script>

<style scoped>
.mechanic-services-page {
    padding: 2rem;
    height: 100%;
    overflow-y: auto;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.page-title {
    font-size: 2rem;
    color: var(--text-primary);
    margin: 0;
}

.services-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
}

.service-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 2rem;
    text-align: center;
    position: relative;
    transition: transform 0.2s;
}

.service-card:hover {
    transform: translateY(-5px);
}

.service-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
    background: rgba(255, 255, 255, 0.05);
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
}

.service-info h3 {
    margin: 0 0 0.5rem 0;
    font-size: 1.2rem;
    color: var(--text-primary);
}

.price-badge {
    background: var(--primary);
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-weight: bold;
    font-size: 1.1rem;
}

.card-header-actions {
    position: absolute;
    top: 1rem;
    right: 1rem;
    display: flex;
    gap: 0.5rem;
    opacity: 0;
    transition: opacity 0.2s;
}

.service-card:hover .card-header-actions {
    opacity: 1;
}

.btn-icon {
    background: rgba(0, 0, 0, 0.3);
    border: none;
    border-radius: 50%;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: white;
    transition: all 0.2s;
}

.btn-icon:hover {
    background: var(--primary);
    transform: scale(1.1);
}

.btn-icon.delete:hover {
    background: #ef4444;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem;
    text-align: center;
}

.empty-icon {
    font-size: 4rem;
    margin-bottom: 1.5rem;
    opacity: 0.5;
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

.form-group {
    margin-bottom: 1.5rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    color: var(--text-secondary);
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}
</style>
