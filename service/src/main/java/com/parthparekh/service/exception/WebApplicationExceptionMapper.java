package com.parthparekh.service.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * WebApplicationException mapper class
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException e) {
        int status = e.getResponse().getStatus();
        if (status == 0) {
            logger.warn("unexpected empty status", e);
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
        ExceptionMessage msg = new ExceptionMessage(e.getMessage());
        Response.ResponseBuilder response = Response.status(status).type(MediaType.APPLICATION_JSON);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        response.entity(gson.toJson(msg) + "\n");
        return response.build();
    }

}