package ca.mcgill.ecse321.repairsystem.model;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Entity;
import java.util.*;

@Entity
public class Service {

	public enum ServiceType {
		CLIMATISATION, BATTERIE, FREINAGE, EMBRAYAGE, ECHAPPEMENT,
		MECANIQUE, VIDANGE, DIAGNOSTIC, REVISION,
		SUSPENSION
	}

	public Service(ServiceType aType, int aPrice) {
		this.serviceType = aType;
		this.price = aPrice;
		this.appointments = new ArrayList<Appointment>();
	}

	public Service() {

	}

	private ServiceType serviceType;

	public void setServiceType(ServiceType aType) {
		serviceType = aType;
	}

	@Id
	public ServiceType getServiceType() {
		return serviceType;
	}

	private int price;

	public void setPrice(int aPrice) {
		price = aPrice;
	}

	public int getPrice() {
		return price;
	}

	private List<Appointment> appointments;

	@ManyToMany
	public List<Appointment> getAppointments() {
		return this.appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

	private Mechanic mechanic;

	@javax.persistence.ManyToOne
	public Mechanic getMechanic() {
		return this.mechanic;
	}

	public void setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public void addAppointment(Appointment appointment) {
		appointments.add(appointment);
	}

	public void removeAppointment(Appointment appointment) {
		appointments.remove(appointment);
	}

}