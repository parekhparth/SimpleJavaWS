package com.parthparekh.service.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * NoDataFoundException mapper class
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Provider
public class NoDataFoundExceptionMapper implements ExceptionMapper<NoDataFoundException> {
	@Override
	public Response toResponse(NoDataFoundException e) {
        Response.ResponseBuilder response = Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON);
        ExceptionMessage msg = new ExceptionMessage(e.getMessage());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        response.entity(gson.toJson(msg) + "\n");
        return response.build();
    }
}