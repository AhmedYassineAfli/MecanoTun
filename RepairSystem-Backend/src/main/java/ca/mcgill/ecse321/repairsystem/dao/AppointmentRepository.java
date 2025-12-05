package ca.mcgill.ecse321.repairsystem.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ca.mcgill.ecse321.repairsystem.model.*;
import ca.mcgill.ecse321.repairsystem.model.Appointment.AppointmentStatus;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "appointment_data", path = "appointment_data")
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	List<Appointment> findByCustomer(Customer customer);

	List<Appointment> findByTimeSlot(TimeSlot time);

	List<Appointment> findByCar(Car car);

	List<Appointment> findByStatus(AppointmentStatus status);

	List<Appointment> findAll();

	// Check if a service has any appointments
	@org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Appointment a JOIN a.services s WHERE s = :service")
	long countByService(@org.springframework.data.repository.query.Param("service") Service service);

}
