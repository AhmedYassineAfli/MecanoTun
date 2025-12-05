package ca.mcgill.ecse321.repairsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ca.mcgill.ecse321.repairsystem.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ca.mcgill.ecse321.repairsystem.dao.*;

@Service
public class MechanicService {

	@Autowired
	private MechanicRepository mechanicRepository;
	@Autowired
	private ServiceRepository serviceRepository;
	@Autowired
	private TimeSlotRepository timeslotRepository;
	@Autowired
	private TimeSlotService timeslotService;
	@Autowired
	private AppointmentRepository appointmentRepository;
	@PersistenceContext
	private EntityManager entityManager;
	//////////////////// SERVICE MECHANIC METHODS ////////////////////

	/**
	 * Creates a mechanic and saves new mechanic object in the database
	 * 
	 * @param aName
	 * @param aPassword
	 * @param aPhone
	 * @param aEmail
	 * @param allCapabilities
	 * @return
	 */
	@Transactional
	public Mechanic createMechanic(String aName, String aPassword, long aPhone, String aEmail,
			List<ca.mcgill.ecse321.repairsystem.model.Service> allCapabilities) {

		if (aName == null || aName.trim().length() == 0) {
			throw new IllegalArgumentException("Mechanic name cannot be empty!");

		} else if (aPassword == null || aPassword.trim().length() == 0) {
			throw new IllegalArgumentException("Mechanic password cannot be empty!");
		} else if (aEmail == null || aEmail.trim().length() == 0) {
			throw new IllegalArgumentException("Mechanic email cannot be empty!");
		}
		int id = aEmail.hashCode();
		Mechanic mechanic = new Mechanic(aName, id, aPassword, aPhone, aEmail, allCapabilities);
		mechanicRepository.save(mechanic);
		return mechanic;
	}

	/**
	 * Getter method to obtain mechanic by id
	 * 
	 * @param id
	 * @return associated mechanic
	 */
	@Transactional
	public Mechanic getMechanicById(int id) {
		Mechanic mechanic = mechanicRepository.findById(id);
		return mechanic;
	}

	/**
	 * Getter method to obtain all mechanics by searching by specific name
	 * 
	 * @param name
	 * @return the list of mechanics
	 */
	@Transactional
	public List<Mechanic> getMechanicsByName(String name) {
		List<Mechanic> mechanics = toList(mechanicRepository.findByName(name));
		return mechanics;
	}

	/**
	 * Getter method to obtain a mechanic object by searching by a specific phone
	 * number
	 * 
	 * @param aPhone
	 * @return
	 */
	@Transactional
	public Mechanic getMechanicByNumber(long aPhone) {
		Mechanic mechanic = mechanicRepository.findByPhone(aPhone);
		return mechanic;
	}

	/**
	 * Getter method to obtain a mechanic by searching by a specific email address
	 * 
	 * @param email
	 * @return mechanic object associated to the email
	 */
	@Transactional
	public Mechanic getMechanicByEmail(String email) {
		Mechanic mechanic = mechanicRepository.findByEmail(email);
		return mechanic;
	}

