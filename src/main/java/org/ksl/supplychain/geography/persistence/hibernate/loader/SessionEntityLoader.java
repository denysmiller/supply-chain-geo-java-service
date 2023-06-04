package org.ksl.supplychain.geography.persistence.hibernate.loader;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.ksl.supplychain.common.infra.cdi.DBSource;
import org.ksl.supplychain.geography.model.entity.AbstractEntity;
import org.ksl.supplychain.geography.persistence.hibernate.SessionFactoryBuilder;
import org.ksl.supplychain.geography.persistence.loader.EntityLoader;

import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

@DBSource
@Slf4j
/**
 * EntityLoader implementation that uses Hibernate Session/Session Factory to
 * load entity 
 * 
 * @author Miller
 *
 */
public class SessionEntityLoader implements EntityLoader {
	private final SessionFactory sessionFactory;

	@Inject
	public SessionEntityLoader(SessionFactoryBuilder builder) {
		sessionFactory = builder.getSessionFactory();
	}

	@Override
	public <T extends AbstractEntity> T load(Class<T> clz, int id) {
		try (Session session = sessionFactory.openSession()) {
			return session.get(clz, id);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new PersistenceException(ex);
		}
	}

}
