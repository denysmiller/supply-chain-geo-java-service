package org.ksl.supplychain.geography.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.microprofile.config.ConfigProvider;
import org.ksl.supplychain.common.model.search.criteria.range.RangeCriteria;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.entity.TransportType;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;
import org.ksl.supplychain.geography.persistence.hibernate.SessionFactoryBuilder;
import org.ksl.supplychain.geography.persistence.repository.CityRepository;
import org.ksl.supplychain.geography.persistence.repository.StationRepository;
import org.ksl.supplychain.geography.persistence.repository.hibernate.HibernateCityRepository;
import org.ksl.supplychain.geography.persistence.repository.hibernate.HibernateStationRepository;
import org.ksl.supplychain.geography.service.GeographicService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Contain unit-tests for {@link GeographicServiceImpl}
 * 
 * @author Miller
 *
 */
public class GeographicServiceImplTest {
	private static final int DEFAULT_CITY_ID = 1;

	private static GeographicService service;

	private static ExecutorService executorService;

	@BeforeAll
	static void setup() {
		SessionFactoryBuilder builder = new SessionFactoryBuilder(ConfigProvider.getConfig());
		CityRepository repository = new HibernateCityRepository(builder);
		StationRepository stationRepository = new HibernateStationRepository(builder);
		service = new GeographicServiceImpl(repository, stationRepository);

		executorService = Executors.newCachedThreadPool();
	}

	@AfterAll
	static void tearDown() {
		executorService.shutdownNow();

		service.deleteCities();
	}

	@Test
	void testNoDataReturnedAtStart() {
		List<City> cities = service.findCities();
		assertTrue(!cities.isEmpty());
	}

	@Test
	void saveCity_validCity_success() {
		int cityCount = service.findCities().size();

		City city = createCity();
		service.saveCity(city);
		
		assertEquals(1, city.getStations().size());
		assertEquals(TransportType.RAILWAY, city.getStations().iterator().next().getTransportType());

		List<City> cities = service.findCities();
		assertEquals(cities.size(), cityCount + 1);
	}

	@Test
	@Disabled
	void testFindCityByIdSuccess() {
		City city = createCity();
		service.saveCity(city);

		Optional<City> foundCity = service.findCityById(DEFAULT_CITY_ID);
		assertTrue(foundCity.isPresent());
		assertEquals(foundCity.get().getId(), DEFAULT_CITY_ID);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1, 1_000_000 })
	void findCityById_incorrectId_returnsEmptyOptional(int incorrectId) {
		Optional<City> foundCity = service.findCityById(incorrectId);
		assertTrue(!foundCity.isPresent());
	}

	@Test
	void testSearchStationsByNameSuccess() {
		City city = new City("Zhytomyr");
		city.setDistrict("Zhytomyr");
		city.setRegion("Zhytomyr");
		city.addStation(TransportType.AUTO);
		city.addStation(TransportType.RAILWAY);
		service.saveCity(city);

		List<Station> stations = service.searchStations(StationCriteria.byName("Zhytomyr"), new RangeCriteria(1, 5));
		assertNotNull(stations);
		assertEquals(stations.size(), 2);
		assertEquals(stations.get(0).getCity(), city);
	}

	@Test
	void testSearchStationsByNameNotFound() {
		List<Station> stations = service.searchStations(StationCriteria.byName("London"), new RangeCriteria(1, 5));
		assertNotNull(stations);
		assertTrue(stations.isEmpty());
	}

	@Test
	void testSearchStationsByTransportTypeSuccess() {
		int stationCount = service.searchStations(new StationCriteria(TransportType.AUTO), new RangeCriteria(1, 5))
				.size();

		City city = createCity();
		city.addStation(TransportType.AUTO);
		service.saveCity(city);
		City city2 = new City("Kiev");
		city2.setDistrict("Kiev");
		city2.setRegion("Kiev");
		city2.addStation(TransportType.AUTO);
		service.saveCity(city2);

		List<Station> stations = service.searchStations(new StationCriteria(TransportType.AUTO),
				new RangeCriteria(1, 5));
		assertNotNull(stations);
		assertEquals(stations.size(), stationCount + 2);
	}

	@Test
	void testSearchStationsByTransportTypeNotFound() {
		City city = createCity();
		city.addStation(TransportType.AUTO);
		service.saveCity(city);
		City city2 = new City("Kiev");
		city2.setDistrict("Kiev");
		city2.setRegion("Kiev");
		city2.setId(2);
		city2.addStation(TransportType.RAILWAY);
		service.saveCity(city2);

		List<Station> stations = service.searchStations(new StationCriteria(TransportType.AVIA),
				new RangeCriteria(1, 5));
		assertNotNull(stations);
		assertTrue(stations.isEmpty());
	}

	@Test
	void testSaveMultipleCitiesSuccess() {
		int cityCount = service.findCities().size();

		int addedCount = 1_000;
		for (int i = 0; i < addedCount; i++) {
			City city = new City("Odessa" + i);
			city.setDistrict("Odessa");
			city.setRegion("Odessa");
			city.addStation(TransportType.AUTO);
			service.saveCity(city);
		}

		List<City> cities = service.findCities();
		assertEquals(cities.size(), cityCount + addedCount);
	}

	@Test
	void testSaveMultipleCitiesInBatchSuccess() {
		int cityCount = service.findCities().size();
		int addedCount = 5_000;

		List<City> cities = new ArrayList<>(addedCount);

		for (int i = 0; i < addedCount; i++) {
			City city = new City("Odessa" + i);
			city.setDistrict("Odessa");
			city.setRegion("Odessa");
			city.addStation(TransportType.AUTO);
			cities.add(city);
		}
		service.saveCities(cities);

		List<City> allCities = service.findCities();
		assertEquals(allCities.size(), cityCount + addedCount);
	}

	@Test
	void testSaveMultipleCitiesConcurrentlySuccess() {
		int cityCount = service.findCities().size();

		int threadCount = 20;
		int batchCount = 5;

		List<Future<?>> futures = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			futures.add(executorService.submit(() -> {
				for (int j = 0; j < batchCount; j++) {
					City city = new City("Lviv_" + Math.random());
					city.setDistrict("Lviv");
					city.setRegion("Lviv");
					city.addStation(TransportType.AUTO);
					service.saveCity(city);
				}
			}));
		}

		waitForFutures(futures);

		List<City> cities = service.findCities();
		assertEquals(cities.size(), cityCount + threadCount * batchCount);
	}

	@Test
	@Disabled
	void testSaveOneCityConcurrentlySuccess() {
		City city = new City("Nikolaev");
		city.setDistrict("Nikolaev");
		city.setRegion("Nikolaev");
		city.addStation(TransportType.AUTO);
		service.saveCity(city);

		int cityCount = service.findCities().size();

		int threadCount = 20;

		List<Future<?>> futures = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			futures.add(executorService.submit(() -> {
				city.setName("Nikolaev" + Math.random());
				service.saveCity(city);
			}));
		}

		waitForFutures(futures);

		List<City> cities = service.findCities();
		assertEquals(cities.size(), cityCount);
	}

	private void waitForFutures(List<Future<?>> futures) {
		futures.forEach(future -> {
			try {
				future.get();
			} catch (Exception e) {
				fail(e.getMessage());
			}
		});
	}

	private City createCity() {
		City city = new City("Odessa");
		city.setDistrict("Odessa");
		city.setRegion("Odessa");

		return city;
	}

}
