package org.ksl.supplychain.geography.model.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.ksl.supplychain.common.infra.util.CommonUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Setter;

/**
 * Any locality that contains transport stations
 * 
 * @author Miller
 *
 */
@Table(name = "CITY")
@Entity(name = "City2")
@NamedQuery(name = City.QUERY_DELETE_ALL, query = "delete from City2")
@NamedQuery(name = City.QUERY_FIND_ALL, query = "from City2")
@Setter
public class City extends AbstractEntity {
	public static final String FIELD_NAME = "name";

	public static final String QUERY_DELETE_ALL = "City.deleteAll";

	public static final String QUERY_FIND_ALL = "City.findAll";

	private String name;

	/**
	 * Name of the district where city is placed
	 */
	private String district;

	/**
	 * Name of the region where district is located. Region is top-level area in the
	 * country
	 */
	private String region;

	/**
	 * Set of transport stations that is linked to this locality
	 */
	private Set<Station> stations;

	public City() {
	}

	public City(final String name) {
		this.name = name;
	}

	@Column(name = "NAME", nullable = false, length = 32)
	public String getName() {
		return name;
	}

	@Column(name = "DISTRICT", nullable = false, length = 32)
	public String getDistrict() {
		return district;
	}

	@Column(name = "REGION", nullable = false, length = 32)
	public String getRegion() {
		return region;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "city", orphanRemoval = true)
	public Set<Station> getStations() {
		return CommonUtil.getSafeSet(stations);
	}

	/**
	 * Adds specified station to the city station list
	 * 
	 * @param station
	 */
	public Station addStation(final TransportType transportType) {
		if (stations == null) {
			stations = new HashSet<>();
		}
		Station station = new Station(this, transportType);
		stations.add(station);

		return station;
	}

	/**
	 * Removes specified station from city station list
	 * 
	 * @param station
	 */
	public void removeStation(final Station station) {
		Objects.requireNonNull(station, "station parameter is not initialized");
		if (stations == null) {
			return;
		}
		stations.remove(station);
	}

}
