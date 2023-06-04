package org.ksl.supplychain.geography.resource.exception;

import org.ksl.supplychain.common.infra.exception.flow.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Jersey exception handler that catches errors linked to invalid request parameters
 * 
 * @author Miller
 *
 */
@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<InvalidParameterException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundExceptionHandler.class);

	@Override
	public Response toResponse(InvalidParameterException ex) {
		LOGGER.debug(ex.getMessage(), ex);

		return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
	}
}
