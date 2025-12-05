package ca.mcgill.ecse321.repairsystem.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.repairsystem.dto.*;
import ca.mcgill.ecse321.repairsystem.model.*;
import ca.mcgill.ecse321.repairsystem.model.Appointment.AppointmentStatus;
import ca.mcgill.ecse321.repairsystem.service.*;

@CrossOrigin(origins = "*")
@RestController
public class CarRestController {

	@Autowired
	private CarService carService;
	@Autowired
	private CustomerService customerService;

	/**
	 * Rest controller for getting car by customer
	 */
	@GetMapping(value = { "/cars/{customerId}", "/cars/{customerId}/" })
	public List<CarDto> getCarsByCustomer(@PathVariable("customerId") String customerId) {
		Customer customer = customerService.getCustomerById(Integer.parseInt(customerId));
		List<CarDto> carsDto = new ArrayList<CarDto>();
		for (Car car : carService.getCarsByCustomer(customer)) {
			carsDto.add(Converter.convertToDto(car));
		}
		return carsDto;
	}

	/**
	 * Rest controller for creating car
	 */
	@PostMapping(value = { "/car/{customerId}", "/car/{customerId}/" })
	public CarDto createCar(@PathVariable("customerId") String customerId,
			@RequestParam(required = false) String brand,
			@RequestParam(required = false) String model,
			@RequestParam(required = false) String engine,
			@RequestParam(required = false) String vin,
			@RequestParam(required = false) Integer year) throws IllegalArgumentException {
		Customer customer = customerService.getCustomerById(Integer.parseInt(customerId));
		Car car = carService.createCar(customer, brand, model, engine);
		car.setVin(vin);
		car.setYear(year);
		carService.updateCar(car);
		return Converter.convertToDto(car);
	}

	/**
	 * Rest controller for updating car
	 */
	@org.springframework.web.bind.annotation.PutMapping(value = { "/car/{id}", "/car/{id}/" })
	public CarDto updateCar(@PathVariable("id") String id,
			@RequestParam(required = false) String brand,
			@RequestParam(required = false) String model,
			@RequestParam(required = false) String engine,
			@RequestParam(required = false) String vin,
			@RequestParam(required = false) Integer year) throws IllegalArgumentException {
		Car car = carService.getCarById(Integer.parseInt(id));
		car.setBrand(brand);
		car.setModel(model);
		car.setEngine(engine);
		car.setVin(vin);
		car.setYear(year);
		carService.updateCar(car);
		return Converter.convertToDto(car);
	}

	/**
	 * Rest controller for deleting car
	 */
	@org.springframework.web.bind.annotation.DeleteMapping(value = { "/car/{id}", "/car/{id}/" })
	public CarDto deleteCar(@PathVariable("id") String id) throws IllegalArgumentException {
		Car car = carService.getCarById(Integer.parseInt(id));
		carService.deleteCar(car);
		return Converter.convertToDto(car);
	}

	/**
	 * Get appointments for a car (for deletion confirmation)
	 */
	@GetMapping(value = { "/car/{id}/appointments", "/car/{id}/appointments/" })
	public List<AppointmentDto> getCarAppointments(@PathVariable("id") String id) {
		Car car = carService.getCarById(Integer.parseInt(id));
		List<AppointmentDto> appointmentsDto = new ArrayList<>();
		for (Appointment appointment : car.getAppointments()) {
			// Only return early-stage appointments that will be deleted
			if (appointment.getStatus() != AppointmentStatus.CarReceived &&
					appointment.getStatus() != AppointmentStatus.InRepair &&
					appointment.getStatus() != AppointmentStatus.Completed) {
				appointmentsDto.add(Converter.convertToDto(appointment));
			}
		}
		return appointmentsDto;
	}

}
