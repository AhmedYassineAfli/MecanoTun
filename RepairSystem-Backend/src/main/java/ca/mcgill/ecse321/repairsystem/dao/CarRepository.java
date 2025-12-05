package ca.mcgill.ecse321.repairsystem.dao;

import org.springframework.data.repository.CrudRepository;
import ca.mcgill.ecse321.repairsystem.model.*;
import ca.mcgill.ecse321.repairsystem.model.*;
import java.util.*;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "car_data", path = "car_data")
public interface CarRepository extends CrudRepository<Car, String> {

	List<Car> findByCustomer(Customer customer);

	Car findById(int Id);

	List<Car> findAll();
}