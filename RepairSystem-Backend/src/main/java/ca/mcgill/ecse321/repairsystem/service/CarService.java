package ca.mcgill.ecse321.repairsystem.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.repairsystem.dao.*;
import ca.mcgill.ecse321.repairsystem.model.*;

@Service
public class CarService {

	@Autowired
	private CarRepository carRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	@Autowired
	private RatingRepository ratingRepository;

	//////////////////// SERVICE CAR METHODS ////////////////////

	/**
	 * Creating a car object
	 * 
	 * @param type
	 * @param winterTires
	 * @param numOfKm
	 * @param customer
	 * @return car object
	 */
	/**
	 * Creating a car object
	 * 
	 * @param customer
	 * @param brand
	 * @param model
	 * @param engine
	 * @return car object
	 */
	@Transactional
	public Car createCar(Customer customer, String brand, String model,
			String engine) {

		if (customer == null) {
			throw new IllegalArgumentException("Customer cannot be null");
		}
		int id = (customer.getId() + brand + model).hashCode();
		Car c = new Car(id, customer, brand, model, engine);
		customer.addCar(c);
		carRepository.save(c);
		customerRepository.save(customer);
		return c;
	}

	/**
	 * Getter method to obtain a car by searching by id
	 * 
	 * @param id
	 * @return the car associated to the id
	 */
	@Transactional
	public Car getCarById(int id) {
		Car c = carRepository.findById(id);
		return c;
	}

	/**
	 * Getter method to obtain the list of cars associated by searching by customer
	 * 
	 * @param customer
	 * @return list of cars associated to input customer
	 */
	@Transactional
	public List<Car> getCarsByCustomer(Customer customer) {
		return toList(carRepository.findByCustomer(customer));
	}

	/**
	 * Getter method to obtain the list of car existing in the database
	 * 
	 * @return list of cars
	 */
	@Transactional
	public List<Car> getAllCars() {
		return toList(carRepository.findAll());
	}

	@Transactional
	public Car updateCar(Car car) {
		carRepository.save(car);
		return car;
	}

	@Transactional
	public void deleteCar(Car car) {
		Customer customer = car.getCustomer();

		// Delete ALL appointments associated with the car to prevent FK violations
		// We iterate over a copy of the list to avoid ConcurrentModificationException
		List<Appointment> appointmentsToDelete = new ArrayList<>(car.getAppointments());
		// Remove appointments
		for (Appointment appointment : appointmentsToDelete) {
			car.removeAppointment(appointment);
			if (customer != null) {
				customer.removeAppointment(appointment);
			}

			// Remove from mechanics
			List<Mechanic> mechanics = appointment.getMechanics();
			if (mechanics != null) {
				for (Mechanic mechanic : mechanics) {
					mechanic.removeAppointment(appointment);
				}
			}

			// Remove from services
			List<ca.mcgill.ecse321.repairsystem.model.Service> services = appointment.getServices();
			if (services != null) {
				for (ca.mcgill.ecse321.repairsystem.model.Service service : services) {
					service.removeAppointment(appointment);
				}
			}

			// Delete associated chat messages
			List<ChatMessage> messages = chatMessageRepository
					.findByAppointmentIdOrderByTimestampAsc(appointment.getId());
			if (messages != null && !messages.isEmpty()) {
				chatMessageRepository.deleteAll(messages);
				chatMessageRepository.flush();
			}

			// Delete associated rating
			Rating rating = ratingRepository.findByAppointmentId(appointment.getId());
			if (rating != null) {
				ratingRepository.delete(rating);
				ratingRepository.flush();
			}

			// Clear TimeSlot reference
			TimeSlot timeSlot = appointment.getTimeSlot();
			if (timeSlot != null) {
				timeSlot.removeAppointment(appointment);
				appointment.setTimeSlot(null);
				// If timeslot has no more appointments, we could delete it, but for now just
				// unlink
			}

			appointmentRepository.delete(appointment);
		}

		// Flush to ensure appointments are deleted before car deletion is attempted
		appointmentRepository.flush();

		// Save customer to persist removal of appointments from their list
		if (customer != null) {
			customerRepository.save(customer);
		}

		// Remove car from customer
		if (customer != null) {
			customer.removeCar(car);
			customerRepository.save(customer);
		}

		carRepository.delete(car);
	}

	/*
	 * helper method
	 */
	private <T> List<T> toList(Iterable<T> iterable) {
		List<T> resultList = new ArrayList<T>();
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}

}
