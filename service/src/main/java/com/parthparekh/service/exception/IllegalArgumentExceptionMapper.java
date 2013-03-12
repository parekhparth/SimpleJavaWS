package com.parthparekh.service.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * IllegalArgumentException mapper class
 *
 * @author: Parth Parekh
 **/
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException e) {
        Response.ResponseBuilder response = Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON);
        ExceptionMessage msg = new ExceptionMessage(e.getMessage());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        response.entity(gson.toJson(msg) + "\n");
        return response.build();
    }
}