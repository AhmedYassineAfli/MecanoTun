import axios from 'axios'

var config = require('../../config')
var backendUrl = config.dev.backendHost
var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port

var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export const vehicleApi = {
    /**
     * Get all vehicle makes from NHTSA database
     */
    getAllMakes() {
        return AXIOS.get('/api/vehicles/makes')
    },

    /**
     * Get all models for a specific make
     */
    getModelsForMake(makeId) {
        return AXIOS.get(`/api/vehicles/makes/${makeId}/models`)
    },

    /**
     * Get available years for a specific make and model
     */
    getYearsForModel(makeName, modelName) {
        return AXIOS.get('/api/vehicles/years', {
            params: { make: makeName, model: modelName }
        })
    },

    /**
     * Get engine options for a specific make, model, and year
     */
    getEnginesForModel(makeName, modelName, year) {
        return AXIOS.get('/api/vehicles/engines', {
            params: { make: makeName, model: modelName, year: year }
        })
    },

    /**
     * Decode VIN to get vehicle information
     */
    decodeVin(vin) {
        return AXIOS.get(`/api/vehicles/decode-vin/${vin}`)
    },

    /**
     * Search makes by name (for autocomplete)
     */
    searchMakes(query) {
        return AXIOS.get('/api/vehicles/search-makes', { params: { query } })
    }
}
