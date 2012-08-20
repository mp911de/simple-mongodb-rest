package de.paluch.mongo.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * User: mark Date: 20.08.12 Time: 19:42
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException>{

    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }
}
