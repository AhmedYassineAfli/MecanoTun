package ca.mcgill.ecse321.repairsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int stars; // 1-5

    @Column(length = 500)
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Mechanic mechanic;

    @OneToOne
    private Appointment appointment;

    // Constructors
    public Rating() {
        this.createdAt = LocalDateTime.now();
    }

    public Rating(int stars, String comment, Customer customer, Mechanic mechanic, Appointment appointment) {
        this.stars = stars;
        this.comment = comment;
        this.customer = customer;
        this.mechanic = mechanic;
        this.appointment = appointment;
        this.createdAt = LocalDateTime.now();
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
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
