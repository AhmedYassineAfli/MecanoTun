<template>
<div class="profile-page">
    <div class="profile-container glass-panel">
        <div class="profile-header">
            <div class="avatar-large">
                {{ mechanic.name ? mechanic.name.charAt(0).toUpperCase() : 'M' }}
            </div>
            <div class="header-info">
                <h1>{{ mechanic.name }}</h1>
                <StarRating 
                    v-if="rating !== undefined"
                    :rating="rating" 
                    :show-count="true"
                    :rating-count="ratingCount"
                    size="medium"
                />
                <span class="role-badge">Mechanic</span>
            </div>
            <button class="btn btn-primary edit-btn" @click="openEditModal">
                <span class="icon">✏️</span> Edit Profile
            </button>
        </div>

        <div class="profile-details">
            <div class="detail-group">
                <label>Email Address</label>
                <div class="detail-value">
                    <span class="icon">✉️</span>
                    {{ mechanic.email }}
                </div>
            </div>

            <div class="detail-group">
                <label>Phone Number</label>
                <div class="detail-value">
                    <span class="icon">📱</span>
                    {{ mechanic.phone }}
                </div>
            </div>

            <div class="detail-group">
                <label>Password</label>
                <div class="detail-value">
                    <span class="icon">🔒</span>
                    ••••••••
                </div>
            </div>

            <div class="detail-group full-width">
                <label>Capabilities (Services)</label>
                <div class="capabilities-list">
                    <span v-for="service in mechanic.services" :key="service.serviceType" class="capability-tag">
                        🔧 {{ service.serviceType }}
                    </span>
                    <span v-if="!mechanic.services || mechanic.services.length === 0" class="no-data">
                        No services assigned
                    </span>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Profile Modal -->
    <b-modal
        v-model="modalShow"
        title="Edit Profile"
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
                <label>Email (Read-only)</label>
                <input type="email" v-model="form.email" class="form-control" disabled>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <input type="tel" v-model="form.phone" class="form-control" required>
            </div>

            <div class="form-group">
                <label>New Password</label>
                <input type="password" v-model="form.password" class="form-control" required>
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
import StarRating from '../../components/StarRating.vue'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    components: { StarRating },
    data() {
        return {
            mechanic: {},
            rating: 0,
            ratingCount: 0,
            modalShow: false,
            form: {
                name: '',
                email: '',
                phone: '',
                password: ''
            },
            error: ''
        }
    },
    created() {
        this.fetchMechanic();
    },
    methods: {
        fetchMechanic() {
            var id = this.$route.params.userId;
            AXIOS.get('/mechanic/'.concat(id))
                .then(response => {
                    this.mechanic = response.data;
                    this.fetchRating(id);
                })
                .catch(e => {
                    this.error = e;
                    console.log(e);
                });
        },
        async fetchRating(mechanicId) {
            try {
                const response = await AXIOS.get(`/rating/mechanic/${mechanicId}/average`);
                this.rating = response.data.averageRating || 0;
                this.ratingCount = response.data.ratingCount || 0;
            } catch (e) {
                console.log('Error fetching rating:', e);
                this.rating = 0;
                this.ratingCount = 0;
            }
        },
        openEditModal() {
            this.form = {
                name: this.mechanic.name,
                email: this.mechanic.email,
                phone: this.mechanic.phone,
                password: this.mechanic.password
            };
            this.modalShow = true;
        },
        handleSubmit() {
            const { name, email, phone, password } = this.form;
            AXIOS.put('/mechanic/'.concat(email + "?name=" + name + "&password=" + password + "&phone=" + phone))
                .then(response => {
                    this.mechanic = response.data;
                    this.modalShow = false;
                    // Optional: Show success message
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
.profile-page {
    padding: 2rem;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    min-height: 100%;
}

.profile-container {
    width: 100%;
    max-width: 800px;
    padding: 3rem;
}

.profile-header {
    display: flex;
    align-items: center;
    gap: 2rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    margin-bottom: 2rem;
}

.avatar-large {
    width: 100px;
    height: 100px;
    background: linear-gradient(135deg, var(--primary), #FFA000);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
    font-weight: 700;
    color: #000;
    box-shadow: 0 0 30px rgba(243, 190, 53, 0.3);
}

.header-info h1 {
    font-size: 2.5rem;
    margin: 0 0 0.5rem 0;
}

.role-badge {
    background: rgba(255, 255, 255, 0.1);
    padding: 0.3rem 0.8rem;
    border-radius: 20px;
    font-size: 0.9rem;
    color: var(--text-secondary);
}

.edit-btn {
    margin-left: auto;
}

.profile-details {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
}

.detail-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.detail-group.full-width {
    grid-column: 1 / -1;
}

.detail-group label {
    color: var(--text-secondary);
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.detail-value {
    display: flex;
    align-items: center;
    gap: 1rem;
    font-size: 1.1rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-sm);
    border: 1px solid rgba(255, 255, 255, 0.05);
}

.detail-value .icon {
    opacity: 0.7;
}

.capabilities-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-sm);
    min-height: 60px;
}

.capability-tag {
    background: rgba(243, 190, 53, 0.15);
    color: var(--primary);
    padding: 0.4rem 1rem;
    border-radius: 20px;
    font-size: 0.9rem;
    border: 1px solid rgba(243, 190, 53, 0.3);
}

.no-data {
    color: var(--text-muted);
    font-style: italic;
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
    padding: 0.8rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    color: var(--text-primary);
    font-size: 1rem;
}

.form-control:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 2px rgba(243, 190, 53, 0.2);
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
</style>
