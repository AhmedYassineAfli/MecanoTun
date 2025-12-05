package ca.mcgill.ecse321.repairsystem.dto;

import java.time.LocalDateTime;

public class RatingDto {

    private int id;
    private int stars;
    private String comment;
    private LocalDateTime createdAt;
    private int customerId;
    private int mechanicId;
    private int appointmentId;

    // Constructors
    public RatingDto() {
    }

    public RatingDto(int id, int stars, String comment, LocalDateTime createdAt,
            int customerId, int mechanicId, int appointmentId) {
        this.id = id;
        this.stars = stars;
        this.comment = comment;
        this.createdAt = createdAt;
        this.customerId = customerId;
        this.mechanicId = mechanicId;
        this.appointmentId = appointmentId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(int mechanicId) {
        this.mechanicId = mechanicId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
