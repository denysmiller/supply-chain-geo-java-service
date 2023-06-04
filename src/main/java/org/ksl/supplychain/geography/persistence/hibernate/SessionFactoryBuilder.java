package org.ksl.supplychain.geography.persistence.hibernate;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.ksl.supplychain.geography.persistence.hibernate.interceptor.TimestampInterceptor;
import org.reflections.Reflections;

import com.google.common.collect.Streams;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Entity;

/**
 * Component that is responsible for managing Hibernate session factory
 * 
 * @author Miller
 *
 */
@Named
public class SessionFactoryBuilder {
	private static final String PREFIX_PROPERTIES = "hibernate.";

	private final SessionFactory sessionFactory;

	@Inject
	public SessionFactoryBuilder(Config config) {

		Map<String, Object> properties = Streams.stream(config.getPropertyNames())
				.filter(propertyName -> propertyName.startsWith(PREFIX_PROPERTIES))
				.collect(Collectors.toMap(name -> name, name -> config.getConfigValue(name).getValue()));

		ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(properties).build();

		MetadataSources sources = new MetadataSources(registry);

		String basePackage = "org.ksl.supplychain";
		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
		entityClasses.forEach(sources::addAnnotatedClass);

		org.hibernate.boot.SessionFactoryBuilder builder = sources.getMetadataBuilder().build()
				.getSessionFactoryBuilder().applyInterceptor(new TimestampInterceptor());

		sessionFactory = builder.build();
	}

	/**
	 * Returns single instance of session factory
	 * 
	 * @return
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@PreDestroy
	public void destroy() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
