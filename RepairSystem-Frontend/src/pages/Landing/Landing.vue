<template>
<div class="landing-page">
    <!-- Navigation -->
    <nav class="navbar glass-card">
        <div class="container nav-container">
            <a class="navbar-brand" href="/">
                <div class="logo-icon-sm">MT</div>
                <span class="brand-text">MecanoTun</span>
            </a>
            
            <div class="nav-links">
                <a href="#about" class="nav-link">About</a>
                <a href="#services" class="nav-link">Services</a>
                <a href="#contact" class="nav-link">Contact</a>
            </div>

            <div class="nav-actions">
                <a class="btn btn-secondary btn-sm" href="/login">Log In</a>
                <a class="btn btn-primary btn-sm" href="/signup">Sign Up</a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <header class="hero-section">
        <div class="hero-bg-shapes">
            <div class="shape shape-hero-1"></div>
            <div class="shape shape-hero-2"></div>
        </div>
        
        <div class="container hero-content">
            <h1 class="hero-title">
                Premium Care for <br>
                <span class="text-gradient">Your Machine</span>
            </h1>
            <p class="hero-subtitle">
                Experience the gold standard in automotive repair. 
                Certified technicians, transparent pricing, and a passion for performance.
            </p>
            <div class="hero-cta">
                <a href="/login?redirect=booking" class="btn btn-primary btn-lg">Book Appointment</a>
                <a href="#services" class="btn btn-secondary btn-lg">Explore Services</a>
            </div>
        </div>
    </header>

    <!-- About Section -->
    <section id="about" class="section about-section">
        <div class="container">
            <div class="section-header">
                <h2>About Us</h2>
                <p>Serving the community with integrity for over 40 years.</p>
            </div>
            
            <div class="about-grid">
                <div class="about-card glass-card">
                    <div class="icon-lg">🏆</div>
                    <h3>Excellence</h3>
                    <p>We pride ourselves on delivering manufacturer-standard repairs with meticulous attention to detail.</p>
                </div>
                <div class="about-card glass-card">
                    <div class="icon-lg">🤝</div>
                    <h3>Trust</h3>
                    <p>Locally owned and operated. We build relationships based on honesty and reliable service.</p>
                </div>
                <div class="about-card glass-card">
                    <div class="icon-lg">⚡</div>
                    <h3>Speed</h3>
                    <p>Efficient diagnostics and repairs to get you back on the road safely and quickly.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Services Section -->
    <section id="services" class="section services-section">
        <div class="container">
            <div class="section-header">
                <h2>Our Services</h2>
                <p>Comprehensive care for every aspect of your vehicle.</p>
            </div>

            <div class="services-grid">
                <div class="service-item glass-card">
                    <div class="service-icon">🔧</div>
                    <h3>Repairs</h3>
                    <p>Engine, transmission, and mechanical repairs.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">✨</div>
                    <h3>Car Wash</h3>
                    <p>Premium detailing and exterior cleaning.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">🔍</div>
                    <h3>Inspection</h3>
                    <p>Thorough safety and performance checks.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">🛢️</div>
                    <h3>Oil Change</h3>
                    <p>High-quality synthetic and conventional oil.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">🆘</div>
                    <h3>Roadside</h3>
                    <p>24/7 assistance when you need it most.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">🛞</div>
                    <h3>Tires</h3>
                    <p>Installation, rotation, and alignment.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">🚛</div>
                    <h3>Towing</h3>
                    <p>Safe transport for your vehicle.</p>
                </div>
                <div class="service-item glass-card">
                    <div class="service-icon">❄️</div>
                    <h3>Winter Prep</h3>
                    <p>Get ready for the harsh Canadian winter.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Contact/Hours Section -->
    <section id="contact" class="section contact-section">
        <div class="container">
            <div class="contact-wrapper glass-card">
                <div class="contact-info">
                    <h2>Visit Us</h2>
                    <p class="address">
                        <span class="icon">📍</span> Ben Arous , Tunis, Tunisia
                    </p>
                    <p class="phone">
                        <span class="icon">📞</span> +216 70 100 200
                    </p>
                    <a href="/signup" class="btn btn-primary mt-4">Book Online</a>
                </div>
                
                <div class="hours-info">
                    <h3>Opening Hours</h3>
                    <ul class="hours-list">
                        <li><span>Mon - Fri</span> <span>9:00 AM - 9:00 PM</span></li>
                        <li><span>Saturday</span> <span>10:00 AM - 6:00 PM</span></li>
                        <li><span>Sunday</span> <span>Closed</span></li>
                    </ul>
                </div>
            </div>
        </div>
    </section>

    <footer class="footer">
        <div class="container">
            <p>&copy; 2023 MecanoTun. All rights reserved.</p>
        </div>
    </footer>
