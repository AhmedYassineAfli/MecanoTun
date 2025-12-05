package ca.mcgill.ecse321.repairsystem.service;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.repairsystem.dao.*;
import ca.mcgill.ecse321.repairsystem.model.*;
import ca.mcgill.ecse321.repairsystem.model.Appointment.AppointmentStatus;

@Service
public class AppointmentService {
	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private TimeSlotRepository timeslotRepository;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private MechanicRepository mechanicRepository;
	@Autowired
	private ServiceRepository serviceRepository;

	/**
	 * Creating an appointment given a customer, time, car, note
	 * 
	 * @param customer
	 * @param time
	 * @param car
	 * @param note
	 * @return
	 */
	@Transactional
	public Appointment createApp(Customer customer, TimeSlot time, Car car,
			List<ca.mcgill.ecse321.repairsystem.model.Service> services, String note) {
		// input validation
		if (customer == null) {
			throw new IllegalArgumentException("Customer cannot be null");
		} else if (time == null) {
			throw new IllegalArgumentException("TimeSlot cannot be null");
		} else if (car == null) {
			throw new IllegalArgumentException("Car cannot be null");
		}

		// Validation: Appointment must be in the future
		// Note: TimeSlots use a template week (2021-04-05 = Monday, etc.)
		// We need to calculate the next occurrence of this day/time in the real future
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextOccurrence = calculateNextOccurrence(time);

		// Validation: Next occurrence must be at least 1 hour from now
		if (nextOccurrence.isBefore(now.plusHours(1))) {
			throw new IllegalArgumentException("Appointment must be booked at least 1 hour in advance.");
		}

		int id = customer.hashCode() * time.hashCode();
		Appointment app = new Appointment(customer, id, time, car, note);
		app.setServices(services);
		customer.addAppointment(app);
		time.addAppointment(app);
		car.addAppointment(app);
		appointmentRepository.save(app);
		for (ca.mcgill.ecse321.repairsystem.model.Service service : services) {
			service.addAppointment(app);
			serviceRepository.save(service);
		}
		customerRepository.save(customer);
		timeslotRepository.save(time);
		carRepository.save(car);
		return app;
	}

	/**
	 * Automatically finds the first available time slot and creates an appointment
	 */
	@Transactional
	public Appointment createAutoAppointment(Customer customer, Car car,
			List<ca.mcgill.ecse321.repairsystem.model.Service> services, String note) {

		List<TimeSlot> allSlots = toList(timeslotRepository.findAll());
		TimeSlot bestSlot = null;
		LocalDateTime bestTime = null;

		for (TimeSlot slot : allSlots) {
			// Check if slot is available (no active appointments)
			boolean isBooked = false;
			if (slot.getAppointments() != null) {
				for (Appointment app : slot.getAppointments()) {
					if (app.getStatus() != AppointmentStatus.Rejected
							&& app.getStatus() != AppointmentStatus.Completed) {
						isBooked = true;
						break;
					}
				}
			}

			if (isBooked)
				continue;

			LocalDateTime nextOcc = calculateNextOccurrence(slot);

			// Track the earliest available slot
			if (bestTime == null || nextOcc.isBefore(bestTime)) {
				bestTime = nextOcc;
				bestSlot = slot;
			}
		}

		if (bestSlot == null) {
			throw new IllegalArgumentException("No available time slots found. Please try again later.");
		}

		return createApp(customer, bestSlot, car, services, note);
	}

	@Transactional
	public Appointment editApp(Appointment appointment, String status) {
		appointment.setStatus(AppointmentStatus.valueOf(status));
		appointmentRepository.save(appointment);
		return appointment;
	}

