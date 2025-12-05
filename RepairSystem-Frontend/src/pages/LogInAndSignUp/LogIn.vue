<template>
    <div class="login-page">
        <div class="background-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
        </div>

        <div class="login-container glass-card animate-fade-in">
            <div class="login-header">
                <div class="logo-icon">MT</div>
                <h1>Welcome Back</h1>
                <p>Sign in to your MecanoTun account</p>
            </div>

            <form @submit.prevent="logIn(customerEmail, customerPassword)">
                <div class="input-group">
                    <label for="email" class="label">Email Address</label>
                    <input 
                        id="email"
                        type="email" 
                        v-model="customerEmail" 
                        class="input" 
                        placeholder="you@example.com"
                        required
                    >
                </div>

                <div class="input-group">
                    <label for="password" class="label">Password</label>
                    <input 
                        id="password"
                        type="password" 
                        v-model="customerPassword" 
                        class="input" 
                        placeholder="••••••••"
                        required
                    >
                </div>

                <button type="submit" class="btn btn-primary btn-block">
                    Log In
                </button>
            </form>

            <div v-if="error" class="error-message">
                {{ error }}
            </div>

            <div class="login-footer">
                <p>Don't have an account? <router-link to="/signup" class="link">Sign Up</router-link></p>
                <router-link to="/" class="link-muted">Back to Home</router-link>
            </div>
        </div>
    </div>
</template>

<script src="./LogIn.js">
</script>

<style scoped>
.login-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
    background-color: var(--bg-primary);
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
    left: -10%;
    width: 500px;
    height: 500px;
    background: var(--primary);
}

.shape-2 {
    bottom: -10%;
    right: -10%;
    width: 600px;
    height: 600px;
    background: var(--secondary);
}

.login-container {
    width: 100%;
    max-width: 450px;
    position: relative;
    z-index: 1;
    padding: 3rem;
}

.login-header {
    text-align: center;
    margin-bottom: 2.5rem;
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

.login-header h1 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
}

.btn-block {
    width: 100%;
    margin-top: 1rem;
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

.login-footer {
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