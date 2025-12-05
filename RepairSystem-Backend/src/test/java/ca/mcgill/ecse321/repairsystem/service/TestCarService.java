package ca.mcgill.ecse321.repairsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import ca.mcgill.ecse321.repairsystem.dao.*;
import ca.mcgill.ecse321.repairsystem.model.*;

@ExtendWith(MockitoExtension.class)
public class TestCarService {
	@Mock
	private CarRepository carDao;
	@Mock
	private CustomerRepository customerDao;

	@InjectMocks
	private CarService carService;
	private static int CAR_ID = 231424;
	private static List<Appointment> APPOINTMENTS = new ArrayList<Appointment>();
	private static Customer CUSTOMER = new Customer("TestPerson", 2001, "123abc", 76523455, "TestPerson@gmail.com",
			"123456789", "987654321", "123 Street Avenue");

	private static int CAR_ID2 = 231425;
	private static List<Appointment> APPOINTMENTS2 = new ArrayList<Appointment>();
	private static Customer CUSTOMER2 = new Customer("TestPerson", 2001, "123abc", 76523455, "TestPerson@gmail.com",
			"123456789", "987654321", "123 Street Avenue");

	@BeforeEach
	public void setMockOutput() {
		lenient().when(carDao.findById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(CAR_ID)) {
				Car car = new Car();
				car.setId(CAR_ID);
				car.setAppointments(APPOINTMENTS);
				car.setCustomer(CUSTOMER);
				return car;
			} else {
				return null;
			}
		});

		lenient().when(carDao.findByCustomer(any(Customer.class))).thenAnswer((InvocationOnMock invocation) -> {
			List<Car> cars = new ArrayList<Car>();
			Customer customer = invocation.getArgument(0);
			if (customer.getId() == CUSTOMER.getId()) {
				Car car = new Car();
				car.setId(CAR_ID);
				car.setAppointments(APPOINTMENTS);
				car.setCustomer(CUSTOMER);
				cars.add(car);
			}
			if (customer.getId() == CUSTOMER2.getId()) {
				Car car = new Car();
				car.setId(CAR_ID2);
				car.setAppointments(APPOINTMENTS2);
				car.setCustomer(CUSTOMER2);
				cars.add(car);
			}
			return cars;
		});

		Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
			return invocation.getArgument(0);
		};

		lenient().when(carDao.save(any(Car.class))).thenAnswer(returnParameterAsAnswer);
		lenient().when(customerDao.save(any(Customer.class))).thenAnswer(returnParameterAsAnswer);

	}

	@Test
	/**
	 * Test the creation of a car object
	 */
	public void testCreateCar() {
		assertEquals(0, carService.getAllCars().size());
		List<Appointment> appointment = new ArrayList<Appointment>();
		Customer customer = new Customer("Marcus", 012123, "password", 6789876, "Marcus@gmail.com", "123456", "678954",
				"123 avenue street");
		Car car = null;
		try {
			car = carService.createCar(customer, "Toyota", "Corolla", "V4");
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertNotNull(car);
		assertEquals("Toyota", car.getBrand());
		assertEquals("Corolla", car.getModel());
		assertEquals("V4", car.getEngine());
		assertEquals(appointment, car.getAppointments());
		assertEquals(customer, car.getCustomer());
	}

	@Test
	/**
	 * Verifies that a customer object is associated to a car
	 */
	public void testCreateCustomerNull() {
		String error = null;
		Customer customer = null;
		Car car = null;

		try {
			car = carService.createCar(customer, "Toyota", "Corolla", "V4");
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertNull(car);
		assertEquals("Customer cannot be null", error);
	}

	@Test
	/**
	 * Verifies that a customer object is associated to a car
	 */
	public void testGetCarById() {
		String error = null;
		Customer customer = new Customer();
		customer.setCars(new ArrayList<Car>());
		List<Appointment> appointments = new ArrayList<Appointment>();
		Car car = carService.createCar(customer, "Toyota", "Corolla", "V4");
		int id = (customer.getId() + "Toyota" + "Corolla").hashCode();

		CAR_ID = id;
		APPOINTMENTS = appointments;
		CUSTOMER = customer;

		try {
			car = carService.getCarById(id);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertNotNull(car);
		assertEquals(car.getId(), id);
	}

	@Test
	/**
	 * Verifies that a customer object is associated to a car
	 */
	public void testGetCarsByCustomer() {
		String error = null;
		Customer customer = new Customer();
		customer.setCars(new ArrayList<Car>());
		customer.setId(100);
		List<Appointment> appointments = new ArrayList<Appointment>();
		Car car = carService.createCar(customer, "Toyota", "Corolla", "V4");
		int id = (customer.getId() + "Toyota" + "Corolla").hashCode();

		CAR_ID = id;
		APPOINTMENTS = appointments;
		CUSTOMER = customer;

		Customer customer2 = customer;
		List<Appointment> appointments2 = new ArrayList<Appointment>();
		Car car2 = carService.createCar(customer2, "Honda", "Civic", "V4");
		int id2 = (customer2.getId() + "Honda" + "Civic").hashCode();

		CAR_ID2 = id2;
		APPOINTMENTS2 = appointments2;
		CUSTOMER2 = customer2;
		List<Car> cars = new ArrayList<Car>();

		try {
			cars = carService.getCarsByCustomer(customer);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		assertNotNull(cars);
		assertEquals(cars.get(0).getId(), id);
		assertEquals(cars.get(0).getCustomer(), customer);
		assertEquals(cars.get(1).getId(), id2);
		assertEquals(cars.get(1).getCustomer(), customer);
	}

}
