package ca.mcgill.ecse321.repairsystem.model;

import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Entity;

@Entity
public class Customer extends Person {
	public Customer(String aName, int id, String aPassword, long aPhone, String aEmail, String add) {
		super(aName, id, aPassword, aPhone, aEmail);
		cars = new ArrayList<Car>();
		appointments = new ArrayList<Appointment>();
		address = add;
		lastActive = Calendar.getInstance();
		LocalDateTime time = LocalDateTime.now();
		lastActive.set(time.getYear(), time.getMonthValue(), time.getDayOfMonth());

	}

	public Customer() {
	}

	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String aAddress) {
		address = aAddress;
	}

	private List<Appointment> appointments;

	@OneToMany(cascade = { CascadeType.ALL })
	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointmentList) {
		appointments = appointmentList;
	}

	public void removeAppointment(Appointment appointment) {
		appointments.remove(appointment);
	}

	private List<Car> cars;

	@OneToMany(cascade = { CascadeType.ALL })
	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> carList) {
		cars = carList;
	}

	public void addCar(Car car) {
		cars.add(car);
	}

	public void removeCar(Car car) {
		cars.remove(car);
	}

	private Calendar lastActive;

	public Calendar getLastActive() {
		return lastActive;
	}

	public void setLastActive(Calendar last) {
		this.lastActive = last;
	}

	public void addAppointment(Appointment appointment) {
		appointments.add(appointment);
	}

}