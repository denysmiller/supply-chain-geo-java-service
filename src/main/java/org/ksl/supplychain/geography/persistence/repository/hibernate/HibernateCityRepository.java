package org.ksl.supplychain.geography.persistence.repository.hibernate;

import java.util.List;

import org.ksl.supplychain.common.infra.cdi.DBSource;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.persistence.hibernate.SessionFactoryBuilder;
import org.ksl.supplychain.geography.persistence.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Hibernate implementation of {@link CityRepository}
 * 
 * @author Miller
 *
 */
@Named
@DBSource
public class HibernateCityRepository extends BaseHibernateRepository implements CityRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateCityRepository.class);

	@Inject
	public HibernateCityRepository(SessionFactoryBuilder builder) {
		super(builder);
	}

	@Override
	public void save(City city) {
		execute(session -> session.saveOrUpdate(city));
	}

	@Override
	public City findById(int cityId) {
		return query(session -> session.get(City.class, cityId));
	}

	@Override
	public void delete(int cityId) {
		execute(session -> {
			City city = session.get(City.class, cityId);
			if (city != null) {
				session.delete(city);
			}
		});
	}

	@Override
	public List<City> findAll() {
		return query(session -> session.createNamedQuery(City.QUERY_FIND_ALL, City.class).list());
	}

	@Override
	public void deleteAll() {
		execute(session -> {
			session.createNamedQuery(Station.QUERY_DELETE_ALL).executeUpdate();
			int deleted = session.getNamedQuery(City.QUERY_DELETE_ALL).executeUpdate();
			LOGGER.debug("Deleted {} cities", deleted);
		});
	}

	@Override
	public void saveAll(List<City> cities) {
		int batchSize = getBatchSize();
		execute(session -> {
			for (int i = 0; i < cities.size(); i++) {
				session.persist(cities.get(i));
				if (i % batchSize == 0 || i == cities.size() - 1) {
					session.flush();
					session.clear();
				}
			}
		});
	}

}
