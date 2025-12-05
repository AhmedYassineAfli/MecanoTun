package ca.mcgill.ecse321.repairsystem.dto;

import java.util.*;
import ca.mcgill.ecse321.repairsystem.model.Service.ServiceType;

public class ServiceDto {

	public ServiceDto(ServiceType aType, int aPrice, MechanicDto mechanic, List<AppointmentDto> a) {
		this.serviceType = aType;
		this.price = aPrice;
		this.mechanic = mechanic;
		this.appointments = a;
	}

	public ServiceDto(ServiceType aType) {
		this.serviceType = aType;
	}

	public ServiceDto() {

	}

	private ServiceType serviceType;

	public void setServiceType(ServiceType aType) {
		serviceType = aType;
	}

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

	private List<AppointmentDto> appointments;

	public List<AppointmentDto> getAppointments() {
		return this.appointments;
	}

	public void setAppointments(List<AppointmentDto> appointments) {
		this.appointments = appointments;
	}

	private MechanicDto mechanic;

	public MechanicDto getMechanic() {
		return this.mechanic;
	}

	public void setMechanic(MechanicDto mechanic) {
		this.mechanic = mechanic;
	}

}
