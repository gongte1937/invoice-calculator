package com.verifyme.common.error;

import com.verifyme.invoice.config.InvoiceConfig;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GlobalErrorMapper implements ExceptionMapper<Throwable> {

  private static final Logger logger = LoggerFactory.getLogger(GlobalErrorMapper.class);

  @Inject
  InvoiceConfig config;

  private Response text(int status, String msg) {
    return Response.status(status).entity(msg).type(MediaType.TEXT_PLAIN).build();
  }

  @Override
  public Response toResponse(Throwable ex) {
    if (ex instanceof ConstraintViolationException) {
      logger.warn("Validation error: {}", ex.getMessage());
      return text(config.error().badRequestStatus(), config.error().validationErrorMessage());
    }
    if (ex instanceof BadRequestException) {
      logger.warn("Bad request: {}", ex.getMessage());
      return text(config.error().badRequestStatus(), "Error: " + ex.getMessage());
    }
    if (ex instanceof NotFoundException) {
      logger.warn("Not found: {}", ex.getMessage());
      return text(config.error().notFoundStatus(), "Error: " + ex.getMessage());
    }
    
    // Log the full stack trace for 500 errors
    logger.error("Internal server error", ex);
    return text(config.error().internalErrorStatus(), config.error().internalErrorMessage());
  }
}
