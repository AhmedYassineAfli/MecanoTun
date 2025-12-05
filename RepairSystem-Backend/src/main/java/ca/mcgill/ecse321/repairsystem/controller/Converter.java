package ca.mcgill.ecse321.repairsystem.controller;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.repairsystem.dto.*;
import ca.mcgill.ecse321.repairsystem.model.*;

/**
 * @author Thomas Jarvis, Norman Kong
 *         Class converts DAO to DTO.
 *
 */
public class Converter {

	public static AdministrativeAssistantDto convertToDto(AdministrativeAssistant administrativeAssistant) {
		return new AdministrativeAssistantDto(administrativeAssistant.getName(), administrativeAssistant.getId(),
				administrativeAssistant.getPassword(), administrativeAssistant.getPhone(),
				administrativeAssistant.getEmail());
	}

	public static AppointmentDto convertToDto(Appointment appointment) {
		if (appointment == null) {
			throw new IllegalArgumentException("There is no such Appointment!");
		}
		List<MechanicDto> mechanicsDto = new ArrayList<MechanicDto>();
		List<ServiceDto> servicesDto = new ArrayList<ServiceDto>();
		for (Mechanic mechanic : appointment.getMechanics()) {
			MechanicDto m = new MechanicDto(mechanic.getId());
			m.setEmail(mechanic.getEmail());
			mechanicsDto.add(m);

		}

		for (Service service : appointment.getServices()) {
			servicesDto.add(new ServiceDto(service.getServiceType()));
		}
		CustomerDto c = new CustomerDto(appointment.getCustomer().getId());
		c.setEmail(appointment.getCustomer().getEmail());
		c.setName(appointment.getCustomer().getName()); // Added name

		TimeSlotDto t = new TimeSlotDto(appointment.getTimeSlot().getId());
		t.setStartTime(appointment.getTimeSlot().getStartTime());
		t.setEndTime(appointment.getTimeSlot().getEndTime()); // Ensure end time is also set

		CarDto car = new CarDto(appointment.getCar().getId());
		car.setBrand(appointment.getCar().getBrand());
		car.setModel(appointment.getCar().getModel());
		car.setEngine(appointment.getCar().getEngine());
		car.setVin(appointment.getCar().getVin());
		car.setYear(appointment.getCar().getYear());

		return new AppointmentDto(c, appointment.getId(), t, mechanicsDto, car, servicesDto,
				appointment.getNote(), appointment.getStatus().toString());
	}

	public static CarDto convertToDto(Car car) {
		if (car == null) {
			throw new IllegalArgumentException("There is no such Car!");
		}
		List<AppointmentDto> appointmentsDto = new ArrayList<AppointmentDto>();
		for (Appointment appointment : car.getAppointments()) {
			appointmentsDto.add(new AppointmentDto(appointment.getId()));
		}
		return new CarDto(car.getId(), appointmentsDto, new CustomerDto(car.getCustomer().getId()), car.getBrand(),
				car.getModel(),
				car.getEngine(), car.getVin(), car.getYear());
	}

	public static CustomerDto convertToDto(Customer customer) {
		if (customer == null) {
			throw new IllegalArgumentException("There is no such Customer!");
		}
		CustomerDto customerDto = new CustomerDto(customer.getName(), customer.getId(), customer.getPassword(),
				customer.getPhone(), customer.getEmail(), customer.getLastActive(), customer.getAddress());
		List<AppointmentDto> appointmentsDto = new ArrayList<AppointmentDto>();
		List<CarDto> carsDto = new ArrayList<CarDto>();
		for (Car car : customer.getCars()) {
			CarDto c = new CarDto(car.getId());
			carsDto.add(c);
		}
		for (Appointment appointment : customer.getAppointments()) {
			appointmentsDto.add(new AppointmentDto(appointment.getId()));
		}
		customerDto.setAppointments(appointmentsDto);
		customerDto.setCars(carsDto);
		return customerDto;
	}

	public static MechanicDto convertToDto(Mechanic mechanic) {
		if (mechanic == null) {
			throw new IllegalArgumentException("There is no such Mechanic!");
		}
		List<ServiceDto> servicesDto = new ArrayList<ServiceDto>();
		List<AppointmentDto> appointmentsDto = new ArrayList<AppointmentDto>();
		List<TimeSlotDto> timeslotsDto = new ArrayList<TimeSlotDto>();
		for (TimeSlot timeslot : mechanic.getTimeSlots()) {
			timeslotsDto.add(new TimeSlotDto(timeslot.getId(), timeslot.getStartTime(), timeslot.getEndTime()));
		}
		for (Appointment appointment : mechanic.getAppointments()) {
			appointmentsDto.add(new AppointmentDto(appointment.getId()));
		}
		for (Service service : mechanic.getServices()) {
			servicesDto.add(new ServiceDto(service.getServiceType()));
		}
		MechanicDto mechanicDto = new MechanicDto(mechanic.getName(), mechanic.getId(), mechanic.getPassword(),
				mechanic.getPhone(), mechanic.getEmail(), servicesDto);
		mechanicDto.setAppointments(appointmentsDto);
		mechanicDto.setTimeSlots(timeslotsDto);
		mechanicDto.setServices(servicesDto);
		return mechanicDto;
	}

	public static ServiceDto convertToDto(Service service) {
		if (service == null) {
			throw new IllegalArgumentException("There is no such Service!");
		}
		List<AppointmentDto> appointmentsDto = new ArrayList<AppointmentDto>();
		// Check if appointments list is not null before iterating
		if (service.getAppointments() != null) {
			for (Appointment appointment : service.getAppointments()) {
				appointmentsDto.add(new AppointmentDto(appointment.getId()));
			}
		}
		MechanicDto mechanicDto = null;
		if (service.getMechanic() != null) {
			mechanicDto = new MechanicDto(service.getMechanic().getId());
			mechanicDto.setName(service.getMechanic().getName());
		}
		return new ServiceDto(service.getServiceType(), service.getPrice(), mechanicDto, appointmentsDto);
	}

	public static TimeSlotDto convertToDto(TimeSlot timeslot) {
		if (timeslot == null) {
			throw new IllegalArgumentException("There is no such TimeSlot!");
		}
		List<MechanicDto> mechanicsDto = new ArrayList<MechanicDto>();
		List<AppointmentDto> appointmentsDto = new ArrayList<AppointmentDto>();
		for (Mechanic mechanic : timeslot.getMechanics()) {
			mechanicsDto.add(new MechanicDto(mechanic.getId()));
		}
		for (Appointment appointment : timeslot.getAppointments()) {
			appointmentsDto.add(new AppointmentDto(appointment.getId()));
		}
		TimeSlotDto timeSlotDto = new TimeSlotDto(timeslot.getStartTime(), timeslot.getEndTime(), timeslot.getId(),
				mechanicsDto, appointmentsDto);
		return timeSlotDto;
	}

}
