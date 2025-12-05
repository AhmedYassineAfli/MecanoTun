package ca.mcgill.ecse321.repairsystem.model;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;

@Entity
public class Car {

  public Car(int id, Customer customer, String brand, String model,
      String engine) {
    carId = id;
    this.appointments = new ArrayList<Appointment>();
    this.customer = customer;
    this.brand = brand;
    this.model = model;
    this.engine = engine;
  }

  public Car() {

  }

  private int carId;

  @Id
  public int getId() {
    return this.carId;
  }

  public void setId(int aId) {
    this.carId = aId;
  }

  private String brand;

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  private String model;

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  private String engine;

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  private String vin;

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  private Integer year;

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  private Customer customer;

  @ManyToOne()
  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer c) {
    customer = c;
  }

  private List<Appointment> appointments;

  @OneToMany(cascade = { CascadeType.ALL })
  public List<Appointment> getAppointments() {
    return appointments;
  }

  public void setAppointments(List<Appointment> appointmentList) {
    appointments = appointmentList;
  }

  public void addAppointment(Appointment appointment) {
    appointments.add(appointment);
  }

  public void removeAppointment(Appointment appointment) {
    appointments.remove(appointment);
  }

}