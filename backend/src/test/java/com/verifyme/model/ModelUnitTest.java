package com.verifyme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.verifyme.invoice.model.InvoiceLine;
import com.verifyme.invoice.dto.InvoicePayload;
import com.verifyme.invoice.dto.InvoiceRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ModelUnitTest {

    @Test
    void invoiceRequest_canBeInstantiated() {
        // When
        InvoiceRequest request = new InvoiceRequest();
        
        // Then
        assertNotNull(request);
        assertNull(request.invoice); // Default value
    }

    @Test
    void invoiceRequest_canSetInvoice() {
        // Given
        InvoiceRequest request = new InvoiceRequest();
        InvoicePayload payload = new InvoicePayload();
        
        // When
        request.invoice = payload;
        
        // Then
        assertEquals(payload, request.invoice);
    }

    @Test
    void invoicePayload_canBeInstantiated() {
        // When
        InvoicePayload payload = new InvoicePayload();
        
        // Then
        assertNotNull(payload);
        assertNull(payload.currency);
        assertNull(payload.date);
        assertNull(payload.lines);
    }

    @Test
    void invoicePayload_canSetAllFields() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        String currency = "USD";
        LocalDate date = LocalDate.of(2023, 1, 15);
        InvoiceLine line = createTestLine();
        
        // When
        payload.currency = currency;
        payload.date = date;
        payload.lines = Arrays.asList(line);
        
        // Then
        assertEquals(currency, payload.currency);
        assertEquals(date, payload.date);
        assertEquals(1, payload.lines.size());
        assertEquals(line, payload.lines.get(0));
    }

    @Test
    void invoiceLine_canBeInstantiated() {
        // When
        InvoiceLine line = new InvoiceLine();
        
        // Then
        assertNotNull(line);
        assertNull(line.description);
        assertNull(line.currency);
        assertNull(line.amount);
    }

    @Test
    void invoiceLine_canSetAllFields() {
        // Given
        InvoiceLine line = new InvoiceLine();
        String description = "Test Item";
        String currency = "USD";
        BigDecimal amount = new BigDecimal("100.00");
        
        // When
        line.description = description;
        line.currency = currency;
        line.amount = amount;
        
        // Then
        assertEquals(description, line.description);
        assertEquals(currency, line.currency);
        assertEquals(amount, line.amount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD"})
    void invoicePayload_acceptsVariousCurrencies(String currency) {
        // Given
        InvoicePayload payload = new InvoicePayload();
        
        // When
        payload.currency = currency;
        
        // Then
        assertEquals(currency, payload.currency);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "GBP", "JPY", "CAD", "AUD"})
    void invoiceLine_acceptsVariousCurrencies(String currency) {
        // Given
        InvoiceLine line = new InvoiceLine();
        
        // When
        line.currency = currency;
        
        // Then
        assertEquals(currency, line.currency);
    }

    @Test
    void invoicePayload_acceptsVariousDates() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        LocalDate[] testDates = {
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2023, 6, 15),
            LocalDate.of(2023, 12, 31),
            LocalDate.now()
        };
        
        for (LocalDate date : testDates) {
            // When
            payload.date = date;
            
            // Then
            assertEquals(date, payload.date);
        }
    }

    @Test
    void invoiceLine_acceptsVariousAmounts() {
        // Given
        InvoiceLine line = new InvoiceLine();
        BigDecimal[] testAmounts = {
            new BigDecimal("0.00"),
            new BigDecimal("0.01"),
            new BigDecimal("100.00"),
            new BigDecimal("999999999.99"),
            new BigDecimal("123.456789")
        };
        
        for (BigDecimal amount : testAmounts) {
            // When
            line.amount = amount;
            
            // Then
            assertEquals(amount, line.amount);
        }
    }

    @Test
    void invoicePayload_canHaveEmptyLinesList() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        
        // When
        payload.lines = Collections.emptyList();
        
        // Then
        assertNotNull(payload.lines);
        assertTrue(payload.lines.isEmpty());
    }

    @Test
    void invoicePayload_canHaveMultipleLines() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        InvoiceLine line1 = createTestLine("Item 1", "USD", "100.00");
        InvoiceLine line2 = createTestLine("Item 2", "EUR", "50.00");
        InvoiceLine line3 = createTestLine("Item 3", "GBP", "25.00");
        
        // When
        payload.lines = Arrays.asList(line1, line2, line3);
        
        // Then
        assertEquals(3, payload.lines.size());
        assertEquals(line1, payload.lines.get(0));
        assertEquals(line2, payload.lines.get(1));
        assertEquals(line3, payload.lines.get(2));
    }

    @Test
    void invoiceLine_descriptionCanContainSpecialCharacters() {
        // Given
        InvoiceLine line = new InvoiceLine();
        String[] specialDescriptions = {
            "Item with spaces",
            "Item-with-dashes",
            "Item_with_underscores",
            "Item (with parentheses)",
            "Item [with brackets]",
            "Item {with braces}",
            "Item with numbers 123",
            "Item with symbols @#$%",
            "Item with unicode: café, naïve, résumé"
        };
        
        for (String description : specialDescriptions) {
            // When
            line.description = description;
            
            // Then
            assertEquals(description, line.description);
        }
    }

    @Test
    void invoicePayload_preservesLineOrder() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        InvoiceLine line1 = createTestLine("First", "USD", "100.00");
        InvoiceLine line2 = createTestLine("Second", "EUR", "200.00");
        InvoiceLine line3 = createTestLine("Third", "GBP", "300.00");
        
        // When
        payload.lines = Arrays.asList(line1, line2, line3);
        
        // Then
        assertEquals("First", payload.lines.get(0).description);
        assertEquals("Second", payload.lines.get(1).description);
        assertEquals("Third", payload.lines.get(2).description);
    }

    @Test
    void invoiceLine_amountPrecisionIsPreserved() {
        // Given
        InvoiceLine line = new InvoiceLine();
        BigDecimal preciseAmount = new BigDecimal("123.456789123456789");
        
        // When
        line.amount = preciseAmount;
        
        // Then
        assertEquals(preciseAmount, line.amount);
        assertEquals(preciseAmount.scale(), line.amount.scale());
    }

    @Test
    void models_supportNullValues() {
        // Given & When
        InvoiceRequest request = new InvoiceRequest();
        InvoicePayload payload = new InvoicePayload();
        InvoiceLine line = new InvoiceLine();
        
        // Then - should not throw exceptions
        assertDoesNotThrow(() -> {
            request.invoice = null;
            payload.currency = null;
            payload.date = null;
            payload.lines = null;
            line.description = null;
            line.currency = null;
            line.amount = null;
        });
    }

    @Test
    void invoicePayload_canContainLinesWithSameCurrency() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        InvoiceLine line1 = createTestLine("Item 1", "USD", "100.00");
        InvoiceLine line2 = createTestLine("Item 2", "USD", "200.00");
        
        // When
        payload.lines = Arrays.asList(line1, line2);
        
        // Then
        assertEquals(2, payload.lines.size());
        assertEquals("USD", payload.lines.get(0).currency);
        assertEquals("USD", payload.lines.get(1).currency);
    }

    @Test
    void invoicePayload_canContainLinesWithDifferentCurrencies() {
        // Given
        InvoicePayload payload = new InvoicePayload();
        InvoiceLine line1 = createTestLine("Item 1", "USD", "100.00");
        InvoiceLine line2 = createTestLine("Item 2", "EUR", "200.00");
        InvoiceLine line3 = createTestLine("Item 3", "GBP", "300.00");
        
        // When
        payload.lines = Arrays.asList(line1, line2, line3);
        
        // Then
        assertEquals(3, payload.lines.size());
        assertEquals("USD", payload.lines.get(0).currency);
        assertEquals("EUR", payload.lines.get(1).currency);
        assertEquals("GBP", payload.lines.get(2).currency);
    }

    private InvoiceLine createTestLine() {
        return createTestLine("Test Item", "USD", "100.00");
    }

    private InvoiceLine createTestLine(String description, String currency, String amount) {
        InvoiceLine line = new InvoiceLine();
        line.description = description;
        line.currency = currency;
        line.amount = new BigDecimal(amount);
        return line;
    }
}
