package ca.mcgill.ecse321.repairsystem.service;

import ca.mcgill.ecse321.repairsystem.dao.*;
import ca.mcgill.ecse321.repairsystem.model.Rating;
import ca.mcgill.ecse321.repairsystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public Rating createRating(int customerId, int mechanicId, int appointmentId, int stars, String comment) {
        // Validate inputs
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars");
        }

        // Check if rating already exists for this appointment
        if (ratingRepository.existsByAppointmentId(appointmentId)) {
            throw new IllegalArgumentException("This appointment has already been rated");
        }

        // Get entities
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Mechanic mechanic = mechanicRepository.findById(mechanicId);
        if (mechanic == null) {
            throw new IllegalArgumentException("Mechanic not found");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }

        // Verify appointment is completed
        if (appointment.getStatus() != Appointment.AppointmentStatus.Completed) {
            throw new IllegalArgumentException("Can only rate completed appointments");
        }

        // Verify customer owns the appointment
        if (appointment.getCustomer().getId() != customerId) {
            throw new IllegalArgumentException("Customer can only rate their own appointments");
        }

        // Create and save rating
        Rating rating = new Rating(stars, comment, customer, mechanic, appointment);
        return ratingRepository.save(rating);
    }

    @Transactional
    public Rating getRatingByAppointment(int appointmentId) {
        return ratingRepository.findByAppointmentId(appointmentId);
    }

    @Transactional
    public Double getMechanicAverageRating(int mechanicId) {
        List<Rating> ratings = ratingRepository.findByMechanicId(mechanicId);

        if (ratings == null || ratings.isEmpty()) {
            return null;
        }

        double sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getStars();
        }

        return sum / ratings.size();
    }

    @Transactional
    public int getMechanicRatingCount(int mechanicId) {
        List<Rating> ratings = ratingRepository.findByMechanicId(mechanicId);
        return ratings != null ? ratings.size() : 0;
    }

    @Transactional
    public boolean hasCustomerRatedAppointment(int appointmentId) {
        return ratingRepository.existsByAppointmentId(appointmentId);
    }

    @Transactional
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
}
