package ca.mcgill.ecse321.repairsystem.dto;

import java.util.*;
import java.util.*;

public class CarDto {
	private int carId;
	private CustomerDto customer;
	private List<AppointmentDto> appointments;
	private String brand;
	private String model;
	private String engine;
	private String vin;
	private Integer year;

	public CarDto(int id, List<AppointmentDto> appointments, CustomerDto customer, String brand, String model,
			String engine, String vin, Integer year) {
		this.carId = id;
		this.appointments = appointments;
		this.customer = customer;
		this.brand = brand;
		this.model = model;
		this.engine = engine;
		this.vin = vin;
		this.year = year;
	}

	public CarDto(int id) {
		this.carId = id;
	}

	public CarDto() {

	}

	public CustomerDto getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}

	public int getId() {
		return carId;
	}

	public List<AppointmentDto> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<AppointmentDto> appointments) {
		this.appointments = appointments;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
}
