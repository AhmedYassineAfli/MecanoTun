<template>
<div class="admin-dashboard-layout">
    <aside class="sidebar glass-card">
        <div class="sidebar-header">
            <div class="logo-icon">MT</div>
            <span class="brand-text">Admin Portal</span>
        </div>
        
        <nav class="sidebar-nav">
            <router-link :to="'/adminDashboard/Overview/' + this.admin.id" active-class="active" class="nav-item">
                <span class="icon">📊</span>
                <span class="label">Overview</span>
            </router-link>
            
            <router-link :to="'/adminDashboard/myAccount/' + this.admin.id" active-class="active" class="nav-item">
                <span class="icon">👤</span>
                <span class="label">My Account</span>
            </router-link>
            
            <router-link :to="'/adminDashboard/TeamMembers/' + userId" active-class="active" class="nav-item">
                <span class="nav-icon">👥</span>
                <span class="nav-text">Mechanics</span>
            </router-link>
            
            <router-link :to="'/adminDashboard/Customers/' + this.admin.id" active-class="active" class="nav-item">
                <span class="icon">👥</span>
                <span class="label">Customers</span>
            </router-link>

            <router-link :to="'/adminDashboard/Services/' + this.admin.id" active-class="active" class="nav-item">
                <span class="icon">🏷️</span>
                <span class="label">Services</span>
            </router-link>
        </nav>

        <div class="sidebar-footer">
            <router-link to="/" class="nav-item logout">
                <span class="icon">🚪</span>
                <span class="label">Log Out</span>
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
            admin: "",
            error: ""
        }
    },
    created: function () {
        var id = this.$route.params.userId
        AXIOS.get('/admin/'.concat(id))
            .then(response => {
                this.admin = response.data
            })
            .catch(e => {
                this.error = e
                console.log(e)
            })
    }
}
</script>

<style scoped>
.admin-dashboard-layout {
    display: flex;
    min-height: 100vh;
    background-color: var(--bg-primary);
}

.sidebar {
    width: 280px;
    margin: 1rem;
    display: flex;
    flex-direction: column;
    border-radius: var(--radius-lg);
    padding: 2rem 1.5rem;
    position: sticky;
    top: 1rem;
    height: calc(100vh - 2rem);
    z-index: 10;
}

.sidebar-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 3rem;
    padding-bottom: 1.5rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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

.brand-text {
    font-size: 1.2rem;
    font-weight: 700;
    color: var(--text-primary);
}

.sidebar-nav {
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
    border-radius: var(--radius-md);
    transition: all 0.2s ease;
}

.nav-item:hover {
    background: rgba(255, 255, 255, 0.05);
    color: var(--text-primary);
}

.nav-item.active {
    background: var(--primary);
    color: #000;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.nav-item .icon {
    font-size: 1.2rem;
}

.sidebar-footer {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    padding-top: 1.5rem;
}

.nav-item.logout:hover {
    background: rgba(239, 68, 68, 0.1);
    color: var(--error);
}

.main-content {
    flex: 1;
    padding: 1rem;
    overflow-y: auto;
}

@media (max-width: 900px) {
    .sidebar {
        width: 80px;
        padding: 1.5rem 0.75rem;
    }
    
    .brand-text, .label {
        display: none;
    }
    
    .sidebar-header {
        justify-content: center;
    }
    
    .nav-item {
        justify-content: center;
    }
}
</style>
