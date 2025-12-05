<template>
    <div class="my-cars-page">
        <div class="page-header">
            <h1 class="page-title">My Garage</h1>
            <button class="btn btn-primary" @click="$bvModal.show('modal-prevent-closing')">
                <span class="font-bold mr-2">+</span> Add New Vehicle
            </button>
        </div>

     
        <div v-if="cars.length > 0" class="grid-responsive">
            <div v-for="car in cars" :key="car.id" class="glass-card car-card" :class="{ 'border-warning': car.serviceMechanic }">
             
                <div class="card-actions">
                    <button class="btn-icon delete" @click="deleteCar(car)" :disabled="!car.isDeletable" title="Delete Car">
                        🗑️
                    </button>
                </div>

                <div class="car-content">
                    <div class="icon-wrapper-lg mb-4">
                        🚗
                    </div>

                    <div class="mb-4">
                        <h2 class="text-2xl text-primary mb-2">{{ car.brand }} {{ car.model }}</h2>
                        <p class="text-secondary text-sm">{{ car.year }} • {{ car.engine }}</p>
                    </div>

                    <div class="car-stats">
                        <div class="stat-row">
                            <span class="text-secondary">VIN</span>
                            <span class="font-bold">{{ car.vin || 'N/A' }}</span>
                        </div>
                        <div class="stat-row">
                            <span class="text-secondary">Status</span>
                            <span v-if="car.serviceMechanic" class="text-warning font-bold">In Service</span>
                            <span v-else class="text-success font-bold">Available</span>
                        </div>
                    </div>

                    <div v-if="car.serviceMechanic" class="service-badge">
                        Currently with {{ car.serviceMechanic }}
                    </div>
                </div>
            </div>
        </div>

       
        <div v-else class="empty-state">
            <div class="text-6xl mb-6 opacity-50">🚗</div>
            <h3 class="text-2xl mb-2">No Vehicles Added</h3>
            <p class="mb-6">Add your first vehicle to book appointments</p>
            <button class="btn btn-primary" @click="$bvModal.show('modal-prevent-closing')">
                Add New Vehicle
            </button>
        </div>

       
        <b-modal id="modal-prevent-closing" ref="modal" title="Add New Vehicle"
            @show="resetModal" @hidden="resetModal" @ok="handleSubmit" centered
            header-class="modal-header"
            footer-class="modal-footer"
            content-class="modal-content">
            
            <form ref="form" @submit.stop.prevent="handleSubmit">
                <div class="mb-4">
                    <label class="label">Vehicle Type</label>
                    <VehicleSelector @vehicle-selected="handleVehicleSelected" />
                </div>
                
             
            </form>
        </b-modal>
    </div>
</template>

