<template>
<div class="vehicle-selector">
    <!-- Full Screen Loading State -->
    <div v-if="loading" class="full-loading-state">
        <div class="gear-spinner">
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.38a2 2 0 0 0-.73-2.73l-.15-.1a2 2 0 0 1-1-1.72v-.51a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>
        </div>
    </div>

    <!-- Main Content (Hidden while loading) -->
    <div v-else class="vehicle-selector-content">
        <!-- Input Mode Tabs -->
        <div class="input-mode-tabs">
        <div 
            class="mode-tab"
            :class="{ active: inputMode === 'model' }"
            @click="inputMode = 'model'"
        >
            <div class="mode-icon">🚗</div>
            <span>MODÈLE</span>
        </div>
        <div 
            class="mode-tab"
            :class="{ active: inputMode === 'vin' }"
            @click="inputMode = 'vin'"
        >
            <div class="mode-icon">🔢</div>
            <span>VIN</span>
        </div>
    </div>

    <!-- Model Selection Mode -->
    <div v-if="inputMode === 'model'" class="selection-mode">
        <div class="form-group">
            <label class="field-label">
                <span class="label-icon">
                    <!-- Car Front Icon for Marque -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/></svg>
                </span>
                Marque
            </label>
            <select v-model="selectedMakeId" @change="onMakeChange" class="custom-select brand-select" :disabled="loading">
                <option :value="null" class="placeholder-option">Sélectionnez une marque</option>
                <option v-for="make in makes" :key="make.makeId" :value="make.makeId">
                    {{ make.makeName }}
                </option>
            </select>
        </div>

        <div class="form-group" :class="{ 'blurred-disabled': !selectedMakeId && !loadingModels }">
            <label class="field-label">
                <span class="label-icon">
                    <!-- Car Side Icon for Modèle -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 16H9m10 0h3v-3.15a1 1 0 0 0-.84-.99L16 11l-2.7-3.6a1 1 0 0 0-.8-.4H5.24a2 2 0 0 0-1.8 1.1l-.8 1.63A6 6 0 0 0 2 12.42V16h2"/><circle cx="6.5" cy="16.5" r="2.5"/><circle cx="16.5" cy="16.5" r="2.5"/></svg>
                </span>
                Modèle
            </label>
            
            <div v-if="loadingModels" class="loading-input-placeholder">
                <div class="spinner-small"></div>
            </div>
            <select v-else v-model="selectedModelYearKey" @change="onModelChange" class="custom-select" :disabled="!selectedMakeId">
                <option :value="null" class="placeholder-option">Sélectionnez un modèle</option>
                <option v-for="model in modelsWithYears" :key="model.key" :value="model.key">
                    {{ model.display }}
                </option>
            </select>
        </div>

        <div class="form-group" :class="{ 'blurred-disabled': !selectedModelYearKey }">
            <label class="field-label">
                <span class="label-icon">
                    <!-- Engine Icon for Motorisation -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M7 10.5V6h3.5"/><path d="M17 10.5V6h-3.5"/><path d="M7 13.5v3.5h3.5"/><path d="M17 13.5v3.5h-3.5"/><path d="M4 10.5h3v3H4z"/><path d="M17 10.5h3v3h-3z"/><path d="M10.5 4h3v3h-3z"/><path d="M10.5 17h3v3h-3z"/><path d="M5.5 7.5h13v9h-13z"/></svg>
                </span>
                Motorisation
            </label>
            <div v-if="loadingEngines" class="loading-input-placeholder">
                <div class="spinner-small"></div>
            </div>
            <select v-else v-model="selectedEngine" @change="onEngineChange" class="custom-select" :disabled="!selectedModelYearKey">
                <option :value="null" class="placeholder-option">Sélectionnez une motorisation</option>
                <option v-for="engine in engines" :key="engine.name" :value="engine.name">
                    {{ engine.name }}
                </option>
            </select>
        </div>
    </div>

    <!-- VIN Decoder Mode -->
    <div v-else-if="inputMode === 'vin'" class="selection-mode">
        <div class="form-group">
            <label class="field-label">
                <span class="label-icon">🔢</span>
                Numéro VIN (17 caractères)
            </label>
            <input 
                type="text" 
                v-model="vinInput" 
                @input="onVinInput"
                @blur="decodeVinIfValid"
                maxlength="17"
                class="custom-input vin-input" 
                placeholder="5YJSA1E14HF000000"
            >
            <div class="vin-counter">{{ vinInput.length }}/17</div>
        </div>

        <div v-if="vinDecoding" class="loading-state">
            <div class="spinner"></div>
            <p>Décodage du VIN...</p>
        </div>

        <div v-if="vinDecoded" class="vin-result">
            <div class="success-checkmark">✓</div>
            <div class="vehicle-info-grid">
                <div class="info-item">
                    <span class="info-label">Marque</span>
                    <span class="info-value">{{ decodedVehicle.make }}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Modèle</span>
                    <span class="info-value">{{ decodedVehicle.model }}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Année</span>
                    <span class="info-value">{{ decodedVehicle.year }}</span>
                </div>
            </div>

            <!-- Engine selection after VIN decode -->
            <div class="form-group" v-if="decodedVehicle.engineOptions && decodedVehicle.engineOptions.length > 0">
                <label class="field-label">
                    <span class="label-icon">⚙️</span>
                    Motorisation
                </label>
                <select v-model="selectedEngine" @change="onEngineChange" class="custom-select">
                    <option :value="null">Sélectionnez</option>
                    <option v-for="(engine, index) in decodedVehicle.engineOptions" :key="index" :value="engine">
                        {{ engine }}
                    </option>
                </select>
            </div>
            <div class="form-group" v-else>
                <label class="field-label">
                    <span class="label-icon">⚙️</span>
                    Motorisation
                </label>
                <input 
                    type="text" 
                    v-model="selectedEngine" 
                    @input="onEngineChange"
                    class="custom-input" 
                    placeholder="e.g. 2.0L 4-Cylinder"
                >
            </div>
        </div>

        <div v-if="vinError" class="error-message">
            {{ vinError }}
        </div>
    </div>

    </div>
