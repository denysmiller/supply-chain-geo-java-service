package org.ksl.supplychain.geography.binding;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

/**
 * Registers DI bindings
 * @author Miller
 *
 */
public class ComponentFeature implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {
        context.register(new ComponentBinder());
        return true;
    }
}