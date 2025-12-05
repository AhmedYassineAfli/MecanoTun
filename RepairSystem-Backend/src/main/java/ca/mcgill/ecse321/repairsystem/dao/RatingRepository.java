package ca.mcgill.ecse321.repairsystem.dao;

import ca.mcgill.ecse321.repairsystem.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "rating_data", path = "rating_data")
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Rating findById(int id);

    Rating findByAppointmentId(int appointmentId);

    List<Rating> findByMechanicId(int mechanicId);

    boolean existsByAppointmentId(int appointmentId);
}