</div>
</template>

<script src="./Landing.js">
</script>

<style scoped>
.landing-page {
    background-color: var(--bg-primary);
    min-height: 100vh;
    color: var(--text-primary);
    overflow-x: hidden;
}

/* Navbar */
.navbar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 100;
    padding: 1rem 0;
    border-radius: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.nav-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.navbar-brand {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    text-decoration: none;
}

.logo-icon-sm {
    width: 32px;
    height: 32px;
    background: var(--primary);
    color: #000;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 800;
    font-size: 0.9rem;
}

.brand-text {
    font-weight: 700;
    color: var(--text-primary);
    font-size: 1.1rem;
}

.nav-links {
    display: flex;
    gap: 2rem;
}

.nav-link {
    color: var(--text-secondary);
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s;
}

.nav-link:hover {
    color: var(--primary);
}

.nav-actions {
    display: flex;
    gap: 1rem;
}

.btn-sm {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
}

/* Hero Section */
.hero-section {
    position: relative;
    padding: 8rem 0 6rem;
    text-align: center;
    overflow: hidden;
}

.hero-bg-shapes .shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.3;
    z-index: 0;
}

.shape-hero-1 {
    top: -20%;
    left: -10%;
    width: 800px;
    height: 800px;
    background: var(--primary);
}

.shape-hero-2 {
    bottom: -20%;
    right: -10%;
    width: 600px;
    height: 600px;
    background: var(--secondary);
}

.hero-content {
    position: relative;
    z-index: 1;
    max-width: 800px;
    margin: 0 auto;
}

.hero-title {
    font-size: 4rem;
    line-height: 1.1;
    margin-bottom: 1.5rem;
    font-weight: 800;
}

.text-gradient {
    background: linear-gradient(135deg, var(--primary) 0%, #fbbf24 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.hero-subtitle {
    font-size: 1.25rem;
    color: var(--text-secondary);
    margin-bottom: 2.5rem;
    line-height: 1.6;
}

.hero-cta {
    display: flex;
    gap: 1rem;
    justify-content: center;
}

.btn-lg {
    padding: 1rem 2rem;
    font-size: 1.1rem;
}

/* Sections */
.section {
    padding: 5rem 0;
}

.section-header {
    text-align: center;
    margin-bottom: 3rem;
}

.section-header h2 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
}

.section-header p {
    font-size: 1.1rem;
    color: var(--text-secondary);
}

/* About Grid */
.about-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 2rem;
}

.about-card {
    text-align: center;
    padding: 2.5rem;
}

.icon-lg {
    font-size: 3rem;
    margin-bottom: 1.5rem;
}

/* Services Grid */
.services-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
}

.service-item {
    padding: 2rem;
    text-align: center;
    transition: transform 0.2s;
}

.service-item:hover {
    transform: translateY(-5px);
    border-color: var(--primary);
}

.service-icon {
    font-size: 2.5rem;
    margin-bottom: 1rem;
}

/* Contact Section */
.contact-wrapper {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
    padding: 3rem;
}

.contact-info h2 {
    margin-bottom: 1.5rem;
}

.contact-info p {
    font-size: 1.2rem;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.hours-list {
    list-style: none;
    padding: 0;
}

.hours-list li {
    display: flex;
    justify-content: space-between;
    padding: 0.75rem 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.hours-list li:last-child {
    border-bottom: none;
}

/* Footer */
.footer {
    padding: 2rem 0;
    text-align: center;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    color: var(--text-secondary);
    font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 768px) {
    .nav-links { display: none; }
    .hero-title { font-size: 2.5rem; }
    .about-grid { grid-template-columns: 1fr; }
    .contact-wrapper { grid-template-columns: 1fr; }
}
</style>