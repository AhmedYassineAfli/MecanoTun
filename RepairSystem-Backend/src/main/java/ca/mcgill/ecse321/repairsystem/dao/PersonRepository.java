package ca.mcgill.ecse321.repairsystem.dao;

import ca.mcgill.ecse321.repairsystem.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    Person findByEmail(String email);
}
