package com.verifyme.controller;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.verifyme.common.error.GlobalErrorMapper;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorMapperUnitTest {

    private GlobalErrorMapper errorMapper;

    @BeforeEach
    void setUp() {
        errorMapper = new GlobalErrorMapper();
    }

    @Test
    void toResponse_withNotFoundException_returns404() {
        // Given
        NotFoundException exception = new NotFoundException("Resource not found");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Error: Resource not found", response.getEntity());
    }

    @Test
    void toResponse_withBadRequestException_returns400() {
        // Given
        BadRequestException exception = new BadRequestException("Invalid request");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Error: Invalid request", response.getEntity());
    }

    @Test
    void toResponse_withGenericException_returns500() {
        // Given
        RuntimeException exception = new RuntimeException("Internal server error");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Error: internal server error", response.getEntity());
    }

    @Test
    void toResponse_withNullMessage_handlesGracefully() {
        // Given
        RuntimeException exception = new RuntimeException((String) null);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        // Should handle null message gracefully
        assertNotNull(response.getEntity());
    }

    @Test
    void toResponse_preservesOriginalExceptionMessage() {
        // Given
        String originalMessage = "Specific error details for debugging";
        NotFoundException exception = new NotFoundException(originalMessage);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals("Error: " + originalMessage, response.getEntity());
    }
}
