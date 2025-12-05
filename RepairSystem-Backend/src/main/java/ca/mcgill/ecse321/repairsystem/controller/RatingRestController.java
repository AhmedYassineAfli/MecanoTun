package ca.mcgill.ecse321.repairsystem.controller;

import ca.mcgill.ecse321.repairsystem.dto.RatingDto;
import ca.mcgill.ecse321.repairsystem.model.Rating;
import ca.mcgill.ecse321.repairsystem.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rating")
public class RatingRestController {

    @Autowired
    private RatingService ratingService;

    /**
     * Submit a rating for a completed appointment
     * POST /rating?customerId=1&mechanicId=2&appointmentId=3&stars=5&comment=Great
     * service!
     */
    @PostMapping
    public ResponseEntity<?> createRating(
            @RequestParam int customerId,
            @RequestParam int mechanicId,
            @RequestParam int appointmentId,
            @RequestParam int stars,
            @RequestParam(required = false) String comment) {
        try {
            Rating rating = ratingService.createRating(customerId, mechanicId, appointmentId, stars, comment);
            RatingDto dto = convertToDto(rating);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get rating for a specific appointment
     * GET /rating/appointment/123
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getRatingByAppointment(@PathVariable int appointmentId) {
        Rating rating = ratingService.getRatingByAppointment(appointmentId);
        if (rating == null) {
            return new ResponseEntity<>("No rating found for this appointment", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(convertToDto(rating), HttpStatus.OK);
    }

    /**
     * Get average rating and count for a mechanic
     * GET /rating/mechanic/2/average
     */
    @GetMapping("/mechanic/{mechanicId}/average")
    public ResponseEntity<?> getMechanicRating(@PathVariable int mechanicId) {
        Double average = ratingService.getMechanicAverageRating(mechanicId);
        int count = ratingService.getMechanicRatingCount(mechanicId);

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", average);
        response.put("ratingCount", count);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Check if an appointment has been rated
     * GET /rating/check/123
     */
    @GetMapping("/check/{appointmentId}")
    public ResponseEntity<?> checkIfRated(@PathVariable int appointmentId) {
        boolean hasRating = ratingService.hasCustomerRatedAppointment(appointmentId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasRating", hasRating);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Helper method to convert Rating to RatingDto
    private RatingDto convertToDto(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getStars(),
                rating.getComment(),
                rating.getCreatedAt(),
                rating.getCustomer().getId(),
                rating.getMechanic().getId(),
                rating.getAppointment().getId());
    }
}
