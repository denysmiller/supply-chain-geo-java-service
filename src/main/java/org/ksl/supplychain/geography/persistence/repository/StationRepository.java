package org.ksl.supplychain.geography.persistence.repository;

import java.util.List;

import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;

/**
 * Defines CRUD methods to access Station objects in the persistent storage
 * @author Miller
 *
 */
public interface StationRepository {
	
	/**
	 * Returns all the stations that match specified criteria 
	 * @param stationCriteria
	 * @return
	 */
	List<Station> findAllByCriteria(StationCriteria stationCriteria);
	
	/**
	 * Returns station with specified identifier. If no station exists with such identifier
	 * then null is returned
	 * @param cityId
	 * @return
	 */
	Station findById(int cityId);
	
	/**
	 * Saves(creates or modifies) specified city instance
	 * @param station
	 */
	void save(Station station);

}
