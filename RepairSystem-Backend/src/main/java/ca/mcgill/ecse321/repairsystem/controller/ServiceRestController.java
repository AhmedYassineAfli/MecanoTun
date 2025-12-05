package ca.mcgill.ecse321.repairsystem.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.repairsystem.dto.*;
import ca.mcgill.ecse321.repairsystem.model.Mechanic;
import ca.mcgill.ecse321.repairsystem.model.Service;
import ca.mcgill.ecse321.repairsystem.model.Service.ServiceType;
import ca.mcgill.ecse321.repairsystem.service.*;

@CrossOrigin(origins = "*")
@RestController
public class ServiceRestController {

	@Autowired
	private ServiceService serviceService;
	@Autowired
	private MechanicService mechanicService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private RatingService ratingService;

	/**
	 * restful controller for all services
	 */
	@GetMapping(value = { "/services/mechanic/{id}", "/services/mechanic/{id}/" })
	public List<ServiceDto> getServicesByMechanic(@PathVariable("id") String id) {
		Mechanic mechanic = mechanicService.getMechanicById(Integer.parseInt(id));
		List<Service> services = serviceService.getServicesByMechanic(mechanic);
		List<ServiceDto> dtos = services.stream().map(Converter::convertToDto).collect(Collectors.toList());

		for (ServiceDto dto : dtos) {
			if (dto.getMechanic() != null) {
				int mechanicId = dto.getMechanic().getId();
				dto.getMechanic().setAverageRating(ratingService.getMechanicAverageRating(mechanicId));
				dto.getMechanic().setRatingCount(ratingService.getMechanicRatingCount(mechanicId));
			}
		}
		return dtos;
	}

	@GetMapping(value = { "/services", "/services/" })
	public List<ServiceDto> getAllServices() {
		List<Service> services = serviceService.getAllServices();
		List<ServiceDto> dtos = services.stream().map(Converter::convertToDto).collect(Collectors.toList());

		for (ServiceDto dto : dtos) {
			if (dto.getMechanic() != null) {
				int mechanicId = dto.getMechanic().getId();
				dto.getMechanic().setAverageRating(ratingService.getMechanicAverageRating(mechanicId));
				dto.getMechanic().setRatingCount(ratingService.getMechanicRatingCount(mechanicId));
			}
		}
		return dtos;
	}

	/**
	 * restful controller for getting service by its type
	 */
	@GetMapping(value = { "/services/{serviceType}", "/services/{serviceType}/" })
	public ServiceDto getServiceByServiceType(@PathVariable("serviceType") String serviceType) {
		return Converter.convertToDto(serviceService.getServiceByServiceType(ServiceType.valueOf(serviceType)));
	}

	/**
	 * restful controller for creating service
	 */
	@PostMapping(value = { "/service/{id}", "/service/{id}/" })
	public ServiceDto createService(@PathVariable("id") String id, @RequestParam String type, @RequestParam int price)
			throws IllegalArgumentException {
		Mechanic mechanic = mechanicService.getMechanicById(Integer.parseInt(id));
		Service service = serviceService.createService(ServiceType.valueOf(type), price, mechanic);
		return Converter.convertToDto(service);
	}

}
