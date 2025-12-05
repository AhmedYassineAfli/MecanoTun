<template>
    <div class="signup-page">
        <div class="background-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
        </div>

        <div class="signup-container glass-card animate-fade-in">
            <div class="signup-header">
                <div class="logo-icon">MT</div>
                <h1>Create Account</h1>
                <p>Join MecanoTun today</p>
            </div>

            <form @submit.prevent="createCustomer(newEmail, newPassword, newName, newNumber, newAddress)">
                <div class="form-grid">
                    <div class="input-group">
                        <label for="name" class="label">Full Name</label>
                        <input type="text" v-model="newName" class="input" required>
                    </div>

                    <div class="input-group">
                        <label for="email" class="label">Email Address</label>
                        <input type="email" v-model="newEmail" class="input" required>
                    </div>

                    <div class="input-group">
                        <label for="password" class="label">Password</label>
                        <input type="password" v-model="newPassword" class="input" required>
                    </div>

                    <div class="input-group">
                        <label for="repeat-password" class="label">Confirm Password</label>
                        <input type="password" v-model="newRepeatPassword" class="input" required>
                    </div>

                    <div class="input-group">
                        <label for="number" class="label">Phone Number</label>
                        <input type="tel" v-model="newNumber" class="input" required>
                    </div>

                    <div class="input-group">
                        <label for="address" class="label">Address</label>
                        <input type="text" v-model="newAddress" class="input" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block mt-4">
                    Sign Up
                </button>
            </form>

            <div v-if="error" class="error-message">
                {{ error }}
            </div>

            <div class="signup-footer">
                <p>Already have an account? <router-link to="/login" class="link">Log In</router-link></p>
                <router-link to="/" class="link-muted">Back to Home</router-link>
            </div>
        </div>
    </div>
</template>

<script src="./SignUp.js">
</script>

<style scoped>
.signup-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
    background-color: var(--bg-primary);
    padding: 2rem;
}

.background-shapes {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    overflow: hidden;
}

.shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.4;
}

.shape-1 {
    top: -10%;
    right: -10%;
    width: 600px;
    height: 600px;
    background: var(--primary);
}

.shape-2 {
    bottom: -10%;
    left: -10%;
    width: 500px;
    height: 500px;
    background: var(--secondary);
}

.signup-container {
    width: 100%;
    max-width: 600px; /* Wider than login for grid */
    position: relative;
    z-index: 1;
    padding: 3rem;
}

.signup-header {
    text-align: center;
    margin-bottom: 2rem;
}

.logo-icon {
    width: 60px;
    height: 60px;
    background: var(--primary);
    color: #000;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 800;
    font-size: 1.5rem;
    margin: 0 auto 1.5rem;
    box-shadow: 0 10px 25px rgba(245, 158, 11, 0.3);
}

.signup-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}

@media (max-width: 600px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}

.btn-block {
    width: 100%;
}

.mt-4 {
    margin-top: 1.5rem;
}

.error-message {
    margin-top: 1.5rem;
    padding: 1rem;
    background: rgba(239, 68, 68, 0.1);
    border: 1px solid rgba(239, 68, 68, 0.2);
    border-radius: var(--radius-sm);
    color: var(--error);
    text-align: center;
    font-size: 0.9rem;
}

.signup-footer {
    margin-top: 2rem;
    text-align: center;
    font-size: 0.9rem;
}

.link {
    color: var(--primary);
    text-decoration: none;
    font-weight: 600;
}

.link:hover {
    text-decoration: underline;
}

.link-muted {
    display: block;
    margin-top: 1rem;
    color: var(--text-muted);
    text-decoration: none;
    font-size: 0.85rem;
}

.link-muted:hover {
    color: var(--text-primary);
}
</style>