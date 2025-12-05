<template>
<div class="service-details-page">
    <div class="page-header">
        <button class="btn-back" @click="goBack">← Back to Services</button>
        <div class="header-content">
            <h1>{{ formatServiceName(serviceType) }}</h1>
            <p>Manage mechanics and pricing for this service</p>
        </div>
    </div>

    <!-- Mechanics List -->
    <div class="mechanics-list" v-if="mechanics.length > 0">
        <div v-for="service in mechanics" :key="service.id" class="mechanic-card glass-card">
            <div class="mechanic-info">
                <div class="mechanic-avatar">👤</div>
                <div>
                    <h3>{{ service.mechanic ? service.mechanic.name : 'Unknown Mechanic' }}</h3>
                    <p class="mechanic-email" v-if="service.mechanic">{{ service.mechanic.email }}</p>
                    <p class="mechanic-rating" v-if="service.mechanic && service.mechanic.averageRating">
                        <span class="stars">⭐</span> {{ service.mechanic.averageRating.toFixed(1) }}
                        <span class="rating-count">({{ service.mechanic.ratingCount }})</span>
                    </p>
                    <p class="mechanic-rating not-rated" v-else>Not Rated</p>
                </div>
            </div>
            <div class="price-section">
                <div class="price-display">
                    <span class="price-label">Price:</span>
                    <span class="price-value">{{ service.price }} DT</span>
                </div>
                <button class="btn btn-primary btn-sm" @click="openEditModal(service)">
                    Edit Price
                </button>
            </div>
        </div>
    </div>

    <div v-else class="empty-state glass-card">
        <div class="empty-icon">👥</div>
        <h3>No mechanics offering this service</h3>
        <p>Mechanics can add this service from their dashboard.</p>
    </div>

    <!-- Edit Price Modal -->
    <b-modal
        v-model="modalShow"
        title="Edit Service Price"
        header-class="modal-header-custom"
        body-class="modal-body-custom"
        footer-class="modal-footer-custom"
        centered
        hide-footer
        append-to-body
    >
        <form @submit.prevent="updatePrice">
            <div class="form-group">
                <label>Mechanic</label>
                <input type="text" :value="currentService.mechanic ? currentService.mechanic.name : ''" class="form-control" disabled>
            </div>
            <div class="form-group">
                <label>Service</label>
                <input type="text" :value="formatServiceName(serviceType)" class="form-control" disabled>
            </div>
            <div class="form-group">
                <label>Price (DT)</label>
                <input type="number" v-model="newPrice" class="form-control" min="0" step="0.01" required>
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
            serviceType: '',
            mechanics: [],
            modalShow: false,
            currentService: {},
            newPrice: 0,
            error: ''
        }
    },
    created() {
        this.serviceType = this.$route.params.serviceType;
        this.fetchMechanics();
    },
    methods: {
        fetchMechanics() {
            AXIOS.get('/services')
                .then(response => {
                    // Filter services by type AND ensure they have a mechanic assigned
                    this.mechanics = response.data.filter(s => 
                        s.serviceType === this.serviceType && s.mechanic && s.mechanic.id
                    );
                })
                .catch(e => {
                    console.error('Error fetching mechanics:', e);
                });
        },
        formatServiceName(name) {
            if (!name) return '';
            return name.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
        },
        openEditModal(service) {
            this.currentService = service;
            this.newPrice = service.price;
            this.modalShow = true;
        },
        updatePrice() {
            // To update price, we need to remove the service and add it back with new price
            const mechanicId = this.currentService.mechanic.id;
            const serviceType = this.currentService.serviceType;
            
            // First remove the service
            AXIOS.put(`/mechanic/removeService/${mechanicId}`, null, {
                params: { serviceType: serviceType }
            })
            .then(() => {
                // Then add it back with the new price
                return AXIOS.post(`/service/${mechanicId}`, null, {
                    params: { type: serviceType, price: this.newPrice }
                });
            })
            .then(() => {
                this.fetchMechanics();
                this.modalShow = false;
            })
            .catch(e => {
                this.error = e;
                console.error('Error updating price:', e);
                alert('Failed to update price. Please try again.');
            });
        },
        goBack() {
            this.$router.back();
        }
    }
}
</script>

<style scoped>
.service-details-page {
    padding: 2rem;
}

.page-header {
    margin-bottom: 2rem;
}

.btn-back {
    background: none;
    border: none;
    color: var(--primary);
    cursor: pointer;
    font-size: 1rem;
    padding: 0;
    margin-bottom: 1rem;
    display: block;
}

.btn-back:hover {
    text-decoration: underline;
}

.header-content h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.header-content p {
    color: var(--text-secondary);
}

.mechanics-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.mechanic-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    position: relative;
    z-index: 1;
}

.mechanic-info {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.mechanic-avatar {
    width: 60px;
    height: 60px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
}

.mechanic-info h3 {
    margin: 0 0 0.25rem 0;
    font-size: 1.2rem;
}

.mechanic-email {
    color: var(--text-secondary);
    font-size: 0.9rem;
    margin: 0 0 0.25rem 0;
}

.mechanic-rating {
    color: #F3BE35;
    font-size: 0.95rem;
    margin: 0;
    font-weight: 600;
}

.mechanic-rating.not-rated {
    color: var(--text-secondary);
    font-style: italic;
    font-weight: normal;
    font-size: 0.85rem;
}

.rating-count {
    color: var(--text-secondary);
    font-size: 0.85rem;
    font-weight: normal;
    margin-left: 0.25rem;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 1.5rem;
}

.price-display {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.price-label {
    color: var(--text-secondary);
    font-size: 0.85rem;
    margin-bottom: 0.25rem;
}

.price-value {
    font-size: 1.8rem;
    font-weight: bold;
    color: var(--primary);
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

/* Modal Styles */
.form-group {
    margin-bottom: 1.5rem;
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
    border-radius: 8px;
    color: var(--text-primary);
    font-size: 1rem;
}

.form-control:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

.btn-sm {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
}

/* Ensure modal appears above all other elements */
::v-deep .modal {
    z-index: 10000 !important;
}

::v-deep .modal-backdrop {
    z-index: 9999 !important;
    opacity: 0.8 !important;
    background-color: rgba(0, 0, 0, 0.8) !important;
}

::v-deep .modal-dialog {
    z-index: 10001 !important;
}
</style>
