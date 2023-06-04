package org.ksl.supplychain.geography.resource.base;

import java.net.URI;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * Base class for all REST web-services
 * 
 * @author Miller
 *
 */
public abstract class BaseResource {
	private static final String SERVICE_GET_NAME = "findById";
	
    @Context
    private UriInfo uriInfo;	
    
	/**
	 * Shared Response that should be returned if requested operation 
	 * returns no data
	 */
	protected final Response NOT_FOUND;
	
	/**
	 * Returned if client sends request in invalid or unsupported format 
	 */
	protected final Response BAD_REQUEST;

	public BaseResource() {
		NOT_FOUND = Response.status(Response.Status.NOT_FOUND).build();
		
		BAD_REQUEST = Response.status(Response.Status.BAD_REQUEST).build();
	}

	/**
	 * Returns operation result as Response object
	 * 
	 * @param result
	 * @return
	 */
	protected Response ok(Object result) {
		return Response.ok(result).build();
	}
	
	protected Response postForLocation(int id) {
        URI uri = uriInfo.getRequestUriBuilder().path(getClass(), SERVICE_GET_NAME).build(id);
        return Response.created(uri).build();
	}

}
