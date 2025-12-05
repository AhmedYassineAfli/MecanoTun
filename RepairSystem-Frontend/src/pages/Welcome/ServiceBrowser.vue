<template>
<div class="service-browser-page">
    <div class="page-header">
        <h1 class="page-title">Browse Services</h1>
        <div class="controls">
            <div class="search-box">
                <span class="search-icon">🔍</span>
                <input type="text" v-model="searchQuery" placeholder="Search services..." class="search-input">
            </div>
        </div>
    </div>

    <!-- View 1: List of Service Types -->
    <div class="services-grid" v-if="!selectedType && filteredServiceTypes.length > 0">
        <div v-for="type in filteredServiceTypes" :key="type" class="service-card glass-card" @click="selectType(type)">
            <div class="service-icon">
                {{ getServiceIcon(type) }}
            </div>
            <div class="service-info">
                <h3>{{ formatServiceName(type) }}</h3>
                <p class="service-count">{{ getMechanicCount(type) }} Mechanics Available</p>
            </div>
        </div>
    </div>

    <!-- View 2: List of Mechanics for Selected Type -->
    <div class="mechanics-list" v-if="selectedType">
        <div class="list-header">
            <button class="btn-back" @click="selectedType = null">← Back to Services</button>
            <h2>{{ formatServiceName(selectedType) }} Providers</h2>
        </div>

        <div class="mechanic-items">
            <div v-for="service in getServicesByType(selectedType)" :key="service.id" class="mechanic-card glass-card">
                <div class="mechanic-info">
                    <div class="mechanic-avatar">👤</div>
                    <div>
                        <h3>{{ service.mechanic ? service.mechanic.name : 'Unknown Mechanic' }}</h3>
                        <p class="mechanic-rating" v-if="service.mechanic && service.mechanic.averageRating">
                            <span class="stars">⭐</span> {{ service.mechanic.averageRating.toFixed(1) }} 
                            <span class="rating-count">({{ service.mechanic.ratingCount }})</span>
                        </p>
                        <p class="mechanic-rating not-rated" v-else>
                            Not Rated
                        </p>
                    </div>
                </div>
                <div class="service-price-action">
                    <div class="price-tag">{{ service.price }} DT</div>
                    <button @click="bookService(service)" class="btn btn-primary">
                        Book Now
                    </button>
                </div>
            </div>
        </div>
    </div>
    
    <div v-if="!selectedType && filteredServiceTypes.length === 0" class="empty-state glass-card">
        <div class="empty-icon">🔍</div>
        <h3>No services found</h3>
        <p>Try adjusting your search or check back later.</p>
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
            services: [],
            searchQuery: '',
            selectedType: null,
            error: ''
        }
    },
    computed: {
        uniqueServiceTypes() {
            const types = new Set(this.services.map(s => s.serviceType));
            return Array.from(types);
        },
        filteredServiceTypes() {
            let types = this.uniqueServiceTypes;
            if (this.searchQuery) {
                const query = this.searchQuery.toLowerCase();
                types = types.filter(t => t.toLowerCase().includes(query));
            }
            return types;
        }
    },
    created() {
        this.fetchAllServices();
    },
    methods: {
        fetchAllServices() {
            AXIOS.get('/services')
                .then(response => {
                    this.services = response.data;
                })
                .catch(e => {
                    this.error = e;
                    console.log(e);
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
                'DIAGNOSTIC': '🔍',
                'REVISION': '✅',
                'SUSPENSION': '🔩'
            };
            return icons[name] || '🔧';
        },
        getServicesByType(type) {
            return this.services.filter(s => s.serviceType === type && s.mechanic && s.mechanic.id);
        },
        getMechanicCount(type) {
            const servicesOfType = this.getServicesByType(type);
            const uniqueMechanics = new Set(
                servicesOfType
                    .filter(s => s.mechanic && s.mechanic.id)
                    .map(s => s.mechanic.id)
            );
            return uniqueMechanics.size;
        },
        selectType(type) {
            this.selectedType = type;
        },
        bookService(service) {
            this.$router.push({
                name: 'BookAppointment',
                params: { 
                    userId: this.$route.params.userId,
                    preselectedMechanicId: service.mechanic ? service.mechanic.id : null,
                    preselectedServiceType: service.serviceType
                }
            });
        }
    }
}
</script>

<style scoped>
.service-browser-page {
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
    cursor: pointer;
    transition: transform 0.2s, background 0.2s;
}

.service-card:hover {
    transform: translateY(-5px);
    background: rgba(255, 255, 255, 0.1);
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

.service-count {
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.list-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
}

.btn-back {
    background: none;
    border: none;
    color: var(--primary);
    cursor: pointer;
    font-size: 1rem;
    padding: 0;
}

.btn-back:hover {
    text-decoration: underline;
}

.mechanic-items {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.mechanic-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
}

.mechanic-info {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.mechanic-avatar {
    width: 50px;
    height: 50px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
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

.service-price-action {
    display: flex;
    align-items: center;
    gap: 1.5rem;
}

.price-tag {
    font-size: 1.5rem;
    font-weight: bold;
    color: var(--primary);
}

.search-input {
    padding-left: 35px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    color: white;
    padding-right: 15px;
    height: 40px;
    width: 250px;
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
</style>
