package com.verifyme.controller;

import com.verifyme.model.InvoiceRequest;
import com.verifyme.service.InvoiceService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerUnitTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private InvoiceRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new InvoiceRequest();
        // Note: InvoicePayload setup would be done in actual test methods
    }

    @Test
    void total_validRequest_returnsOkResponseWithPlainText() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("123.45");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("123.45", response.getEntity());
        assertEquals(MediaType.TEXT_PLAIN, response.getMediaType().toString());
        verify(invoiceService).calculateTotal(testRequest.invoice);
    }

    @Test
    void total_serviceReturnsZero_returnsZeroAsPlainText() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("0.00");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("0.00", response.getEntity());
        assertEquals(MediaType.TEXT_PLAIN, response.getMediaType().toString());
    }

    @Test
    void total_serviceReturnsLargeNumber_returnsCorrectPlainText() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("999999999.99");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("999999999.99", response.getEntity());
    }

    @Test
    void total_serviceReturnsSmallNumber_returnsCorrectPlainText() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("0.01");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("0.01", response.getEntity());
    }

    @Test
    void total_serviceThrowsNotFoundException_propagatesException() {
        // Given
        NotFoundException expectedException = new NotFoundException("Exchange rate not found");
        when(invoiceService.calculateTotal(any())).thenThrow(expectedException);

        // When & Then
        NotFoundException actualException = assertThrows(NotFoundException.class, 
            () -> invoiceController.total(testRequest));
        
        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(invoiceService).calculateTotal(testRequest.invoice);
    }

    @Test
    void total_serviceThrowsBadRequestException_propagatesException() {
        // Given
        BadRequestException expectedException = new BadRequestException("Invalid exchange rate");
        when(invoiceService.calculateTotal(any())).thenThrow(expectedException);

        // When & Then
        BadRequestException actualException = assertThrows(BadRequestException.class, 
            () -> invoiceController.total(testRequest));
        
        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(invoiceService).calculateTotal(testRequest.invoice);
    }

    @Test
    void total_serviceThrowsRuntimeException_propagatesException() {
        // Given
        RuntimeException expectedException = new RuntimeException("Unexpected error");
        when(invoiceService.calculateTotal(any())).thenThrow(expectedException);

        // When & Then
        RuntimeException actualException = assertThrows(RuntimeException.class, 
            () -> invoiceController.total(testRequest));
        
        assertEquals(expectedException.getMessage(), actualException.getMessage());
        verify(invoiceService).calculateTotal(testRequest.invoice);
    }

    @Test
    void total_callsServiceWithCorrectPayload() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("100.00");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        invoiceController.total(testRequest);

        // Then
        verify(invoiceService, times(1)).calculateTotal(testRequest.invoice);
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    void total_responseContentTypeIsTextPlain() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("42.00");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        assertEquals(MediaType.TEXT_PLAIN, response.getMediaType().toString());
    }

    @Test
    void total_responseEntityIsStringRepresentation() {
        // Given
        BigDecimal expectedTotal = new BigDecimal("123.456");
        when(invoiceService.calculateTotal(any())).thenReturn(expectedTotal);

        // When
        Response response = invoiceController.total(testRequest);

        // Then
        // BigDecimal.toPlainString() should be used (no scientific notation)
        assertEquals("123.456", response.getEntity());
        assertTrue(response.getEntity() instanceof String);
    }
}
