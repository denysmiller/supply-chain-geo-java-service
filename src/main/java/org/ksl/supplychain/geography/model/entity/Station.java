package org.ksl.supplychain.geography.model.entity;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Station where passengers can get off or take specific kind of transport.
 * Multiple stationts compose route of the trip.
 * 
 * @author Miller
 *
 */
@Table(name = "STATION")
@Entity(name = "Station2")
@NamedQuery(name = Station.QUERY_DELETE_ALL, query = "delete from Station2")
@Setter
@EqualsAndHashCode(callSuper = true, of = { "city", "transportType", "address" })
public class Station extends AbstractEntity {
	public static final String FIELD_TRANSPORT_TYPE = "transportType";

	public static final String FIELD_CITY = "city";

	public static final String QUERY_DELETE_ALL = "Station.deleteAll";

	public static final String FIELD_TRIP = "trip";

	private City city;

	private Address address;

	/**
	 * (Optional) Phone of the inquiry office
	 */
	private String phone;

	private Coordinate coordinate;

	private TransportType transportType;

	public Station() {
	}

	/**
	 * You shouldn't create station object directly. Use {@link City} functionality
	 * instead
	 * 
	 * @param city
	 * @param transportType
	 */
	public Station(final City city, final TransportType transportType) {
		this.city = Objects.requireNonNull(city);
		this.transportType = Objects.requireNonNull(transportType);
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "CITY_ID")
	public City getCity() {
		return city;
	}

	@Embedded
	public Address getAddress() {
		return address;
	}

	@Column(name = "PHONE", length = 16)
	public String getPhone() {
		return phone;
	}

	@Embedded
	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Enumerated
	@Column(nullable = false, name = "TRANSPORT_TYPE")
	public TransportType getTransportType() {
		return transportType;
	}

	/**
	 * Verifies if current station matches specified criteria
	 * 
	 * @param criteria
	 * @return
	 */
	public boolean match(final StationCriteria criteria) {
		Objects.requireNonNull(criteria, "Station criteria is not initialized");

		if (!StringUtils.isEmpty(criteria.name())) {
			if (!city.getName().equals(criteria.name())) {
				return false;
			}
		}

		if (criteria.transportType() != null) {
			if (transportType != criteria.transportType()) {
				return false;
			}
		}

		return true;
	}

}
