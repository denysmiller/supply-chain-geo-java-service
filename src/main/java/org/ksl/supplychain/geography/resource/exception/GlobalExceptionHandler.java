package org.ksl.supplychain.geography.resource.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Global Jersey handler that catches any Exception-based errors
 * 
 * @author Miller
 *
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private final Response SERVER_ERROR;

	public GlobalExceptionHandler() {
		SERVER_ERROR = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}

	@Override
	public Response toResponse(Exception ex) {
		LOGGER.error(ex.getMessage(), ex);

		return SERVER_ERROR;
	}
}