</div>
</template>

<script>
import { vehicleApi } from '../api/vehicles'

export default {
    name: 'VehicleSelector',
    props: {
        initialVehicle: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            inputMode: 'model', // 'model' or 'vin'
            loading: false,
            loadingModels: false,
            vinDecoding: false,
            vinDecoded: false,
            vinError: '',
            
            // Make/Model selection
            makes: [],
            models: [],  // Raw models from API
            engines: [],
            selectedMakeId: null,
            selectedMakeName: '',
            selectedModelYearKey: null,  // Format: "ModelName|Year"
            selectedModelName: null,
            selectedYear: null,
            selectedEngine: '',
            loadingEngines: false,
            
            // VIN mode
            vinInput: '',
            decodedVehicle: {}
        }
    },
    computed: {
        // Transform models into model+year combinations
        modelsWithYears() {
            const result = []
            this.models.forEach((model, index) => {
                // Display format: "ModelName (2015-2025)" or just "ModelName" if no year range
                let display = model.modelName
                if (model.yearRange && model.yearRange.length > 0) {
                    display = `${model.modelName} (${model.yearRange})`
                }
                
                result.push({
                    key: `${model.modelName}|${index}`,
                    display: display,
                    modelName: model.modelName,
                    years: model.years || [],  // Array of available years
                    yearRange: model.yearRange || ''
                })
            })
            
            return result
        }
    },
    mounted() {
        this.loadMakes()
        this.generateYears()
        
        if (this.initialVehicle) {
            this.selectedMakeName = this.initialVehicle.brand
            this.selectedModelName = this.initialVehicle.model
            this.selectedEngine = this.initialVehicle.engine
            if (this.initialVehicle.vin) {
                this.inputMode = 'vin'
                this.vinInput = this.initialVehicle.vin
            }
        }
    },
    methods: {
        async loadMakes() {
            this.loading = true
            try {
                const response = await vehicleApi.getAllMakes()
                this.makes = response.data
            } catch (error) {
                console.error('Error loading makes:', error)
            } finally {
                this.loading = false
            }
        },
        generateYears() {
            const currentYear = new Date().getFullYear()
            const years = []
            for (let year = currentYear; year >= 1990; year--) {
                years.push(year)
            }
            this.years = years
        },
        async loadYears() {
            if (!this.selectedMakeName || !this.selectedModelName) {
                return
            }
            
            try {
                const response = await vehicleApi.getYearsForModel(
                    this.selectedMakeName,
                    this.selectedModelName
                )
                this.years = response.data
            } catch (error) {
                console.error('Error loading years:', error)
                // Fallback to generating years locally
                this.generateYears()
            }
        },
        async loadEngines() {
            if (!this.selectedMakeName || !this.selectedModelName || !this.selectedYear) {
                return
            }
            
            this.loadingEngines = true
            try {
                const response = await vehicleApi.getEnginesForModel(
                    this.selectedMakeName,
                    this.selectedModelName,
                    this.selectedYear
                )
                this.engines = response.data
            } catch (error) {
                console.error('Error loading engines:', error)
                // Fallback to empty if API fails
                this.engines = []
            } finally {
                this.loadingEngines = false
            }
        },
        async onMakeChange() {
            if (!this.selectedMakeId) {
                this.models = []
                this.selectedModelName = null
                return
            }
            
            const selectedMake = this.makes.find(m => m.makeId === this.selectedMakeId)
            if (selectedMake) {
                this.selectedMakeName = selectedMake.makeName
            }
            
            this.loadingModels = true
            try {
                const response = await vehicleApi.getModelsForMake(this.selectedMakeId)
                this.models = response.data
            } catch (error) {
                console.error('Error loading models:', error)
            } finally {
                this.loadingModels = false
            }
        },
        onModelChange() {
            if (!this.selectedModelYearKey) {
                this.selectedModelName = null
                this.selectedYear = null
                this.selectedEngine = ''
                this.engines = []
                return
            }
            
            // Parse the key to get model name
            const parts = this.selectedModelYearKey.split('|')
            this.selectedModelName = parts[0]
            
            // For now, we'll use a default year or fetch from API
            // You can enhance this to show year ranges
            this.selectedYear = new Date().getFullYear()
            
            // Load engines for this model
            this.loadEngines()
            this.emitVehicleData()
        },
        onYearChange() {
            // This method is no longer needed since year is part of model selection
            // Keeping it for compatibility
            this.selectedEngine = ''
            this.loadEngines()
            this.emitVehicleData()
        },
        onEngineChange() {
            this.emitVehicleData()
        },
        onVinInput() {
            this.vinInput = this.vinInput.toUpperCase()
            this.vinDecoded = false
            this.vinError = ''
        },
        async decodeVinIfValid() {
            if (this.vinInput.length !== 17) {
                if (this.vinInput.length > 0) {
                    this.vinError = 'Le VIN doit contenir exactement 17 caractères'
                }
                return
            }
            
            this.vinDecoding = true
            this.vinError = ''
            
            try {
                const response = await vehicleApi.decodeVin(this.vinInput)
                this.decodedVehicle = response.data
                this.vinDecoded = true
                
                // Set values for emission
                this.selectedMakeName = this.decodedVehicle.make
                this.selectedModelName = this.decodedVehicle.model
                
                // Auto-select first engine option if available
                if (this.decodedVehicle.engineOptions && this.decodedVehicle.engineOptions.length === 1) {
                    this.selectedEngine = this.decodedVehicle.engineOptions[0]
                    this.emitVehicleData()
                }
            } catch (error) {
                this.vinError = (error.response && error.response.data && error.response.data.message) || 'Erreur lors du décodage du VIN'
                console.error('Error decoding VIN:', error)
            } finally {
                this.vinDecoding = false
            }
        },
        emitVehicleData() {
            const vehicleData = {
                brand: this.selectedMakeName,
                makeId: this.selectedMakeId,
                model: this.selectedModelName,
                engine: this.selectedEngine,
                vin: this.inputMode === 'vin' ? this.vinInput : null,
                year: this.decodedVehicle.year || null
            }
            
            // Only emit if we have at least brand and model
            if (vehicleData.brand && vehicleData.model) {
                this.$emit('vehicle-selected', vehicleData)
            }
        }
    }
}
</script>

