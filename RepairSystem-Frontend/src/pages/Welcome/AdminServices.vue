<template>
<div class="p-8">
    <div class="mb-8">
        <div class="header-content">
            <h1>Service Management</h1>
            <p>Manage service offerings and pricing</p>
        </div>
    </div>

    <!-- Services Grid -->
    <div class="grid-responsive">
        <div v-for="serviceType in serviceTypes" :key="serviceType" class="glass-card clickable" @click="viewServiceDetails(serviceType)">
            <div class="card-header">
                <div class="icon-wrapper">
                    {{ getServiceIcon(serviceType) }}
                </div>
                <div class="service-info">
                    <h3>{{ formatServiceName(serviceType) }}</h3>
                </div>
            </div>

            <div class="card-body">
                <div class="info-row">
                    <span class="label">Mechanics:</span>
                    <span class="value">{{ getMechanicCount(serviceType) }}</span>
                </div>
                <div class="info-row">
                    <span class="label">Appointments:</span>
                    <span class="value">{{ getAppointmentCount(serviceType) }}</span>
                </div>
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
            services: [],
            error: ''
        }
    },
    computed: {
        serviceTypes() {
            const types = new Set(this.services.map(s => s.serviceType));
            return Array.from(types);
        }
    },
    created() {
        this.fetchServices();
    },
    methods: {
        fetchServices() {
            AXIOS.get('/services')
                .then(response => {
                    this.services = response.data;
                })
                .catch(e => this.error = e);
        },
        formatServiceName(name) {
            if (!name) return '';
            // Add spaces before capital letters
            return name.replace(/([A-Z])/g, ' $1').trim();
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
        getMechanicCount(serviceType) {
            const servicesOfType = this.services.filter(s => s.serviceType === serviceType);
            const uniqueMechanics = new Set(
                servicesOfType
                    .filter(s => s.mechanic && s.mechanic.id)
                    .map(s => s.mechanic.id)
            );
            return uniqueMechanics.size;
        },
        getAppointmentCount(serviceType) {
            return this.services
                .filter(s => s.serviceType === serviceType)
                .reduce((total, service) => {
                    return total + (service.appointments ? service.appointments.length : 0);
                }, 0);
        },
        viewServiceDetails(serviceType) {
            this.$router.push({
                name: 'AdminServiceDetails',
                params: {
                    userId: this.$route.params.userId,
                    serviceType: serviceType
                }
            });
        }
    }
}
</script>

<style scoped>
.glass-card {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.glass-card.clickable {
    cursor: pointer;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.service-info h3 {
    font-size: 1.1rem;
    margin: 0;
}

.info-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
}

.info-row .label {
    color: var(--text-secondary);
}

.info-row .value {
    color: var(--text-primary);
    font-weight: 600;
}
</style>
