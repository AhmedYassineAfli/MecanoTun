<template>
<div class="customerDashboard">
    <aside class="sidebar glass-card">
        <div class="logo-container">
            <router-link to="/" class="logo-link">
                <div class="logo-icon">MT</div>
                <span class="logo-text">MecanoTun</span>
            </router-link>
        </div>
        
        <nav class="nav-menu">
            <router-link :to="'/customerDashboard/Overview/' + this.$route.params.userId" active-class="active" class="nav-item">
                <i class="icon">📊</i>
                <span>Overview</span>
            </router-link>
            
            <router-link :to="'/customerDashboard/myCars/' + this.$route.params.userId" active-class="active" class="nav-item">
                <i class="icon">🚗</i>
                <span>My Cars</span>
            </router-link>

            <router-link :to="'/customerDashboard/services/' + this.$route.params.userId" active-class="active" class="nav-item">
                <i class="icon">🔍</i>
                <span>Browse Services</span>
            </router-link>
            
            <router-link :to="'/customerDashboard/bookAppointment/' + this.$route.params.userId" active-class="active" class="nav-item">
                <i class="icon">📅</i>
                <span>Book Appointment</span>
            </router-link>
            
            <router-link :to="'/customerDashboard/myAccount/' + this.$route.params.userId" active-class="active" class="nav-item">
                <i class="icon">👤</i>
                <span>My Profile</span>
            </router-link>
        </nav>
        
        <div class="logout-container">
            <router-link to="/" class="nav-item logout">
                <i class="icon">🚪</i>
                <span>Log Out</span>
            </router-link>
        </div>
    </aside>
    
    <main class="main-content">
        <router-view />
    </main>
</div>
</template>

<script>
import axios from 'axios'
var config = require('../../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = config.dev.backendHost
var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: {
        'Access-Control-Allow-Origin': frontendUrl
    }
})

export default {
    data() {
        return {
            customer: "",
            error: ""
        }
    },
    created: function () {
        var id = this.$route.params.userId
        AXIOS.get('/customer/'.concat(id))
            .then(response => {
                this.customer = response.data
            })
            .catch(e => {
                this.error = e
                console.log(e)
            })
    }
}
</script>

<style scoped>
.customerDashboard {
    display: flex;
    height: 100vh;
    width: 100vw;
    background-color: var(--bg-primary);
    overflow: hidden;
}

.sidebar {
    width: 280px;
    height: 96vh;
    margin: 2vh;
    display: flex;
    flex-direction: column;
    border-radius: var(--radius-lg);
    border: var(--glass-border);
    background: rgba(30, 41, 59, 0.4); /* Slightly more transparent than standard glass card */
}

.logo-container {
    padding: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.logo-link {
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.logo-icon {
    width: 40px;
    height: 40px;
    background: var(--primary);
    color: #000;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 800;
    font-size: 1.2rem;
}

.logo-text {
    color: var(--text-primary);
    font-weight: 700;
    font-size: 1.2rem;
    letter-spacing: -0.5px;
}

.nav-menu {
    flex: 1;
    padding: 2rem 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.nav-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem 1.5rem;
    color: var(--text-secondary);
    text-decoration: none;
    border-radius: var(--radius-md);
    transition: all var(--transition-fast);
    font-weight: 500;
}

.nav-item:hover {
    background: rgba(255, 255, 255, 0.05);
    color: var(--text-primary);
    transform: translateX(5px);
}

.nav-item.active {
    background: rgba(245, 158, 11, 0.15); /* Amber with low opacity */
    color: var(--primary);
    border-left: 3px solid var(--primary);
}

.nav-item .icon {
    font-style: normal;
    font-size: 1.2rem;
}

.logout-container {
    padding: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.logout {
    color: var(--error);
}

.logout:hover {
    background: rgba(239, 68, 68, 0.1);
    color: var(--error);
}

.main-content {
    flex: 1;
    padding: 2vh;
    overflow-y: auto;
    position: relative;
}

/* Scrollbar styling */
::-webkit-scrollbar {
    width: 8px;
}

::-webkit-scrollbar-track {
    background: transparent;
}

::-webkit-scrollbar-thumb {
    background: var(--bg-tertiary);
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: var(--text-muted);
}
</style>
