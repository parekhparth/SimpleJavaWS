package com.parthparekh.service.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Runtime mapper class
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException e) {
        Response.ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON);
        ExceptionMessage msg = new ExceptionMessage("something went terribly wrong: " + e.getMessage());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        response.entity(gson.toJson(msg) + "\n");
        return response.build();
    }
}