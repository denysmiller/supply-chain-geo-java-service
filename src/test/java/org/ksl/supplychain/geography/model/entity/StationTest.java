package org.ksl.supplychain.geography.model.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;
import org.junit.jupiter.api.Test;

/**
 * Verifies functionality of the {@link Station} domain entity
 * @author Miller
 *
 */
public class StationTest {

	@Test
	void testMatchCriteriaNotInitialized() {
		City city = new City("Odessa");
		Station station = new Station(city, TransportType.AUTO);
		
		assertThrows(NullPointerException.class, () -> station.match(null));
	}

	@Test
	void testMatchByNameSuccess() {
		City city = new City("Odessa");
		Station station = new Station(city, TransportType.AUTO);
		
		assertTrue(station.match(StationCriteria.byName("Odessa")));
	}

	@Test
	void testMatchByNameNotFound() {
		City city = new City("Odessa");
		Station station = new Station(city, TransportType.AUTO);
		
		assertFalse(station.match(StationCriteria.byName("Kiev")));
	}
	
}
