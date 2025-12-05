package ca.mcgill.ecse321.repairsystem.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import ca.mcgill.ecse321.repairsystem.dao.TimeSlotRepository;
import ca.mcgill.ecse321.repairsystem.model.Appointment;
import ca.mcgill.ecse321.repairsystem.model.Mechanic;
import ca.mcgill.ecse321.repairsystem.model.TimeSlot;

@Service
public class TimeSlotService {
	@Autowired
	private TimeSlotRepository timeSlotRepository;

	/**
	 * Creates a service and saves new service object in the database
	 * 
	 * @param aStartTime
	 * @param aEndTime
	 * @return
	 */
	@Transactional
	public TimeSlot createTimeSlot(LocalDateTime aStartTime, LocalDateTime aEndTime) {

		if (aStartTime == null) {
			throw new IllegalArgumentException("Start time cannot be null");
		} else if (aEndTime == null) {
			throw new IllegalArgumentException("End time cannot be null");
		}
		TimeSlot timeslot = new TimeSlot(aStartTime, aEndTime);
		timeSlotRepository.save(timeslot);
		return timeslot;
	}

	/**
	 * Getter method to obtain a timeslot by searching by id
	 * 
	 * @param id
	 * @return time slot
	 */
	@Transactional
	public TimeSlot getTimeSlotById(int id) {
		return timeSlotRepository.findById(id);
	}

	/**
	 * Getter method to obtain all time slot by searching by start time
	 * 
	 * @param time
	 * @return list of time slots
	 */
	@Transactional
	public List<TimeSlot> getTimeSlotsByStartTime(LocalDateTime time) {
		return toList(timeSlotRepository.findByStartTime(time));
	}

	/**
	 * Getter method to obtain all time slots by searching by end time
	 * 
	 * @param time
	 * @return list of timeslots
	 */
	@Transactional
	public List<TimeSlot> getTimeSlotsByEndTime(LocalDateTime time) {
		return toList(timeSlotRepository.findByEndTime(time));
	}

	/**
	 * Getter methods to obtain all timeslots
	 * 
	 * @return list of timeslots
	 */
	@Transactional
	public List<TimeSlot> getAllTimeSlots() {
		return toList(timeSlotRepository.findAll());
	}

	/**
	 * Getter method to obtain only available timeslots
	 * A timeslot is available if:
	 * 1. It is in the future (startTime > now)
	 * 2. It has no appointments assigned
	 * 
	 * @return list of available timeslots
	 */
	@Transactional
	public List<TimeSlot> getAvailableTimeSlots() {
		List<TimeSlot> allTimeSlots = toList(timeSlotRepository.findAll());
		List<TimeSlot> availableTimeSlots = new ArrayList<TimeSlot>();
		LocalDateTime now = LocalDateTime.now();

		for (TimeSlot timeSlot : allTimeSlots) {
			// Check if time slot is in the future
			if (timeSlot.getStartTime() != null && timeSlot.getStartTime().isAfter(now)) {
				// Check if time slot has no appointments
				if (timeSlot.getAppointments() == null || timeSlot.getAppointments().isEmpty()) {
					availableTimeSlots.add(timeSlot);
				}
			}
		}

		return availableTimeSlots;
	}

	/**
	 * Add mechanic to timeslot and save time slot in database
	 * 
	 * @param mechanic
	 * @param timeslot
	 */
	@Transactional
	public void addMechanic(Mechanic mechanic, TimeSlot timeslot) {
		timeslot.addMechanic(mechanic);
		timeSlotRepository.save(timeslot);
	}

	/**
	 * add appointment to a time slot and save time slot in database
	 * 
	 * @param appointment
	 * @param timeslot
	 */
	@Transactional
	public void addAppointment(Appointment appointment, TimeSlot timeslot) {
		timeslot.addAppointment(appointment);
		timeSlotRepository.save(timeslot);
	}

	/**
	 * Remove mechanic from a time slot and save time slot in database
	 * 
	 * @param mechanic
	 * @param timeslot
	 */
	@Transactional
	public void removeMechanic(Mechanic mechanic, TimeSlot timeslot) {
		timeslot.removeMechanic(mechanic);
		timeSlotRepository.save(timeslot);
	}

	/**
	 * Remove mechanic from a time slot and save time slot in database
	 * 
	 * @param appointment
	 * @param timeslot
	 */
	@Transactional
	public void removeAppointment(Appointment appointment, TimeSlot timeslot) {
		timeslot.removeAppointment(appointment);
		timeSlotRepository.save(timeslot);
	}

	@Transactional
	@SuppressWarnings("null") // Null check is performed before delete
	public void deleteTimeSlot(int id) {
		TimeSlot t = timeSlotRepository.findById(id);
		if (t == null) {
			throw new IllegalArgumentException("No timeslot with id: " + id + "exists");
		}
		timeSlotRepository.delete(t);
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
