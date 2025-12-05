<template>
<div class="dashboard-container">
    <div class="sidebar glass-panel">
        <div class="sidebar-header">
            <div class="logo-circle">
                <span class="logo-icon">🔧</span>
            </div>
            <h2 class="brand-name">MecanoTun</h2>
        </div>

        <div class="nav-menu">
            <router-link :to="'/mechanicDashboard/Overview/' + userId" active-class="active" class="nav-item">
                <span class="nav-icon">📊</span>
                <span class="nav-text">Overview</span>
            </router-link>
            
            <router-link :to="'/mechanicDashboard/Schedule/' + userId" active-class="active" class="nav-item">
                <span class="nav-icon">📅</span>
                <span class="nav-text">Schedule</span>
            </router-link>

            <router-link :to="'/mechanicDashboard/Services/' + userId" active-class="active" class="nav-item">
                <span class="nav-icon">🛠️</span>
                <span class="nav-text">My Services</span>
            </router-link>
            
            <router-link :to="'/mechanicDashboard/myAccount/' + userId" active-class="active" class="nav-item">
                <span class="nav-icon">👤</span>
                <span class="nav-text">My Profile</span>
            </router-link>
        </div>

        <div class="sidebar-footer">
            <router-link to="/" class="nav-item logout">
                <span class="nav-icon">🚪</span>
                <span class="nav-text">Log Out</span>
            </router-link>
        </div>
    </div>

    <div class="main-content">
        <router-view />
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
    computed: {
        userId() {
            return this.$route.params.userId
        }
    },
    data() {
        return {
            mechanic: "",
            error: ""
        }
    },
    created: function () {
        var id = this.$route.params.userId
        AXIOS.get('/mechanic/'.concat(id))
            .then(response => {
                this.mechanic = response.data
            })
            .catch(e => {
                this.error = e
                console.log(e)
            })
    }
}
</script>

<style scoped>
.dashboard-container {
    display: flex;
    height: 100vh;
    width: 100vw;
    background: var(--bg-dark);
    overflow: hidden;
}

.sidebar {
    width: 280px;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 2rem;
    border-right: 1px solid rgba(255, 255, 255, 0.1);
    z-index: 10;
}

.sidebar-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 3rem;
}

.logo-circle {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, var(--primary), #FFA000);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 0 20px rgba(243, 190, 53, 0.3);
}

.logo-icon {
    font-size: 1.5rem;
}

.brand-name {
    font-family: 'Orbitron', sans-serif;
    font-size: 1.2rem;
    color: var(--text-primary);
    line-height: 1.2;
    margin: 0;
}

.nav-menu {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    flex: 1;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    color: var(--text-secondary);
    text-decoration: none;
    border-radius: var(--radius-sm);
    transition: all 0.3s ease;
    border: 1px solid transparent;
}

.nav-item:hover {
    background: rgba(255, 255, 255, 0.05);
    color: var(--text-primary);
    transform: translateX(5px);
}

.nav-item.active {
    background: rgba(243, 190, 53, 0.15);
    color: var(--primary);
    border-color: rgba(243, 190, 53, 0.3);
    box-shadow: 0 0 15px rgba(243, 190, 53, 0.1);
}

.nav-icon {
    font-size: 1.2rem;
    width: 24px;
    text-align: center;
}

.nav-text {
    font-weight: 500;
    font-size: 1rem;
}

.sidebar-footer {
    margin-top: auto;
    padding-top: 2rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.logout {
    color: #ff4d4d;
}

.logout:hover {
    background: rgba(255, 77, 77, 0.1);
    color: #ff6666;
}

.main-content {
    flex: 1;
    overflow-y: auto;
    background: radial-gradient(circle at top right, #1a1a2e, #000000);
    position: relative;
}

/* Scrollbar Styling */
.main-content::-webkit-scrollbar {
    width: 8px;
}

.main-content::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.3);
}

.main-content::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.2);
}
</style>
