package ca.mcgill.ecse321.repairsystem.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ChatMessage {
    private int id;
    private String content;
    private LocalDateTime timestamp;
    private Person sender;
    private Person receiver;
    private Appointment appointment;
    private boolean seen;

    public ChatMessage() {
    }

    public ChatMessage(String content, Person sender, Person receiver, Appointment appointment) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.appointment = appointment;
        this.timestamp = LocalDateTime.now();
        this.seen = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @ManyToOne
    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    @ManyToOne
    public Person getReceiver() {
        return receiver;
    }

    public void setReceiver(Person receiver) {
        this.receiver = receiver;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @ManyToOne(optional = true)
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
