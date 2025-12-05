package ca.mcgill.ecse321.repairsystem.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.repairsystem.dao.*;
import ca.mcgill.ecse321.repairsystem.model.*;
import ca.mcgill.ecse321.repairsystem.model.Service.ServiceType;

@Service
public class ServiceService {
	@Autowired
	private ServiceRepository serviceRepository;
	@Autowired
	private MechanicRepository mechanicRepository;
	@Autowired
	private AppointmentRepository appointmentRepository;

	/**
	 * Creates a service and saves new service object in the database
	 * 
	 * @param type
	 * @param price
	 * @param mechanic
	 * @return
	 */
	@Transactional
	public ca.mcgill.ecse321.repairsystem.model.Service createService(ServiceType type, int price, Mechanic mechanic) {
		if (price < 0) {
			throw new IllegalArgumentException("Price cannot be negative!");
		}
		if (mechanic == null) {
			throw new IllegalArgumentException("Service must belong to a mechanic!");
		}

		// Reload mechanic to ensure it's managed (attached)
		Mechanic managedMechanic = mechanicRepository.findById(mechanic.getId());
		if (managedMechanic != null) {
			mechanic = managedMechanic;
		}

		ca.mcgill.ecse321.repairsystem.model.Service service = new ca.mcgill.ecse321.repairsystem.model.Service();
		service.setServiceType(type);
		service.setPrice(price);
		service.setMechanic(mechanic);
		mechanic.addService(service);

		serviceRepository.save(service);
		return service;
	}

	/**
	 * Get a service by a type
	 * 
	 * @param type
	 * @return service
	 */
	@Transactional
	public ca.mcgill.ecse321.repairsystem.model.Service getServiceByServiceType(ServiceType type) {
		return serviceRepository.findByServiceType(type);
	}

	@Transactional
	public List<ca.mcgill.ecse321.repairsystem.model.Service> getServicesByMechanic(Mechanic mechanic) {
		if (mechanic == null) {
			throw new IllegalArgumentException("Mechanic cannot be null!");
		}
		return serviceRepository.findServicesByMechanic(mechanic);
	}

	/**
	 * Getter method to obtain all services in the database
	 * 
	 * @return list of service
	 */
	@Transactional
	public List<ca.mcgill.ecse321.repairsystem.model.Service> getAllServices() {
		return (List<ca.mcgill.ecse321.repairsystem.model.Service>) serviceRepository.findAll();
	}

}
