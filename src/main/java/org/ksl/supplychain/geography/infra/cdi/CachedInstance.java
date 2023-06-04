package org.ksl.supplychain.geography.infra.cdi;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.ksl.supplychain.common.infra.cdi.Cached;

/**
 * Special class that has to be created for HK2 processor to support @Qualifier annotations
 * @author Miller
 *
 */
public class CachedInstance extends AnnotationLiteral<Cached> implements Cached {
	public static Cached get() {
		return new CachedInstance();
	}
}