package ca.mcgill.ecse321.repairsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Service for integrating with NHTSA vPIC API to get vehicle information
 * API Documentation: https://vpic.nhtsa.dot.gov/api/
 */
@Service
public class VehicleApiService {

    private static final String NHTSA_BASE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public VehicleApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get comprehensive list of vehicle makes
     * Using user-specified list for better coverage
     */
    public List<Map<String, Object>> getAllMakes() throws IllegalArgumentException {
        List<Map<String, Object>> makes = new ArrayList<>();

        // Comprehensive list of car manufacturers as specified by user
        String[] makeNames = {
                "ABARTH", "AC", "ACURA", "ADDAX", "AIWAYS", "AIXAM", "ALFA ROMEO", "ALPINA", "ALPINE", "AMC", "ARO",
                "ARTEGA", "ASHOK LEYLAND", "ASIA MOTORS", "ASKAM", "ASTON MARTIN", "AUDI", "AUSTIN", "AUSTIN-HEALEY",
                "AUTO UNION", "AUTOBIANCHI", "AUVERLAND", "AVIA", "B-ON", "BAC", "BAIC", "BARKAS", "BAW", "BEDFORD",
                "BEIJING", "BENTLEY", "BERTONE", "BESTUNE", "BITTER", "BIZZARRINI", "BLUECAR", "BMC", "BMW", "BOND",
                "BORGWARD", "BRABUS", "BREMACH", "BRISTOL", "BUGATTI", "BUICK", "BYD", "CADILLAC", "CALLAWAY",
                "CARBODIES", "CASALINI", "CATERHAM", "CENNTRO", "CHATENET", "CHECKER", "CHERY", "CHEVROLET", "CHRYSLER",
                "CITROEN", "CMC", "COMARTH", "CUPRA", "DACIA", "DAEWOO", "DAF", "DAIHATSU", "DAIMLER", "DALLAS",
                "DE LA CHAPELLE", "DE LOREAN", "DE TOMASO", "DFSK", "DODGE", "DONGFENG", "DONGFENG (DFAC)",
                "DONKERVOORT", "DR", "DS", "DSK", "E.GO", "EBRO", "ELARIS", "EMC", "EON MOTORS", "EVO", "FAAM",
                "FARIZON AUTO", "FEIDI", "FENGON", "FERRARI", "FEST", "FIAT", "FIREFLY", "FISKER", "FORD",
                "FORD AUSTRALIA", "FORD OTOSAN", "FORD USA", "FORTHING", "FOTON", "FREIGHTLINER", "FSO", "GALLOPER",
                "GAZ", "GEELY", "GENESIS", "GEO", "GEOMETRY", "GINETTA", "GIOTTI VICTORIA", "GLAS", "GMC", "GME",
                "GONOW", "GONOW (GAC)", "GOUPIL", "GREAT WALL", "GUMPERT", "HAVAL", "HIGER", "HINDUSTAN", "HOBBYCAR",
                "HONDA", "HONGQI", "HUMMER", "HYUNDAI", "ICH-X", "ICKX", "INDIGO", "INEOS", "INFINITI", "INNOCENTI",
                "IRMSCHER", "ISDERA", "ISO", "ISORIVOLTA", "ISUZU", "IVECO", "IZH", "JAC", "JAECOO", "JAGUAR", "JEEP",
                "JENSEN", "JIANGLING LANDWIND", "JIEFANG (FAW)", "KARMA", "KARSAN", "KG MOBILITY", "KIA", "KING LONG",
                "KTM", "LADA", "LAMBORGHINI", "LANCIA", "LAND ROVER", "LDV", "LEAPMOTOR", "LEVC", "LEXUS", "LIFAN",
                "LIGIER", "LINCOLN", "LIVAN AUTO", "LOTUS", "LTI", "LUCID", "LYNK & CO", "M-HERO", "MAHINDRA", "MAN",
                "MARCOS", "MARUTI", "MASERATI", "MAXUS", "MAYBACH", "MAZDA", "MCLAREN", "MEGA", "MERCEDES-BENZ",
                "METROCAB", "MG", "MIA ELECTRIC", "MICRO", "MICROCAR", "MIDDLEBRIDGE", "MINELLI", "MINI", "MINI (GB)",
                "MITSUBISHI", "MITSUOKA", "MOBILIZE", "MORGAN", "MORRIS", "MOSKVICH", "MPM MOTORS", "NAVOR", "NEXTEM",
                "NIO", "NISSAN", "NSU", "OHM", "OLDSMOBILE", "OLTCIT", "OMODA", "OPEL", "ORA", "OSCA", "PAGANI",
                "PANOZ", "PANTHER", "PAYKAN", "PEUGEOT", "PGO", "PIAGGIO", "PININFARINA", "PLYMOUTH", "POLARIS",
                "POLESTAR", "PONTIAC", "PORSCHE", "PREMIER", "PROTON", "PUCH", "RAM", "RANGER", "RAYTON FISSORE",
                "RELIANT", "RENAULT", "RENAULT TRUCKS", "RILEY", "RIMAC", "RIVIAN", "ROLLS-ROYCE", "ROVER", "RUF",
                "SAAB", "SANTANA", "SATURN", "SCOOBIC", "SEAT", "SERES", "SEVIC", "SHELBY", "SHINERAY", "SILENCE",
                "SINOTRUK (CNHTC)", "SIPANI", "SKODA", "SKYWELL", "SKYWORTH", "SMART", "SPECTRE", "SPORTEQUIPE",
                "SPYKER", "SSANGYONG", "STANDARD AUTOMOBILE", "STANGUELLINI", "STEYR", "STREETSCOOTER", "SUBARU",
                "SUZUKI", "SWM MOTORS", "TALBOT", "TATA (TELCO)", "TAZZARI", "TESLA", "THINK", "TIGER", "TOGG",
                "TOYOTA", "TRABANT", "TRIUMPH", "TROPOS", "TVR", "TVS", "UAZ", "UMM", "VAUXHALL", "VECTOR", "VENTURI",
                "VICTORY", "VINFAST", "VOLKSWAGEN", "VOLVO", "VOYAH", "VUHL", "WARTBURG", "WESTFIELD", "WEY",
                "WIESMANN", "WOLSELEY", "XBUS", "XEV", "XPENG", "YUDO", "YUGO", "YULON", "ZASTAVA", "ZAZ", "ZD",
                "ZEEKR", "ZENOS CARS", "ZUENDAPP"
        };

        int id = 1;
        for (String makeName : makeNames) {
            Map<String, Object> makeMap = new HashMap<>();
            makeMap.put("makeId", id++);
            makeMap.put("makeName", makeName);
            makes.add(makeMap);
        }

        return makes;
    }

