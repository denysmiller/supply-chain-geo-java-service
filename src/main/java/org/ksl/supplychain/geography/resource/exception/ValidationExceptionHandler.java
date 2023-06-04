package org.ksl.supplychain.geography.resource.exception;

import org.ksl.supplychain.common.infra.exception.flow.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Jersey exception handler that catches validation errors
 * 
 * @author Miller
 *
 */
@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionHandler.class);

	@Override
	public Response toResponse(ValidationException ex) {
		LOGGER.error(ex.getMessage(), ex);

		return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
	}
}
