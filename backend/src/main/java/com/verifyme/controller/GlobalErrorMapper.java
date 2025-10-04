package com.verifyme.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.StringJoiner;
import java.util.stream.Collectors;

@Provider
public class GlobalErrorMapper implements ExceptionMapper<Throwable> {

  private Response text(int status, String msg) {
    return Response.status(status).entity("Error: " + msg).type(MediaType.TEXT_PLAIN).build();
  }

  @Override
  public Response toResponse(Throwable ex) {
    if (ex instanceof ConstraintViolationException cve) {
      String message = cve.getConstraintViolations().stream()
          .map(v -> formatViolation(v.getPropertyPath(), v.getMessage()))
          .sorted()
          .collect(Collectors.joining("; "));
      return text(400, message);
    }
    if (ex instanceof BadRequestException) return text(400, ex.getMessage());
    if (ex instanceof NotFoundException)  return text(404, ex.getMessage());
    return text(500, "internal server error");
  }

  private String formatViolation(Path path, String message) {
    if (path == null) return message;

    StringJoiner joiner = new StringJoiner(".");
    for (Path.Node node : path) {
      ElementKind kind = node.getKind();
      if (kind == ElementKind.METHOD
          || kind == ElementKind.PARAMETER
          || kind == ElementKind.CROSS_PARAMETER
          || kind == ElementKind.RETURN_VALUE
          || kind == ElementKind.CONSTRUCTOR
          || kind == ElementKind.BEAN) {
        continue;
      }

      String name = node.getName();
      if (name == null || name.isBlank()) continue;

      if (node.getIndex() != null) {
        joiner.add(name + "[" + node.getIndex() + "]");
      } else if (node.getKey() != null) {
        joiner.add(name + "[" + node.getKey() + "]");
      } else {
        joiner.add(name);
      }
    }

    String pathText = joiner.toString();
    if (pathText.isBlank()) return message;
    return pathText + " " + message;
  }
}
