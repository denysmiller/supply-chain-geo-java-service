package org.ksl.supplychain.geography.dto.transformable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.ksl.supplychain.common.model.transform.Transformable;
import org.ksl.supplychain.common.model.transform.TransformableProvider;
import org.ksl.supplychain.geography.model.entity.Station;

/**
 * Default provider that returns current {@link Transformable} instances for the project entities
 * @author Miller
 *
 */
public class DefaultTransformableProvider implements TransformableProvider {
	
	private final Map<Class<?>, Transformable<?, ?>> transformables = new HashMap<>();
	
	public DefaultTransformableProvider() {
		transformables.put(Station.class, new StationTransformable());
	}

	@Override
	public <T, P> Optional<Transformable<T, P>> find(Class<T> classT) {
		return (Optional) Optional.ofNullable(transformables.get(classT));
	}
}