	@Transactional
	public Mechanic addTimeSlots(String oldEmail, String[] timeslotsStart, String[] timeslotsEnd) {
		Mechanic mechanic = mechanicRepository.findByEmail(oldEmail);
		List<TimeSlot> slots = mechanic.getTimeSlots();
		mechanic.setTimeSlots(new ArrayList<TimeSlot>());
		mechanicRepository.save(mechanic);
		for (TimeSlot slot : slots) {
			slot.removeMechanic(mechanic);
			if (slot.getAppointments().size() == 0 && slot.getMechanics().size() == 0) {
				timeslotService.deleteTimeSlot(slot.getId());
			} else {
				timeslotRepository.save(slot);
			}
		}
		String startTime = "";
		String endTime = "";
		List<TimeSlot> timeslotList = new ArrayList<TimeSlot>();
		int day = 5;
		for (int i = 0; i < timeslotsStart.length - 1; i++) {
			// Skip if marked as OFF or empty
			if (timeslotsStart[i] == null || timeslotsStart[i].equals("OFF") || timeslotsStart[i].isEmpty() ||
					timeslotsEnd[i] == null || timeslotsEnd[i].equals("OFF") || timeslotsEnd[i].isEmpty()) {
				day++;
				continue;
			}

			startTime = String.format("2021-04-%02d-%s", day, timeslotsStart[i]);
			endTime = String.format("2021-04-%02d-%s", day, timeslotsEnd[i]);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
			LocalDateTime start = LocalDateTime.parse(startTime, formatter);
			LocalDateTime end = LocalDateTime.parse(endTime, formatter);
			int id = start.hashCode() * end.hashCode();
			TimeSlot t = timeslotRepository.findById(id);
			if (t == null) {
				t = timeslotService.createTimeSlot(start, end);
			}
			t.addMechanic(mechanic);
			timeslotRepository.save(t);
			timeslotList.add(t);
			day++;
		}

		// Handle Saturday (last element)
		if (timeslotsStart[timeslotsStart.length - 1] != null
				&& !timeslotsStart[timeslotsStart.length - 1].equals("OFF")
				&& !timeslotsStart[timeslotsStart.length - 1].isEmpty() &&
				timeslotsEnd[timeslotsEnd.length - 1] != null && !timeslotsEnd[timeslotsEnd.length - 1].equals("OFF")
				&& !timeslotsEnd[timeslotsEnd.length - 1].isEmpty()) {

			startTime = "2021-".concat("04-10-" + timeslotsStart[timeslotsStart.length - 1]);
			endTime = "2021-".concat("04-10-" + timeslotsEnd[timeslotsEnd.length - 1]);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
			LocalDateTime start = LocalDateTime.parse(startTime, formatter);
			LocalDateTime end = LocalDateTime.parse(endTime, formatter);
			int id = start.hashCode() * end.hashCode();
			TimeSlot t = timeslotRepository.findById(id);
			if (t == null) {
				t = timeslotService.createTimeSlot(start, end);
			}
			t.addMechanic(mechanic);
			timeslotRepository.save(t);
			timeslotList.add(t);
		}

		mechanic.setTimeSlots(timeslotList);
		mechanicRepository.save(mechanic);
		return mechanic;
	}

	/**
	 * Add a service to a mechanic and updating the mechanic and service tables in
	 * database
	 * 
	 * @param mechanic
	 * @param service
	 * @return mechanic object
	 */
	@Transactional
	public Mechanic addServiceToMechanic(Mechanic mechanic, ca.mcgill.ecse321.repairsystem.model.Service service) {
		if (mechanic == null || service == null) {
			throw new IllegalArgumentException("Mechanic or Service cannot be null!");
		}

		// Set the mechanic on the service (Many-to-One)
		service.setMechanic(mechanic);
		serviceRepository.save(service);

		// Add service to mechanic's list
		mechanic.addService(service);
		mechanicRepository.save(mechanic);

		return mechanic;
	}

	/**
	 * Removing a service from a mechanic and updating the mechanic and service
	 * table in database
	 * 
	 * @param service
	 * @param mechanic
	 * @return mechanic
	 */
	@Transactional
	public Mechanic removeService(ca.mcgill.ecse321.repairsystem.model.Service service, Mechanic mechanic) {
		if (mechanic.getServices().contains(service)) {
			// Remove service from mechanic's collection
			mechanic.removeService(service);

			// IMPORTANT: We NEVER delete Service entities
			// Services use serviceType as their ID, so there's only ONE Service record per
			// type
			// Multiple mechanics and appointments can reference the same Service
			// We just remove the mechanic-service relationship
			service.setMechanic(null);

			// Save both entities
			serviceRepository.save(service);
			mechanicRepository.save(mechanic);
		}
		return mechanic;
	}

	/**
	 * Obtain all the mechanics of the database
	 * 
	 * @return mechanic
	 */
	@Transactional
	public List<Mechanic> getAllMechanics() {
		return toList(mechanicRepository.findAll());
	}

	@Transactional
	public Mechanic editMechanic(String email, String name, String password, String phone) {
		Mechanic mechanic = mechanicRepository.findByEmail(email);
		mechanic.setName(name);
		mechanic.setPassword(password);
		mechanic.setPhone(Long.parseLong(phone));
		mechanicRepository.save(mechanic);
		return mechanic;
	}

