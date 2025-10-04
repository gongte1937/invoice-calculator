package com.verifyme.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalErrorMapper implements ExceptionMapper<Throwable> {

  private Response text(int status, String msg) {
    return Response.status(status).entity(msg).type(MediaType.TEXT_PLAIN).build();
  }

  @Override
  public Response toResponse(Throwable ex) {
    if (ex instanceof ConstraintViolationException) {
      return text(400, "Error: Invalid request data");
    }
    if (ex instanceof BadRequestException) return text(400, "Error: " + ex.getMessage());
    if (ex instanceof NotFoundException)  return text(404, "Error: " + ex.getMessage());
    return text(500, "Error: internal server error");
  }
}