	@Transactional
	public Appointment deleteApp(Appointment appointment) {
		for (Mechanic m : appointment.getMechanics()) {
			m.removeAppointment(appointment);
			mechanicRepository.save(m);
		}
		for (ca.mcgill.ecse321.repairsystem.model.Service s : appointment.getServices()) {
			s.removeAppointment(appointment);
			serviceRepository.save(s);
		}
		TimeSlot t = appointment.getTimeSlot();
		t.removeAppointment(appointment);
		timeslotRepository.save(t);
		Car car = appointment.getCar();
		Customer c = appointment.getCustomer();
		car.removeAppointment(appointment);
		c.removeAppointment(appointment);
		customerRepository.save(c);
		carRepository.save(car);
		appointmentRepository.delete(appointment);
		return appointment;
	}

	/**
	 * Getter methods to obtain a customer's list of appointments
	 * 
	 * @param customer
	 * @return
	 */
	@Transactional
	public List<Appointment> getAppointmentsByCustomer(Customer customer) {
		List<Appointment> appointments = toList(appointmentRepository.findByCustomer(customer));
		return appointments;
	}

	/**
	 * Getter methods to obtain a customer's list of car
	 * 
	 * @param car
	 * @return the list of appointments
	 */
	@Transactional
	public List<Appointment> getAppointmentsByCar(Car car) {
		List<Appointment> appointments = toList(appointmentRepository.findByCar(car));
		return appointments;
	}

