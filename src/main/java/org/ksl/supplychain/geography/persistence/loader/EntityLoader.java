package org.ksl.supplychain.geography.persistence.loader;

import org.ksl.supplychain.geography.model.entity.AbstractEntity;

@FunctionalInterface
/**
 * Loads and returns entity by entity class and identifier
 * @author Miller
 *
 */
public interface EntityLoader {
	
	/**
	 * Returns entity with specified identifier
	 * @param clz
	 * @param id
	 * @return
	 */
	<T extends AbstractEntity> T load(Class<T> clz, int id);

}