<style scoped>
.vehicle-selector {
    width: 100%;
}

/* Input Mode Tabs */
.input-mode-tabs {
    display: flex;
    gap: 0.5rem;
    margin-bottom: 1.5rem;
}

.mode-tab {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.mode-tab:hover {
    background: rgba(255, 255, 255, 0.08);
    border-color: rgba(255, 102, 0, 0.3);
}

.mode-tab.active {
    background: linear-gradient(135deg, #FF6600 0%, #FF8533 100%);
    border-color: #FF6600;
    box-shadow: 0 4px 15px rgba(255, 102, 0, 0.3);
}

.mode-icon {
    font-size: 2rem;
}

.mode-tab.active .mode-icon,
.mode-tab.active span {
    color: white;
}

.mode-tab span {
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--text-secondary);
}

/* Form Groups */
.selection-mode {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
}

.form-group {
    position: relative;
}

.field-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.5rem;
    color: var(--text-primary);
    font-weight: 500;
    font-size: 0.95rem;
}

.label-icon {
    font-size: 1.2rem;
}

.custom-select,
.custom-input {
    width: 100%;
    padding: 1.2rem 1.5rem;
    min-height: 60px;
    background: rgba(255, 255, 255, 0.05);
    border: 2px solid rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    color: #ffffff;
    font-size: 1.1rem;
    transition: all 0.2s ease;
}

