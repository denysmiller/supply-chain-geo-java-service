package org.ksl.supplychain.geography.resource.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.ksl.supplychain.geography.binding.ComponentFeature;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("api")
@OpenAPIDefinition(info = @Info(description = "Geographic API definition", title = "Supply Chain project", version = "2.8.1", 
contact = @Contact(email = "test@gmail.com", name = "Denys Miller", url = "https://ksl.com")))
/**
 * REST web-service configuration for Jersey
 * 
 * @author Miller
 *
 */
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		super(ComponentFeature.class);
		packages("org.ksl.supplychain.geography.resource");
		
		//TODO uncomment when Swagger 3 will support JAX-RS 3.0
		//register(OpenApiResource.class);
	}
}
