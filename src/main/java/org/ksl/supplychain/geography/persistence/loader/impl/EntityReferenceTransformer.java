package org.ksl.supplychain.geography.persistence.loader.impl;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.ksl.supplychain.common.infra.cdi.Cached;
import org.ksl.supplychain.common.infra.cdi.DBSource;
import org.ksl.supplychain.common.infra.exception.ConfigurationException;
import org.ksl.supplychain.common.infra.util.ReflectionUtil;
import org.ksl.supplychain.common.model.transform.TransformableProvider;
import org.ksl.supplychain.common.model.transform.Transformer;
import org.ksl.supplychain.common.model.transform.impl.FieldProvider;
import org.ksl.supplychain.common.model.transform.impl.SimpleDTOTransformer;
import org.ksl.supplychain.geography.model.entity.AbstractEntity;
import org.ksl.supplychain.geography.persistence.loader.EntityLoader;

import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
/**
 * Transformer that is able to manage entity references when copying data
 * from/to DTO to entities
 * 
 * @author Miller
 *
 */
public class EntityReferenceTransformer implements Transformer {

	private final EntityLoader entityLoader;

	/**
	 * Transformer object to delegate to continue working process
	 */
	private final Transformer delegate;

	private final TransformableProvider transformableProvider;

	@Inject
	public EntityReferenceTransformer(@DBSource EntityLoader entityLoader, @Cached FieldProvider fieldProvider,
			TransformableProvider transformableProvider) {
		this.entityLoader = entityLoader;
		this.delegate = new SimpleDTOTransformer(fieldProvider, transformableProvider);
		this.transformableProvider = transformableProvider;
	}

	@Override
	public <T, P> P transform(final T entity, final P dest) {
		Class<T> clz = (Class<T>) entity.getClass();
		Map<String, String> mapping = transformableProvider.find(clz).map(t -> t.getSourceMapping()).orElse(Map.of());

		for (Entry<String, String> entry : mapping.entrySet()) {
			String name = entry.getKey();
			String domainProperty = entry.getValue();
			Object value = ReflectionUtil.getFieldValue(entity, domainProperty);
			if (value instanceof AbstractEntity refEntity) {
				int id = refEntity.getId();
				ReflectionUtil.setFieldValue(dest, name, id);
			} else {
				throw new ConfigurationException(
						"Reference property value of the domain object " + entity + " is not an entity: " + value);
			}
		}

		return delegate.transform(entity, dest);
	}

	@Override
	public <T, P> T untransform(final P dto, final T entity) {
		Class<T> clz = (Class<T>) entity.getClass();
		Map<String, String> mapping = transformableProvider.find(clz).map(t -> t.getSourceMapping()).orElse(Map.of());
		
		for (Entry<String, String> entry : mapping.entrySet()) {
			String name = entry.getKey();
			String domainProperty = entry.getValue();			

			Field dstField = ReflectionUtil.getField(entity.getClass(), domainProperty);
			int id = (int) ReflectionUtil.getFieldValue(dto, name);
			if (id > 0) {
				AbstractEntity value = entityLoader.load((Class) dstField.getType(), id);
				ReflectionUtil.setFieldValue(entity, domainProperty, value);
			}
		}
		delegate.untransform(dto, entity);

		return entity;
	}
}