.custom-select:hover,
.custom-input:hover {
    border-color: rgba(255, 102, 0, 0.3);
    background: rgba(255, 255, 255, 0.08);
}

.custom-select:focus,
.custom-input:focus {
    outline: none;
    border-color: #FF6600;
    background: rgba(255, 255, 255, 0.08);
    box-shadow: 0 0 0 3px rgba(255, 102, 0, 0.1);
}

.custom-select:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* VIN Input */
.vin-input {
    text-transform: uppercase;
    letter-spacing: 0.05em;
    font-family: 'Courier New', monospace;
    padding-right: 3.5rem;
}

.vin-counter {
    position: absolute;
    right: 1rem;
    top: 2.7rem;
    font-size: 0.85rem;
    color: var(--text-secondary);
    font-weight: 600;
}

/* VIN Result */
.vin-result {
    padding: 1.5rem;
    background: rgba(40, 167, 69, 0.1);
    border: 2px solid rgba(40, 167, 69, 0.3);
    border-radius: 12px;
    margin-top: 1rem;
}

.success-checkmark {
    text-align: center;
    font-size: 3rem;
    color: #28a745;
    margin-bottom: 1rem;
}

.vehicle-info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 1rem;
    margin-bottom: 1.5rem;
}

.info-item {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.info-label {
    font-size: 0.75rem;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.info-value {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary);
}

/* Loading State */
.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    gap: 1rem;
}

.spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba(255, 102, 0, 0.1);
    border-top-color: #FF6600;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.loading-state p {
    color: var(--text-secondary);
    margin: 0;
}

/* Error Message */
.error-message {
    padding: 1rem;
    background: rgba(220, 53, 69, 0.1);
    border: 2px solid rgba(220, 53, 69, 0.3);
    border-radius: 8px;
    color: #dc3545;
    font-size: 0.9rem;
    margin-top: 1rem;
}

/* Blurred Disabled State */
.blurred-disabled {
    opacity: 0.5;
    filter: blur(1px);
    pointer-events: none;
    transition: all 0.3s ease;
}

.loading-input-placeholder {
    height: 60px;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.spinner-small {
    width: 24px;
    height: 24px;
    border: 2px solid rgba(255, 102, 0, 0.1);
    border-top-color: #FF6600;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

.brand-select {
    font-weight: 600;
    color: #FF6600;
    background: rgba(255, 102, 0, 0.05);
    border-color: rgba(255, 102, 0, 0.2);
}

.brand-select:hover {
    background: rgba(255, 102, 0, 0.1);
}

/* Placeholder Styling */
.placeholder-option {
    color: #888; /* Lighter color for placeholder options */
}

.custom-input::placeholder {
    color: #888; /* Lighter color for input placeholders */
    opacity: 1; /* Firefox */
}

.custom-select {
    color: #fff; /* Default text color */
}

.custom-select option {
    background-color: #1a1a1a; /* Dark background for options */
    color: #fff;
}

.custom-select option.placeholder-option {
    color: #888; /* Lighter color for the placeholder option in the dropdown */
}

/* Full Screen Loading State */
.full-loading-state {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #1a1a1a; /* Match modal background */
    z-index: 10;
    border-radius: 12px;
}

.gear-spinner {
    color: #FF6600;
    animation: spin 2s linear infinite;
}

.vehicle-selector-content {
    animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

</style>
