package com.verifyme.common.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ValidationErrorMapper implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public Response toResponse(ConstraintViolationException exception) {
    String errorMessage = exception.getConstraintViolations()
        .stream()
        .map(this::formatViolation)
        .collect(Collectors.joining(", "));
    
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.TEXT_PLAIN)
        .entity("Error: " + errorMessage)
        .build();
  }
  
  private String formatViolation(ConstraintViolation<?> violation) {
    String fieldName = violation.getPropertyPath().toString();
    String message = violation.getMessage();
    return fieldName + " " + message;
  }
}
