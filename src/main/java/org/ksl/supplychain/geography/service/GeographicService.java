package org.ksl.supplychain.geography.service;

import java.util.List;
import java.util.Optional;

import org.ksl.supplychain.common.model.search.criteria.range.RangeCriteria;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;

/**
 * Entry point to perform operations over geographic entities
 * 
 * @author Miller
 *
 */
public interface GeographicService {

	/**
	 * Returns list of existing cities
	 * 
	 * @return
	 */
	List<City> findCities();

	/**
	 * Returns city with specified identifier. If no city is found then empty optional is
	 * returned
	 * 
	 * @param id
	 * @return
	 */
	Optional<City> findCityById(int id);

	/**
	 * Returns all the stations that match specified criteria
	 * @param criteria
	 * @param rangeCriteria
	 * @return
	 */
	List<Station> searchStations(StationCriteria criteria, RangeCriteria rangeCriteria);
	
	/**
	 * Returns station with specified identifier. If no station is found then empty optional is
	 * returned
	 * 
	 * @param id
	 * @return
	 */
	Optional<Station> findStationById(int id);
	
	/**
	 * Saves(creates or modifies) specified city instance
	 * @param station
	 */
	void saveStation(Station station);

	/**
	 * Saves specified city instance
	 * 
	 * @param city
	 */
	void saveCity(City city);
	
	/**
	 * Removes all the cities
	 */
	void deleteCities();

	/**
	 * Saves all specified city instances
	 * @param cities
	 */
	void saveCities(List<City> cities);
	
	/**
	 * Delete city with specified identifier
	 * @param cityId
	 */
	void deleteCity(int cityId);	

}
