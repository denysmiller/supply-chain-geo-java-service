package org.ksl.supplychain.geography.model.search.criteria;

import java.util.Objects;

import org.ksl.supplychain.geography.model.entity.TransportType;


/**
 * Filtering criteria for search stations operation.
 * 
 * Fields:
 * - name City's name
 * - transportType
 * - address street, zipCode, building number
 * @author Miller
 *
 */
public record StationCriteria (String name, TransportType transportType, String address) {
	/**
	 * Returns filtering criteria to search stations that
	 * contains specified name parameter
	 * @param name
	 * @return
	 */
	public static StationCriteria byName(String name) {
		return new StationCriteria(name);
	}
	
	public StationCriteria() {
		this(null, null, null);
	}

	private StationCriteria(final String name) {
		this(name, null, null);
		Objects.requireNonNull(name);		
	}
	
	public StationCriteria(final TransportType transportType) {
		this(null, transportType, null);
		Objects.requireNonNull(transportType);
	}	
}

