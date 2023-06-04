package org.ksl.supplychain.geography.resource.exception;

import org.glassfish.jersey.server.ParamException.PathParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
/**
 * This exception handler catches PathParamException that originates from ExtractorException and NumberFormatException
 * and happens when client provides invalid numeric value as a path parameter (for example, identifier)
 * @author Miller
 *
 */
public class PathParamExceptionHandler implements ExceptionMapper<PathParamException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PathParamExceptionHandler.class);

	@Override
	public Response toResponse(PathParamException ex) {
		LOGGER.error(ex.getMessage(), ex);

		return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
	}
}
