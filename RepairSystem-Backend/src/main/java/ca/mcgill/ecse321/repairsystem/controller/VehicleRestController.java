package ca.mcgill.ecse321.repairsystem.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.repairsystem.service.VehicleApiService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    @Autowired
    private VehicleApiService vehicleApiService;

    /**
     * Get all vehicle makes
     * GET /api/vehicles/makes
     */
    @GetMapping(value = { "/makes", "/makes/" })
    public List<Map<String, Object>> getAllMakes() {
        return vehicleApiService.getAllMakes();
    }

    /**
     * Get models for a specific make
     * GET /api/vehicles/makes/{makeId}/models
     */
    @GetMapping(value = { "/makes/{makeId}/models", "/makes/{makeId}/models/" })
    public List<Map<String, Object>> getModelsForMake(@PathVariable("makeId") int makeId) {
        return vehicleApiService.getModelsForMake(makeId);
    }

    /**
     * Decode VIN to get vehicle information
     * GET /api/vehicles/decode-vin/{vin}
     */
    @GetMapping(value = { "/decode-vin/{vin}", "/decode-vin/{vin}/" })
    public Map<String, Object> decodeVin(@PathVariable("vin") String vin) {
        return vehicleApiService.decodeVin(vin);
    }

    /**
     * Get available years for a specific make and model
     * GET /api/vehicles/years?make=Toyota&model=Camry
     */
    @GetMapping(value = { "/years", "/years/" })
    public List<Integer> getYearsForModel(
            @RequestParam("make") String makeName,
            @RequestParam("model") String modelName) {
        return vehicleApiService.getYearsForModel(makeName, modelName);
    }

    /**
     * Get engine options for a specific make, model, and year
     * GET /api/vehicles/engines?make=Toyota&model=Camry&year=2020
     */
    @GetMapping(value = { "/engines", "/engines/" })
    public List<Map<String, Object>> getEnginesForModel(
            @RequestParam("make") String makeName,
            @RequestParam("model") String modelName,
            @RequestParam("year") int year) {
        return vehicleApiService.getEnginesForModel(makeName, modelName, year);
    }

    /**
     * Search vehicle makes by name
     * GET /api/vehicles/search-makes?query=toyota
     */
    @GetMapping(value = { "/search-makes", "/search-makes/" })
    public List<Map<String, Object>> searchMakes(@RequestParam("query") String query) {
        return vehicleApiService.searchMakes(query);
    }
}
