package org.ksl.supplychain.geography.infra.cdi;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.ksl.supplychain.common.infra.cdi.DBSource;

/**
 * Special class that has to be created for HK2 processor to support @Qualifier annotations
 * @author Miller
 *
 */
public class DBSourceInstance extends AnnotationLiteral<DBSource> implements DBSource {
	public static DBSource get() {
		return new DBSourceInstance();
	}
}