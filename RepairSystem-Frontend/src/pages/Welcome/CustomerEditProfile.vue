<template>
<div class="profile-page">
    <div class="profile-container glass-card">
        <div class="profile-header">
            <div class="avatar-large">
                {{ customer.name ? customer.name.charAt(0).toUpperCase() : 'U' }}
            </div>
            <h1>{{ customer.name }}</h1>
            <p class="user-role">Customer Account</p>
        </div>

        <div class="profile-details-grid">
            <div class="detail-item">
                <label>Email Address</label>
                <div class="value">{{ customer.email }}</div>
            </div>
            <div class="detail-item">
                <label>Phone Number</label>
                <div class="value">{{ customer.phone }}</div>
            </div>
            <div class="detail-item full-width">
                <label>Residential Address</label>
                <div class="value">{{ customer.address }}</div>
            </div>
        </div>

        <div class="profile-actions">
            <button class="btn btn-primary" @click="openEditModal">
                Edit Profile
            </button>
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
                <input type="text" v-model="editName" class="input" required>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <input type="text" v-model="editPhone" class="input" required>
            </div>

            <div class="form-group">
                <label>Address</label>
                <input type="text" v-model="editAddress" class="input" required>
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password" v-model="editPassword" class="input" required>
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
            customer: {},
            modalShow: false,
            // Edit fields
            editName: "",
            editPhone: "",
            editPassword: "",
            editAddress: "",
            error: ""
        }
    },
    created() {
        this.fetchCustomer();
    },
    methods: {
        fetchCustomer() {
            var id = this.$route.params.userId
            AXIOS.get('/customer/'.concat(id))
                .then(response => {
                    this.customer = response.data
                })
                .catch(e => {
                    this.error = e
                    console.log(e)
                })
        },
        openEditModal() {
            this.editName = this.customer.name;
            this.editPhone = this.customer.phone;
            this.editPassword = this.customer.password;
            this.editAddress = this.customer.address;
            this.modalShow = true;
        },
        handleSubmit() {
            // Construct query params for the PUT request
            const params = `?newName=${this.editName}&newPassword=${this.editPassword}&newPhone=${this.editPhone}&newAddress=${this.editAddress}`;
            
            AXIOS.put('/customer/'.concat(this.customer.email + params))
                .then(response => {
                    this.customer = response.data;
                    this.modalShow = false;
                })
                .catch(e => {
                    this.error = e;
                    console.error(e);
                })
        }
    }
}
</script>

<style scoped>
.profile-page {
    padding: 2rem;
    min-height: 100%;
    display: flex;
    justify-content: center;
    align-items: flex-start;
}

.profile-container {
    width: 100%;
    max-width: 800px;
    padding: 3rem;
}

.profile-header {
    text-align: center;
    margin-bottom: 3rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 2rem;
}

.avatar-large {
    width: 100px;
    height: 100px;
    background: var(--primary);
    color: #000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 3rem;
    font-weight: 700;
    margin: 0 auto 1.5rem;
    box-shadow: 0 0 20px rgba(245, 158, 11, 0.3);
}

.profile-header h1 {
    margin-bottom: 0.5rem;
    font-size: 2rem;
}

.user-role {
    color: var(--text-secondary);
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 1px;
}

.profile-details-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2rem;
    margin-bottom: 3rem;
}

.detail-item {
    background: rgba(255, 255, 255, 0.03);
    padding: 1.5rem;
    border-radius: var(--radius-md);
    border: 1px solid rgba(255, 255, 255, 0.05);
}

.detail-item.full-width {
    grid-column: 1 / -1;
}

.detail-item label {
    display: block;
    color: var(--text-secondary);
    font-size: 0.85rem;
    margin-bottom: 0.5rem;
}

.detail-item .value {
    font-size: 1.1rem;
    font-weight: 500;
    color: var(--text-primary);
}

.detail-item .value.masked {
    font-family: monospace;
    letter-spacing: 2px;
}

.profile-actions {
    display: flex;
    justify-content: center;
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

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}

@media (max-width: 768px) {
    .profile-details-grid {
        grid-template-columns: 1fr;
    }
}
</style>
