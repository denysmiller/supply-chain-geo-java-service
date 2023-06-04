package org.ksl.supplychain.geography.service.impl;

import java.util.List;
import java.util.Optional;

import org.ksl.supplychain.common.infra.cdi.DBSource;
import org.ksl.supplychain.common.model.search.criteria.range.RangeCriteria;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.entity.TransportType;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;
import org.ksl.supplychain.geography.persistence.repository.CityRepository;
import org.ksl.supplychain.geography.persistence.repository.StationRepository;
import org.ksl.supplychain.geography.service.GeographicService;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Default implementation of the {@link GeographicService}
 * 
 * @author Miller
 *
 */
@Named
public class GeographicServiceImpl implements GeographicService {
	private final CityRepository cityRepository;

	private final StationRepository stationRepository;

	@Inject
	public GeographicServiceImpl(@DBSource CityRepository cityRepository,
			@DBSource StationRepository stationRepository) {
		this.cityRepository = cityRepository;
		this.stationRepository = stationRepository;
	}

	@Override
	public List<City> findCities() {
		return cityRepository.findAll();
	}

	@Override
	public void saveCity(City city) {
		if(city.getId() == 0 && (city.getStations() == null || city.getStations().isEmpty())) {
			city.addStation(TransportType.RAILWAY);
		}
		
		cityRepository.save(city);
	}

	@Override
	public Optional<City> findCityById(final int id) {
		return Optional.ofNullable(cityRepository.findById(id));
	}

	@Override
	public List<Station> searchStations(final StationCriteria criteria, final RangeCriteria rangeCriteria) {
		return stationRepository.findAllByCriteria(criteria);
	}

	@Override
	public void deleteCities() {
		cityRepository.deleteAll();
	}

	@Override
	public void saveCities(List<City> cities) {
		cityRepository.saveAll(cities);
	}

	@Override
	public void deleteCity(int cityId) {
		cityRepository.delete(cityId);
	}

	@Override
	public Optional<Station> findStationById(int id) {
		return Optional.ofNullable(stationRepository.findById(id));
	}

	@Override
	public void saveStation(Station station) {
		stationRepository.save(station);
	}
}
