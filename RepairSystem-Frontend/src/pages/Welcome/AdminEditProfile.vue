<template>
<div class="admin-profile-page">
    <div class="profile-container glass-card">
        <!-- Profile Header -->
        <div class="profile-header">
            <div class="avatar-circle">
                {{ admin.name ? admin.name.charAt(0).toUpperCase() : 'A' }}
            </div>
            <h1>{{ admin.name }}</h1>
            <p class="role-badge">Administrator</p>
        </div>

        <!-- Profile Details Grid -->
        <div class="profile-details-grid">
            <div class="detail-item">
                <span class="label">Email Address</span>
                <div class="value-box">
                    <span class="icon">✉️</span>
                    {{ admin.email }}
                </div>
            </div>

            <div class="detail-item">
                <span class="label">Phone Number</span>
                <div class="value-box">
                    <span class="icon">📱</span>
                    {{ admin.phone }}
                </div>
            </div>

            <div class="detail-item">
                <span class="label">Password</span>
                <div class="value-box">
                    <span class="icon">🔒</span>
                    ••••••••
                </div>
            </div>

            <div class="detail-item">
                <span class="label">Admin ID</span>
                <div class="value-box">
                    <span class="icon">🆔</span>
                    #{{ admin.id }}
                </div>
            </div>
        </div>

        <!-- Actions -->
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
        <form @submit.prevent="saveProfile">
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" v-model="editName" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Email Address</label>
                <input type="email" v-model="editEmail" class="form-control" disabled>
                <small class="text-muted">Email cannot be changed</small>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <input type="tel" v-model="editPhone" class="form-control" required>
            </div>

            <div class="form-group">
                <label>New Password</label>
                <input type="password" v-model="editPassword" class="form-control" placeholder="Leave blank to keep current">
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
            admin: {},
            modalShow: false,
            editName: '',
            editEmail: '',
            editPhone: '',
            editPassword: '',
            error: ''
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            var id = this.$route.params.userId
            AXIOS.get('/admin/'.concat(id))
                .then(response => {
                    this.admin = response.data
                })
                .catch(e => {
                    this.error = e
                    console.error(e)
                })
        },
        openEditModal() {
            this.editName = this.admin.name;
            this.editEmail = this.admin.email;
            this.editPhone = this.admin.phone;
            this.editPassword = this.admin.password; // Pre-fill or leave empty? Original pre-filled.
            this.modalShow = true;
        },
        saveProfile() {
            // Logic from original JS: AXIOS.put('/admin/'.concat(email+"?name="+name+"&password="+password+"&phone="+phone)...
            const params = `?name=${this.editName}&password=${this.editPassword}&phone=${this.editPhone}`;
            
            AXIOS.put('/admin/'.concat(this.editEmail + params))
                .then(response => {
                    this.admin = response.data;
                    this.modalShow = false;
                    // Optional: Show success toast
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
.admin-profile-page {
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
    text-align: center;
    margin-bottom: 3rem;
    padding-bottom: 2rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.avatar-circle {
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
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.role-badge {
    display: inline-block;
    padding: 0.25rem 1rem;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    font-size: 0.9rem;
    color: var(--text-secondary);
}

.profile-details-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 2rem;
    margin-bottom: 3rem;
}

.detail-item {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.label {
    color: var(--text-secondary);
    font-size: 0.9rem;
}

.value-box {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.05);
    border-radius: var(--radius-md);
    color: var(--text-primary);
    font-size: 1.1rem;
}

.icon {
    font-size: 1.2rem;
    opacity: 0.8;
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

.form-control {
    width: 100%;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: var(--radius-sm);
    color: var(--text-primary);
}

.form-control:focus {
    border-color: var(--primary);
    outline: none;
}

.form-control:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.text-muted {
    display: block;
    margin-top: 0.25rem;
    font-size: 0.8rem;
    color: var(--text-muted);
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 2rem;
}
</style>
