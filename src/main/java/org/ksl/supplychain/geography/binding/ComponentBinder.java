package org.ksl.supplychain.geography.binding;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.ksl.supplychain.common.model.transform.TransformableProvider;
import org.ksl.supplychain.common.model.transform.Transformer;
import org.ksl.supplychain.common.model.transform.impl.CachedFieldProvider;
import org.ksl.supplychain.common.model.transform.impl.FieldProvider;
import org.ksl.supplychain.geography.dto.transformable.DefaultTransformableProvider;
import org.ksl.supplychain.geography.infra.cdi.CachedInstance;
import org.ksl.supplychain.geography.infra.cdi.DBSourceInstance;
import org.ksl.supplychain.geography.persistence.hibernate.SessionFactoryBuilder;
import org.ksl.supplychain.geography.persistence.hibernate.loader.SessionEntityLoader;
import org.ksl.supplychain.geography.persistence.loader.EntityLoader;
import org.ksl.supplychain.geography.persistence.loader.impl.EntityReferenceTransformer;
import org.ksl.supplychain.geography.persistence.repository.CityRepository;
import org.ksl.supplychain.geography.persistence.repository.StationRepository;
import org.ksl.supplychain.geography.persistence.repository.hibernate.HibernateCityRepository;
import org.ksl.supplychain.geography.persistence.repository.hibernate.HibernateStationRepository;
import org.ksl.supplychain.geography.service.GeographicService;
import org.ksl.supplychain.geography.service.impl.GeographicServiceImpl;

import jakarta.inject.Singleton;

/**
 * Binds bean implementations and implemented interfaces
 * @author Miller
 *
 */
public class ComponentBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(HibernateCityRepository.class).to(CityRepository.class).in(Singleton.class).qualifiedBy(new DBSourceInstance());
        bind(HibernateStationRepository.class).to(StationRepository.class).in(Singleton.class).qualifiedBy(new DBSourceInstance());
        bind(EntityReferenceTransformer.class).to(Transformer.class).in(Singleton.class);
        bind(SessionEntityLoader.class).to(EntityLoader.class).in(Singleton.class).qualifiedBy(new DBSourceInstance());
        bind(CachedFieldProvider.class).to(FieldProvider.class).in(Singleton.class).qualifiedBy(new CachedInstance());
        bind(GeographicServiceImpl.class).to(GeographicService.class).in(Singleton.class);
        bind(SessionFactoryBuilder.class).to(SessionFactoryBuilder.class).in(Singleton.class);
        bindFactory(() -> ConfigProvider.getConfig()).to(Config.class).in(Singleton.class);
        bind(DefaultTransformableProvider.class).to(TransformableProvider.class).in(Singleton.class);
    }
}