<script>
import axios from 'axios'
import VehicleSelector from '../../components/VehicleSelector'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    components: { VehicleSelector },
    data() {
        return {
            customer: "",
            cars: [],
            error: "",
            // Modal data
            brand: '',
            model: '',
            engine: '',
            vinNumber: '',
            yearModel: null,

        }
    },
    created: function () {
        var id = this.$route.params.userId
        
        AXIOS.get('/customer/'.concat(id))
        .then(response => {
            this.customer = response.data
            this.fetchCars(id);
        })
        .catch(e => {
            this.error = e
            console.log(e)
        })
    },
    methods: {
        async fetchCars(id) {
            try {
                const response = await AXIOS.get('/cars/'.concat(id));
                const cars = response.data;
                
                // Check availability for each car
                for (let car of cars) {
                    this.$set(car, 'isDeletable', true);
                    this.$set(car, 'serviceMechanic', '');
                    
                    try {
                        // Check if car is deletable (not in service/completed)
                        const availResponse = await AXIOS.get(`/appointment/car-deletable/${car.id}`);
                        this.$set(car, 'isDeletable', availResponse.data);
                        
                        // Get service mechanic if any (for display purposes)
                        const mechanicResponse = await AXIOS.get(`/appointment/car-service-mechanic/${car.id}`);
                        this.$set(car, 'serviceMechanic', mechanicResponse.data || '');
                    } catch (e) {
                        console.log('Error checking car status:', e);
                    }
                }
                
                this.cars = cars;
            } catch (e) {
                this.error = e;
                console.log(e);
            }
        },
        handleVehicleSelected(vehicleData) {
            this.brand = vehicleData.brand
            this.model = vehicleData.model
            this.engine = vehicleData.engine
            this.vinNumber = vehicleData.vin || ''
            this.yearModel = vehicleData.year
        },
        createCar: function() {
            return AXIOS.post('/car/'.concat(this.customer.id), null, {
                params: {
                    brand: this.brand,
                    model: this.model,
                    engine: this.engine,
                    vin: this.vinNumber || null,
                    year: this.yearModel || null
                }
            })
            .then(response => {
                this.cars.push(response.data)
            })
            .catch(e => {
                this.error = e
                console.log(e)
                throw e  // Re-throw to prevent modal from closing
            })
        },
        async deleteCar(car) {
            // Check if car is available for deletion
            if (!car.isDeletable) {
                this.$bvModal.msgBoxOk(
                    'This car is currently in service. Cannot delete at this stage.',
                    {
                        title: 'Cannot Delete Car',
                        size: 'md',
                        buttonSize: 'sm',
                        okVariant: 'danger',
                        headerClass: 'p-2 border-bottom-0',
                        footerClass: 'p-2 border-top-0',
                        centered: true
                    }
                );
                return;
            }
            
            try {
                // Fetch appointments that will be deleted
                const response = await AXIOS.get(`/car/${car.id}/appointments`);
                const appointments = response.data;
                
                let message = `Are you sure you want to delete ${car.brand} ${car.model}?`;
                
                if (appointments.length > 0) {
                    message += '\n\n⚠️ This will also cancel the following appointment(s):\n\n';
                    appointments.forEach(app => {
                        const date = new Date(app.timeSlot.startTime).toLocaleDateString('en-US', {
                            weekday: 'short',
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric'
                        });
                        const time = new Date(app.timeSlot.startTime).toLocaleTimeString('en-US', {
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                        const mechanicName = app.mechanics && app.mechanics.length > 0 ? app.mechanics[0].name : 'Unassigned';
                        message += `• ${date} at ${time} with ${mechanicName}\n`;
                    });
                }
                
                const confirmed = await this.$bvModal.msgBoxConfirm(message, {
                    title: 'Delete Car',
                    size: 'md',
                    buttonSize: 'sm',
                    okVariant: 'danger',
                    okTitle: 'Delete',
                    cancelTitle: 'Cancel',
                    footerClass: 'p-2',
                    hideHeaderClose: false,
                    centered: true
                });
                
                if (!confirmed) return;
                
                await AXIOS.delete('/car/'.concat(car.id));
                const index = this.cars.findIndex(c => c.id === car.id);
                if (index !== -1) {
                    this.cars.splice(index, 1);
                }
                
                this.$bvModal.msgBoxOk(
                    'Car deleted successfully' + (appointments.length > 0 ? ` and ${appointments.length} appointment(s) cancelled.` : '.'),
                    {
                        title: 'Success',
                        size: 'sm',
                        buttonSize: 'sm',
                        okVariant: 'success',
                        headerClass: 'p-2 border-bottom-0',
                        footerClass: 'p-2 border-top-0',
                        centered: true
                    }
                );
            } catch (e) {
                this.error = e;
                console.log(e);
                this.$bvModal.msgBoxOk(
                    'Failed to delete car: ' + (e.response && e.response.data ? e.response.data : e.message),
                    {
                        title: 'Error',
                        size: 'sm',
                        buttonSize: 'sm',
                        okVariant: 'danger',
                        headerClass: 'p-2 border-bottom-0',
                        footerClass: 'p-2 border-top-0',
                        centered: true
                    }
                );
            }
        },
        resetModal() {
            this.brand = ''
            this.model = ''
            this.engine = ''
            this.vinNumber = ''
            this.yearModel = null
        },
        async handleSubmit() {
            if (!this.brand || !this.model || !this.engine) {
                return;
            }
            
            try {
                await this.createCar();
                
                // Only close modal if save was successful
                this.$nextTick(() => {
                    this.$bvModal.hide('modal-prevent-closing')
                })
            } catch (error) {
                // Error already logged, modal stays open
            }
        },

    }
}
</script>

<style scoped>
.my-cars-page {
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

.car-card {
    position: relative;
    display: flex;
    flex-direction: column;
    padding: 1.5rem;
}

.car-content {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.card-actions {
    position: absolute;
    top: 1rem;
    right: 1rem;
    display: flex;
    gap: 0.5rem;
    opacity: 0;
    transition: opacity 0.2s;
}

.car-card:hover .card-actions {
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

.btn-icon:disabled {
    opacity: 0.3;
    cursor: not-allowed;
    pointer-events: none;
}

.stat-row {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 0.5rem;
    margin-bottom: 0.5rem;
}

.stat-row:last-child {
    border-bottom: none;
    margin-bottom: 0;
}

.service-badge {
    margin-top: 1rem;
    padding: 0.5rem;
    background: rgba(245, 158, 11, 0.2);
    border: 1px solid rgba(245, 158, 11, 0.4);
    border-radius: 6px;
    color: var(--warning);
    text-align: center;
    font-size: 0.875rem;
    font-weight: 700;
}

.border-warning {
    border-color: rgba(245, 158, 11, 0.5) !important;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem;
    text-align: center;
}
</style>