    /**
     * Map our make names to NHTSA-compatible names
     * NHTSA uses specific capitalization and naming conventions
     */
    private String getNHTSAMakeName(String makeName) {
        // Convert common variations to NHTSA format
        Map<String, String> makeMapping = new HashMap<>();

        // Common mappings - NHTSA uses proper case, not all caps
        makeMapping.put("MERCEDES-BENZ", "Mercedes-Benz");
        makeMapping.put("ALFA ROMEO", "Alfa Romeo");
        makeMapping.put("ASTON MARTIN", "Aston Martin");
        makeMapping.put("LAND ROVER", "Land Rover");
        makeMapping.put("ROLLS-ROYCE", "Rolls-Royce");

        // If we have a specific mapping, use it
        if (makeMapping.containsKey(makeName)) {
            return makeMapping.get(makeName);
        }

        // Otherwise, convert to title case (first letter uppercase, rest lowercase)
        String[] words = makeName.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * Get all models for a specific make by searching NHTSA API with make name
     */
    public List<Map<String, Object>> getModelsForMake(int makeId) throws IllegalArgumentException {
        // Get the make name from our list
        List<Map<String, Object>> allMakes = getAllMakes();
        String makeName = null;
        for (Map<String, Object> make : allMakes) {
            if ((int) make.get("makeId") == makeId) {
                makeName = (String) make.get("makeName");
                break;
            }
        }

        if (makeName == null) {
            return new ArrayList<>();
        }

        try {
            // Convert to NHTSA-compatible name
            String nhtsaMakeName = getNHTSAMakeName(makeName);

            // URL encode the make name to handle spaces and special characters
            String encodedMakeName = java.net.URLEncoder.encode(nhtsaMakeName, "UTF-8");

            // Search NHTSA API by make name
            String url = NHTSA_BASE_URL + "/GetModelsForMake/" + encodedMakeName + "?format=json";
            System.out.println("Fetching models for '" + makeName + "' (NHTSA: '" + nhtsaMakeName + "') from: " + url);

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("Results");

            // Collect unique model names
            Set<String> uniqueModels = new HashSet<>();
            for (JsonNode node : results) {
                uniqueModels.add(node.get("Model_Name").asText());
            }

            // Build the response without year ranges for instant loading
            List<Map<String, Object>> models = new ArrayList<>();
            int modelId = 1;
            for (String modelName : uniqueModels) {
                Map<String, Object> model = new HashMap<>();
                model.put("makeId", makeId);
                model.put("makeName", makeName);
                model.put("modelId", modelId++);
                model.put("modelName", modelName);
                model.put("yearRange", ""); // No year range for speed
                model.put("years", new ArrayList<>());
                models.add(model);
            }

            System.out.println("Found " + models.size() + " models for " + makeName);
            return models;
        } catch (Exception e) {
            System.err.println("Error fetching models for '" + makeName + "': " + e.getMessage());
            e.printStackTrace();
            // If NHTSA doesn't have this make, return empty list
            return new ArrayList<>();
        }
    }

    /**
     * Get available years for a specific make and model
     */
    public List<Integer> getYearsForModel(String makeName, String modelName) {
        Set<Integer> years = new HashSet<>();

        try {
            // Query NHTSA API for all years this model was made
            // We'll check a range of years and see which ones have this model
            int currentYear = java.time.Year.now().getValue();

            for (int year = currentYear; year >= 1990; year--) {
                String url = NHTSA_BASE_URL + "/GetModelsForMakeYear/make/" + makeName +
                        "/modelyear/" + year + "?format=json";
                String response = restTemplate.getForObject(url, String.class);

                JsonNode root = objectMapper.readTree(response);
                JsonNode results = root.get("Results");

                // Check if this model exists in this year
                for (JsonNode node : results) {
                    String model = node.get("Model_Name").asText();
                    if (model.equalsIgnoreCase(modelName)) {
                        years.add(year);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // If API fails, return a default range
            int currentYear = java.time.Year.now().getValue();
            for (int year = currentYear; year >= currentYear - 35; year--) {
                years.add(year);
            }
        }

        // Convert to sorted list (descending)
        List<Integer> yearList = new ArrayList<>(years);
        yearList.sort(Collections.reverseOrder());
        return yearList;
    }

    /**
     * Get engine options for a specific make, model, and year
     */
    public List<Map<String, Object>> getEnginesForModel(String makeName, String modelName, int year) {
        List<Map<String, Object>> engines = new ArrayList<>();

        try {
            // Use NHTSA API to get vehicle details by make/model/year
            String url = NHTSA_BASE_URL + "/GetModelsForMakeYear/make/" + makeName +
                    "/modelyear/" + year + "?format=json";
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("Results");

            // Find models matching our model name and collect unique engines
            Set<String> uniqueEngines = new HashSet<>();
            for (JsonNode node : results) {
                String model = node.get("Model_Name").asText();
                if (model.equalsIgnoreCase(modelName)) {
                    // Try to get engine info from various fields
                    String engineInfo = "";

                    // Check for displacement and cylinders
                    JsonNode dispL = node.get("DisplacementL");
                    JsonNode cylinders = node.get("EngineCylinders");

                    if (dispL != null && !dispL.isNull() && cylinders != null && !cylinders.isNull()) {
                        engineInfo = dispL.asText() + "L " + cylinders.asText() + "-Cylinder";
                        uniqueEngines.add(engineInfo);
                    }
                }
            }

            // If no engines found from API, provide common options
            if (uniqueEngines.isEmpty()) {
                uniqueEngines.add("1.5L 4-Cylinder");
                uniqueEngines.add("2.0L 4-Cylinder");
                uniqueEngines.add("2.5L 4-Cylinder");
                uniqueEngines.add("3.0L V6");
                uniqueEngines.add("3.5L V6");
                uniqueEngines.add("5.0L V8");
                uniqueEngines.add("Electric");
                uniqueEngines.add("Hybrid");
                uniqueEngines.add("Other");
            }

            // Convert to list of maps
            for (String engine : uniqueEngines) {
                Map<String, Object> engineMap = new HashMap<>();
                engineMap.put("name", engine);
                engines.add(engineMap);
            }

        } catch (Exception e) {
            // Fallback to common engines if API fails
            String[] fallbackEngines = {
                    "1.5L 4-Cylinder", "2.0L 4-Cylinder", "2.5L 4-Cylinder",
                    "3.0L V6", "3.5L V6", "5.0L V8", "Electric", "Hybrid", "Other"
            };
            for (String engine : fallbackEngines) {
                Map<String, Object> engineMap = new HashMap<>();
                engineMap.put("name", engine);
                engines.add(engineMap);
            }
        }

        return engines;
    }

    /**
     * Decode VIN to get vehicle information including available engine options
     */
    public Map<String, Object> decodeVin(String vin) throws IllegalArgumentException {
        if (vin == null || vin.length() != 17) {
            throw new IllegalArgumentException("VIN must be exactly 17 characters");
        }

        try {
            String url = NHTSA_BASE_URL + "/DecodeVinValues/" + vin + "?format=json";
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode result = root.get("Results").get(0);

            // Check if VIN is valid
            if (result.get("ErrorCode").asText().contains("6")) {
                throw new IllegalArgumentException("Invalid VIN: " + result.get("ErrorText").asText());
            }

            Map<String, Object> vehicleInfo = new HashMap<>();
            vehicleInfo.put("vin", vin);
            vehicleInfo.put("make", getTextValue(result, "Make"));
            vehicleInfo.put("model", getTextValue(result, "Model"));
            vehicleInfo.put("year", getTextValue(result, "ModelYear"));
            vehicleInfo.put("vehicleType", getTextValue(result, "VehicleType"));

            // Engine information
            List<String> engineOptions = new ArrayList<>();
            String displacementL = getTextValue(result, "DisplacementL");
            String cylinders = getTextValue(result, "EngineCylinders");
            String engineModel = getTextValue(result, "EngineModel");
            String fuelType = getTextValue(result, "FuelTypePrimary");

            // Build engine description
            if (!displacementL.isEmpty() && !cylinders.isEmpty()) {
                engineOptions.add(displacementL + "L " + cylinders + "-Cylinder" +
                        (!fuelType.isEmpty() ? " " + fuelType : ""));
            } else if (!engineModel.isEmpty()) {
                engineOptions.add(engineModel);
            }

            // Additional engine configurations if available
            String engineHP = getTextValue(result, "EngineHP");
            if (!engineHP.isEmpty() && !engineOptions.isEmpty()) {
                vehicleInfo.put("horsePower", engineHP + " HP");
            }

            vehicleInfo.put("engineOptions", engineOptions);
            vehicleInfo.put("transmission", getTextValue(result, "TransmissionStyle"));
            vehicleInfo.put("bodyClass", getTextValue(result, "BodyClass"));
            vehicleInfo.put("doors", getTextValue(result, "Doors"));

            return vehicleInfo;
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Failed to decode VIN: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing VIN data: " + e.getMessage());
        }
    }

    /**
     * Search makes by name (for autocomplete)
     */
    public List<Map<String, Object>> searchMakes(String query) throws IllegalArgumentException {
        List<Map<String, Object>> allMakes = getAllMakes();
        List<Map<String, Object>> filtered = new ArrayList<>();

        String lowerQuery = query.toLowerCase();
        for (Map<String, Object> make : allMakes) {
            String makeName = (String) make.get("makeName");
            if (makeName.toLowerCase().contains(lowerQuery)) {
                filtered.add(make);
            }
        }

        return filtered;
    }

    /**
     * Helper method to safely get text value from JsonNode
     */
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field == null || field.isNull() || field.asText().isEmpty()) {
            return "";
        }
        return field.asText();
    }
}