	@Transactional
	public void deleteMechanic(int id) {
		if (mechanicRepository.findById(id) == null) {
			throw new IllegalArgumentException("No mechanic with id: " + id + "exists");
		}
		Mechanic m = mechanicRepository.findById(id);
		for (ca.mcgill.ecse321.repairsystem.model.Service service : m.getServices()) {
			// Service cannot exist without mechanic, so we delete it
			serviceRepository.delete(service);
		}
		List<TimeSlot> slotList = m.getTimeSlots();
		m.setTimeSlots(null);
		mechanicRepository.save(m);
		for (TimeSlot t : slotList) {
			t.removeMechanic(m);
			timeslotRepository.save(t);
			if (t.getAppointments().size() == 0 && t.getMechanics().size() == 0) {
				timeslotService.deleteTimeSlot(t.getId());
			}
		}
		mechanicRepository.delete(m);
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
	 * Update mechanic's schedule with validation
	 * Prevents removing time slots that have booked appointments
	 */
	@Transactional
	public Mechanic updateMechanicSchedule(int mechanicId,
			List<ca.mcgill.ecse321.repairsystem.controller.MechanicRestController.TimeSlotRequest> newTimeSlots) {
		Mechanic mechanic = mechanicRepository.findById(mechanicId);
		if (mechanic == null) {
			throw new IllegalArgumentException("Mechanic not found");
		}

		// Get mechanic's existing appointments
		List<Appointment> appointments = mechanic.getAppointments();

		// Validate that we're not removing time slots with appointments
		for (Appointment app : appointments) {
			// Skip completed appointments - they shouldn't block schedule changes
			if (app.getStatus() == Appointment.AppointmentStatus.Completed) {
				continue;
			}

			TimeSlot appSlot = app.getTimeSlot();
			LocalDateTime appStart = appSlot.getStartTime();
			int appDayOfWeek = appStart.getDayOfWeek().getValue();
			int appHour = appStart.getHour();

			// Check if this appointment's time is covered by new schedule
			boolean isCovered = false;
			for (ca.mcgill.ecse321.repairsystem.controller.MechanicRestController.TimeSlotRequest newSlot : newTimeSlots) {
				if (newSlot.dayOfWeek == appDayOfWeek &&
						appHour >= newSlot.startHour &&
						appHour < newSlot.endHour) {
					isCovered = true;
					break;
				}
			}

			if (!isCovered) {
				// Build helpful error message
				String dayName = "";
				switch (appDayOfWeek) {
					case 1:
						dayName = "Monday";
						break;
					case 2:
						dayName = "Tuesday";
						break;
					case 3:
						dayName = "Wednesday";
						break;
					case 4:
						dayName = "Thursday";
						break;
					case 5:
						dayName = "Friday";
						break;
					case 6:
						dayName = "Saturday";
						break;
					case 7:
						dayName = "Sunday";
						break;
				}
				throw new IllegalArgumentException(
						"Cannot remove working hours - appointment scheduled with " +
								app.getCustomer().getName() + " on " + dayName + " at " + appHour + ":00. " +
								"You must include " + dayName + " " + appHour + ":00-" + (appHour + 1)
								+ ":00 in your schedule.");
			}
		}

		// Clear existing time slots
		List<TimeSlot> oldSlots = new ArrayList<>(mechanic.getTimeSlots());
		for (TimeSlot slot : oldSlots) {
			slot.removeMechanic(mechanic);
			mechanic.removeTimeSlot(slot);
		}

		// Create new time slots
		// Using a reference date (2021-04-05 is Monday)
		LocalDateTime baseMonday = LocalDateTime.of(2021, 4, 5, 0, 0);
		List<TimeSlot> newSlotsList = new ArrayList<>();

		System.out.println("Updating schedule for mechanic " + mechanic.getId());
		System.out.println("Received " + newTimeSlots.size() + " slots from frontend:");
		for (ca.mcgill.ecse321.repairsystem.controller.MechanicRestController.TimeSlotRequest req : newTimeSlots) {
			System.out.println("  Day: " + req.dayOfWeek + ", " + req.startHour + ":00 - " + req.endHour + ":00");
		}

		for (ca.mcgill.ecse321.repairsystem.controller.MechanicRestController.TimeSlotRequest slotReq : newTimeSlots) {
			// Calculate the date for this day of week
			LocalDateTime slotDate = baseMonday.plusDays(slotReq.dayOfWeek - 1);
			LocalDateTime startTime = slotDate.withHour(slotReq.startHour).withMinute(0);
			LocalDateTime endTime = slotDate.withHour(slotReq.endHour).withMinute(0);

			TimeSlot t = timeslotRepository.findByStartTimeAndEndTime(startTime, endTime);
			if (t == null) {
				t = timeslotService.createTimeSlot(startTime, endTime);
			}
			t.addMechanic(mechanic);
			timeslotRepository.save(t);
			newSlotsList.add(t);
		}

		// Set the new timeslots list on the mechanic
		mechanic.setTimeSlots(newSlotsList);
		mechanicRepository.save(mechanic);
		return mechanic;
	}
}