	/**
	 * Getter methods to obtain a customer's list of appointments by searching by
	 * appointment status
	 * 
	 * @param status
	 * @return the list of appointments
	 */
	@Transactional
	public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
		List<Appointment> appointments = toList(appointmentRepository.findByStatus(status));
		return appointments;
	}

	/**
	 * Getter methods to obtain a customer's list of appointments by searching by
	 * timeslot
	 * 
	 * @param time
	 * @return the list of appointmnets
	 */
	@Transactional
	public List<Appointment> getAppointmentsByTimeSlot(TimeSlot time) {
		List<Appointment> appointments = toList(appointmentRepository.findByTimeSlot(time));
		return appointments;
	}

	/**
	 * Getter methods to obtain a list of a customer's appointment by searching by
	 * id
	 * 
	 * @param id
	 * @return appointment
	 */
	@Transactional
	public Appointment getAppointmentById(int id) {
		Appointment appointment = appointmentRepository.findById(id).orElse(null);
		return appointment;
	}

	/**
	 * Getter method to obtain all the appointments in the database
	 * 
	 * @return list of appointments
	 */
	@Transactional
	public List<Appointment> getAllAppointments() {
		return toList(appointmentRepository.findAll());
	}

	/**
	 * Adding a mechanic to an appointment
	 * 
	 * @param appointment
	 * @param mechanic
	 */
	@Transactional
	public void addMechanic(Appointment appointment, Mechanic mechanic) {
		TimeSlot appSlot = appointment.getTimeSlot();

		// VALIDATION DISABLED: Allow customers to book multiple appointments with same
		// mechanic
		// This validation was too restrictive and prevented legitimate bookings
		/*
		 * // Validation: Check if customer has incomplete appointments with this
		 * mechanic
		 * for (Appointment existingApp : customer.getAppointments()) {
		 * // Check if this appointment is with the same mechanic and not completed
		 * if (existingApp.getMechanics() != null &&
		 * !existingApp.getMechanics().isEmpty()) {
		 * for (Mechanic existingMechanic : existingApp.getMechanics()) {
		 * if (existingMechanic.getId() == mechanic.getId() &&
		 * existingApp.getStatus() != AppointmentStatus.Completed &&
		 * existingApp.getStatus() != AppointmentStatus.Rejected &&
		 * existingApp.getId() != appointment.getId()) {
		 * throw new IllegalArgumentException(
		 * "You already have an incomplete appointment with this mechanic. " +
		 * "Please complete or cancel your existing appointment before booking a new one."
		 * );
		 * }
		 * }
		 * }
		 * }
		 */

		// Validation: Check if mechanic is working at this time
		// Mechanic TimeSlots represent their weekly schedule (e.g., Mon 9-5, Tue 9-5)
		// We need to check if the appointment's day of week and time match the
		// mechanic's schedule
		boolean isWorking = false;
		for (TimeSlot workSlot : mechanic.getTimeSlots()) {
			// Get day of week for both slots
			int appDayOfWeek = appSlot.getStartTime().getDayOfWeek().getValue();
			int workDayOfWeek = workSlot.getStartTime().getDayOfWeek().getValue();

			// Check if same day of week
			if (appDayOfWeek == workDayOfWeek) {
				// Extract time of day (hour and minute) from both slots
				int appStartHour = appSlot.getStartTime().getHour();
				int appStartMinute = appSlot.getStartTime().getMinute();
				int appEndHour = appSlot.getEndTime().getHour();
				int appEndMinute = appSlot.getEndTime().getMinute();

				int workStartHour = workSlot.getStartTime().getHour();
				int workStartMinute = workSlot.getStartTime().getMinute();
				int workEndHour = workSlot.getEndTime().getHour();
				int workEndMinute = workSlot.getEndTime().getMinute();

				// Convert to minutes for easier comparison
				int appStartMinutes = appStartHour * 60 + appStartMinute;
				int appEndMinutes = appEndHour * 60 + appEndMinute;
				int workStartMinutes = workStartHour * 60 + workStartMinute;
				int workEndMinutes = workEndHour * 60 + workEndMinute;

				// Check if appointment time is within working hours
				if (appStartMinutes >= workStartMinutes && appEndMinutes <= workEndMinutes) {
					isWorking = true;
					break;
				}
			}
		}

		if (mechanic.getTimeSlots() != null && !mechanic.getTimeSlots().isEmpty() && !isWorking) {
			throw new IllegalArgumentException("Mechanic is not working at this time.");
		}

		// Validation: Check for overlaps with existing appointments (with 1 hour
		// buffer)
		for (Appointment existingApp : mechanic.getAppointments()) {
			TimeSlot existingSlot = existingApp.getTimeSlot();

			// Check for strict overlap first
			if (appSlot.getStartTime().isBefore(existingSlot.getEndTime()) &&
					existingSlot.getStartTime().isBefore(appSlot.getEndTime())) {
				throw new IllegalArgumentException("Mechanic is already booked at this time.");
			}

			// Check for 1 hour buffer
			// Buffer means: NewApp.Start must be >= ExistingApp.End + 1hr OR NewApp.End <=
			// ExistingApp.Start - 1hr
			// Conversely, if NewApp.Start < ExistingApp.End + 1hr AND NewApp.End >
			// ExistingApp.Start - 1hr, then we have a violation.

			LocalDateTime existingEndPlusBuffer = existingSlot.getEndTime().plusHours(1);
			LocalDateTime existingStartMinusBuffer = existingSlot.getStartTime().minusHours(1);

			if (appSlot.getStartTime().isBefore(existingEndPlusBuffer) &&
					appSlot.getEndTime().isAfter(existingStartMinusBuffer)) {
				throw new IllegalArgumentException("Appointments must be at least 1 hour apart.");
			}
		}

		appointment.addMechanic(mechanic);
		mechanic.addAppointment(appointment);
		appointmentRepository.save(appointment);
		mechanicRepository.save(mechanic);
	}

	/**
	 * Adding a service to an appointment
	 * 
	 * @param appointment
	 * @param service
	 */
	@Transactional
	public void addService(Appointment appointment, ca.mcgill.ecse321.repairsystem.model.Service service) {
		appointment.addService(service);
		service.addAppointment(appointment);
		appointmentRepository.save(appointment);
		serviceRepository.save(service);
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

	/**
	 * Check if a mechanic is available for a customer to book
	 * (customer has no incomplete appointments with this mechanic)
	 * 
	 * @param mechanicId
	 * @param customerId
	 * @return true if available, false otherwise
	 */
	@Transactional
	public boolean isMechanicAvailableForCustomer(int mechanicId, int customerId) {
		Customer customer = customerRepository.findById(customerId);
		if (customer == null) {
			return false;
		}

		for (Appointment existingApp : customer.getAppointments()) {
			if (existingApp.getMechanics() != null && !existingApp.getMechanics().isEmpty()) {
				for (Mechanic existingMechanic : existingApp.getMechanics()) {
					if (existingMechanic.getId() == mechanicId &&
							existingApp.getStatus() != AppointmentStatus.Completed &&
							existingApp.getStatus() != AppointmentStatus.Rejected) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check if a car is available for booking
	 * (car does not have ANY incomplete appointments)
	 * 
	 * @param carId
	 * @return true if available for booking, false otherwise
	 */
	@Transactional
	public boolean isCarAvailable(int carId) {
		Car car = carRepository.findById(carId);
		if (car == null) {
			return false;
		}

		// Strict check for booking: Block if ANY incomplete appointment exists
		for (Appointment appointment : car.getAppointments()) {
			if (appointment.getStatus() != AppointmentStatus.Completed &&
					appointment.getStatus() != AppointmentStatus.Rejected) {
				return false; // Car has an incomplete appointment
			}
		}
		return true;
	}

	/**
	 * Check if a car is available for deletion
	 * (car is not currently in service - status is not CarReceived, InRepair, or
	 * Completed)
	 * Early stage appointments (AppointmentBooked, Confirmed) can be cancelled when
	 * deleting car
	 * 
	 * @param carId
	 * @return true if available for deletion, false otherwise
	 */
	@Transactional
	public boolean isCarDeletable(int carId) {
		Car car = carRepository.findById(carId);
		if (car == null) {
			return false;
		}

		// Only block deletion if car is actually in service
		for (Appointment appointment : car.getAppointments()) {
			if (appointment.getStatus() == AppointmentStatus.CarReceived ||
					appointment.getStatus() == AppointmentStatus.InRepair) {
				return false; // Car is in service
			}
		}
		return true; // Early stage appointments are OK to delete
	}

	/**
	 * Get the mechanic name for a car that is currently in service
	 * 
	 * @param carId
	 * @return mechanic name or null if not in service
	 */
	@Transactional
	public String getCarServiceMechanic(int carId) {
		Car car = carRepository.findById(carId);
		if (car == null) {
			return null;
		}

		for (Appointment appointment : car.getAppointments()) {
			// Return mechanic for any active appointment
			if (appointment.getStatus() != AppointmentStatus.Completed &&
					appointment.getStatus() != AppointmentStatus.Rejected) {
				if (appointment.getMechanics() != null && !appointment.getMechanics().isEmpty()) {
					return appointment.getMechanics().get(0).getName();
				}
			}
		}
		return null;
	}

	/**
	 * Helper method to calculate the next occurrence of a time slot
	 */
	private LocalDateTime calculateNextOccurrence(TimeSlot time) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime appointmentTime = time.getStartTime();

		// Extract day of week and time of day from the template slot
		int appointmentDayOfWeek = appointmentTime.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
		int appointmentHour = appointmentTime.getHour();
		int appointmentMinute = appointmentTime.getMinute();

		// Calculate next occurrence of this day/time
		LocalDateTime nextOccurrence;
		int currentDayOfWeek = now.getDayOfWeek().getValue();

		// Find the next occurrence of the target day
		int daysUntilTarget = (appointmentDayOfWeek - currentDayOfWeek + 7) % 7;
		if (daysUntilTarget == 0) {
			// Same day - check if time has passed
			LocalDateTime todayAtAppointmentTime = now.withHour(appointmentHour).withMinute(appointmentMinute)
					.withSecond(0).withNano(0);
			if (todayAtAppointmentTime.isAfter(now.plusHours(1))) {
				nextOccurrence = todayAtAppointmentTime;
			} else {
				// Time has passed today, use next week
				nextOccurrence = now.plusDays(7).withHour(appointmentHour).withMinute(appointmentMinute).withSecond(0)
						.withNano(0);
			}
		} else {
			// Different day this week
			nextOccurrence = now.plusDays(daysUntilTarget).withHour(appointmentHour).withMinute(appointmentMinute)
					.withSecond(0).withNano(0);
		}

		return nextOccurrence;
	}
}
