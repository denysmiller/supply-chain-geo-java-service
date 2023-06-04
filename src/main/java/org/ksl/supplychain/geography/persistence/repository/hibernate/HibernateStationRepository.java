package org.ksl.supplychain.geography.persistence.repository.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ksl.supplychain.common.infra.cdi.DBSource;
import org.ksl.supplychain.geography.model.entity.City;
import org.ksl.supplychain.geography.model.entity.Station;
import org.ksl.supplychain.geography.model.search.criteria.StationCriteria;
import org.ksl.supplychain.geography.persistence.hibernate.SessionFactoryBuilder;
import org.ksl.supplychain.geography.persistence.repository.StationRepository;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Named
@DBSource
/**
 * Hibernate implementation of StationRepository
 * 
 * @author Miller
 *
 */
public class HibernateStationRepository extends BaseHibernateRepository implements StationRepository {

	@Inject
	public HibernateStationRepository(SessionFactoryBuilder builder) {
		super(builder);
	}

	@Override
	public List<Station> findAllByCriteria(StationCriteria stationCriteria) {
		return query(session -> {

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Station> criteria = builder.createQuery(Station.class);
			Root<Station> root = criteria.from(Station.class);

			List<Predicate> predicates = new ArrayList<>();
			if (stationCriteria.transportType() != null) {
				predicates.add(builder.equal(root.get(Station.FIELD_TRANSPORT_TYPE), stationCriteria.transportType()));
			}
			if (!StringUtils.isEmpty(stationCriteria.name())) {
			    Join<Station, City> city = root.join(Station.FIELD_CITY);
				predicates.add(builder.equal(city.get(City.FIELD_NAME), stationCriteria.name()));
			}
			
			Predicate predicate = builder.and(predicates.toArray(new Predicate[] {}));
			
			criteria.select(root).where(predicate);

			TypedQuery<Station> query = session.createQuery(criteria);

			return query.getResultList();
		});
	}

	@Override
	public Station findById(int stationId) {
		return query(session -> session.get(Station.class, stationId));
	}

	@Override
	public void save(Station station) {
		execute(session -> {
			if (station.getId() == 0) {
				session.persist(station);
			} else {
				session.merge(station);
			}
		});
	}

}
