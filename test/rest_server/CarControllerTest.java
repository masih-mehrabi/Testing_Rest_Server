package rest_server;


import com.fasterxml.jackson.databind.ObjectMapper;
import rest_server.model.Car;
import rest_server.server.CarController;
import rest_server.server.ModelStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = CarController.class)
class CarControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	void setUp() {
		ModelStorage.createSampleModel();
	}

	@Test
	void testGetAllCars() throws Exception {
		List<Car> cars = ModelStorage.getAllCars();
		ResultActions request = mockMvc.perform(get("/cars")).andDo(print()).andExpect(status().isOk());
		String jsonString = request.andReturn().getResponse().getContentAsString();
		List<Car> car = Arrays.stream(objectMapper.readValue(jsonString, Car[].class)).collect(Collectors.toList());
		assertEquals(car, cars);
		
		
		
	}

	@Test
	void testGetCarByIdExisting() throws Exception {
//		Car car = new Car("2", "blue", "bmw", 20);
//		ModelStorage.saveCar(car);
		List <Car> cars = ModelStorage.getAllCars();
		Car car = cars.get(1);
		
		
		
				mockMvc.perform(get("/cars/" + car.getId()))
						.andDo(print())
						.andExpect(MockMvcResultMatchers.jsonPath("$.color").value(car.getColor()))
						.andExpect(MockMvcResultMatchers.jsonPath("$.brand").value(car.getBrand()))
						.andExpect(MockMvcResultMatchers.jsonPath("$.rentalCostPerDay").value(car.getRentalCostPerDay()))
						.andExpect(status().isOk()
						);
				
//		String jsonString = response.andReturn().getResponse().getContentAsString();
//		int status = response.andReturn().getResponse().getStatus();
//		Car returnedCar = objectMapper.readValue(jsonString, Car.class);
//		assertEquals(returnedCar.getColor(), car.getColor());
//		assertEquals(status, 200 );
	
	}

	@Test
	void testGetCarByIdNotExisting() throws Exception {
		ResultActions response =
				mockMvc.perform(get("/cars/2"))
						.andDo(print()).andExpect(status().isNotFound());
		int status = response.andReturn().getResponse().getStatus();
		assertEquals(status, 404);
	}

	@Test
	void testCreateCar() throws Exception {
		Car car = new Car("2","blue", "bmw", 20);
	ResultActions resultActions = mockMvc
			                              .perform(MockMvcRequestBuilders
					                                       .post("/cars")
					                                       .content(asJsonString(car))
					                                       .contentType(MediaType.APPLICATION_JSON))
			                              .andExpect(status().isOk());
	
	
	int status = resultActions.andReturn().getResponse().getStatus();
	String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
	Car returnedCar = objectMapper.readValue(jsonResponse, Car.class);
	resultActions.andReturn().getResponse();
	System.out.println(ModelStorage.getCarById(car.getId()));
		Car car1 =  ModelStorage.getCarById(returnedCar.getId());
		assertEquals(status, 200);
		assertEquals(car1.getId(), returnedCar.getId());
	
	
	
	
	}

	@Test
	void testUpdateCar() throws Exception {
		Car car = new Car("2","blue", "bmw", 20);
		
		ModelStorage.saveCar(car);
		Car updatedCar = new Car("2", "green", "Benz", 30);
		
		ResultActions resultActions = mockMvc
				                              .perform(MockMvcRequestBuilders
						                                       .put("/cars")
						                                       .content(asJsonString(updatedCar))
						                                       .contentType(MediaType.APPLICATION_JSON))
				                              .andExpect(status().isOk());
		
		
		int status = resultActions.andReturn().getResponse().getStatus();
		String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
		Car returnedCar = objectMapper.readValue(jsonResponse, Car.class);
		resultActions.andReturn().getResponse();

		Car car1 =  ModelStorage.getCarById(returnedCar.getId());

		assertEquals(status, 200);
		assertEquals(updatedCar.getId(), car1.getId());
		assertEquals(car1.getColor(), returnedCar.getColor());
		assertEquals(car1.getBrand(), returnedCar.getBrand());
		assertEquals(car1.getRentalCostPerDay(), returnedCar.getRentalCostPerDay());
		
		assertNotEquals(car1.getColor(), "blue");
		assertNotEquals(car1.getBrand(), "blue");
		assertNotEquals(car1.getRentalCostPerDay(), 20);
		
	}


	@Test
	void testDeleteCar() throws Exception {
		Car car = new Car("2","blue", "bmw", 20);
		ModelStorage.saveCar(car);
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/cars/2")).
				andExpect(status().isOk());
		int status = resultActions.andReturn().getResponse().getStatus();
		List<Car> cars = ModelStorage.getAllCars();
		assertFalse(cars.contains(car));
		assertEquals(status, 200);
	}


}
