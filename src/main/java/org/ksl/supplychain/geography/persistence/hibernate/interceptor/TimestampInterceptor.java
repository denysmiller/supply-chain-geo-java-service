package org.ksl.supplychain.geography.persistence.hibernate.interceptor;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.ksl.supplychain.geography.model.entity.AbstractEntity;

/**
 * Initializes mandatory timestamp fields for the entities
 * @author Miller
 *
 */
public class TimestampInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 6825201844366406253L;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		int idx = ArrayUtils.indexOf(propertyNames, AbstractEntity.FIELD_CREATED_AT);
		if (idx >= 0) {
			state[idx] = LocalDateTime.now();
			return true;
		}
		return false;
	}
}
