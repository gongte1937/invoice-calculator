package com.verifyme.service;

import com.verifyme.common.client.FrankfurterClient;
import com.verifyme.common.client.FrankfurterResponse;
import com.verifyme.invoice.model.InvoiceLine;
import com.verifyme.invoice.dto.InvoicePayload;
import com.verifyme.invoice.service.InvoiceService;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceUnitTest {

    @Mock
    private FrankfurterClient frankfurterClient;

    @InjectMocks
    private InvoiceService invoiceService;

    private InvoicePayload testPayload;
    private InvoiceLine testLine;

    @BeforeEach
    void setUp() {
        testLine = new InvoiceLine();
        testLine.description = "Test Item";
        testLine.currency = "USD";
        testLine.amount = new BigDecimal("100.00");

        testPayload = new InvoicePayload();
        testPayload.currency = "USD";
        testPayload.date = LocalDate.of(2023, 1, 15);
        testPayload.lines = Arrays.asList(testLine);
    }

    @Test
    void calculateTotal_singleLineSameCurrency_returnsCorrectTotal() {
        // Given
        testLine.amount = new BigDecimal("123.456");

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("123.46"), result);
        verifyNoInteractions(frankfurterClient);
    }

    @Test
    void calculateTotal_currencyTrimsWhitespace() {
        // Given
        testPayload.currency = "  USD  ";
        testLine.currency = "  USD  ";

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("100.00"), result);
        verifyNoInteractions(frankfurterClient);
    }

    @Test
    void calculateTotal_currencyIsCaseInsensitive() {
        // Given
        testPayload.currency = "usd";
        testLine.currency = "USD";

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("100.00"), result);
        verifyNoInteractions(frankfurterClient);
    }

    @Test
    void calculateTotal_differentCurrency_callsExchangeRateAPI() {
        // Given
        testLine.currency = "EUR";
        FrankfurterResponse response = createMockResponse("USD", 1.0850);
        when(frankfurterClient.getHistoricalRate("2023-01-15", "EUR", "USD"))
                .thenReturn(response);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("108.50"), result);
        verify(frankfurterClient).getHistoricalRate("2023-01-15", "EUR", "USD");
    }

    @Test
    void calculateTotal_exchangeRateRoundedToFourDecimals() {
        // Given
        testLine.currency = "EUR";
        testLine.amount = new BigDecimal("100.00");
        FrankfurterResponse response = createMockResponse("USD", 1.123456789);
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        // 100.00 * 1.1235 (rounded to 4 decimals) = 112.35
        assertEquals(new BigDecimal("112.35"), result);
    }

    @Test
    void calculateTotal_resultRoundedToTwoDecimals() {
        // Given
        testLine.currency = "EUR";
        testLine.amount = new BigDecimal("33.333");
        FrankfurterResponse response = createMockResponse("USD", 1.1234);
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        // 33.333 * 1.1234 = 37.45 (rounded to 2 decimals)
        assertEquals(new BigDecimal("37.45"), result);
    }

    @Test
    void calculateTotal_dateFormattedCorrectly() {
        // Given
        testPayload.date = LocalDate.of(2023, 12, 25);
        testLine.currency = "EUR";
        FrankfurterResponse response = createMockResponse("USD", 1.0850);
        when(frankfurterClient.getHistoricalRate("2023-12-25", "EUR", "USD"))
                .thenReturn(response);

        // When
        invoiceService.calculateTotal(testPayload);

        // Then
        verify(frankfurterClient).getHistoricalRate("2023-12-25", "EUR", "USD");
    }

    @Test
    void calculateTotal_apiThrowsException_throwsNotFoundException() {
        // Given
        testLine.currency = "EUR";
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Network error"));

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("cannot fetch exchange rate"));
        assertTrue(exception.getMessage().contains("EUR->USD"));
        assertTrue(exception.getMessage().contains("2023-01-15"));
    }

    @Test
    void calculateTotal_nullResponse_throwsNotFoundException() {
        // Given
        testLine.currency = "EUR";
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("exchange rate not found"));
    }

    @Test
    void calculateTotal_nullRates_throwsNotFoundException() {
        // Given
        testLine.currency = "EUR";
        FrankfurterResponse response = new FrankfurterResponse();
        response.rates = null;
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("exchange rate not found"));
    }

    @Test
    void calculateTotal_missingTargetCurrency_throwsNotFoundException() {
        // Given
        testLine.currency = "EUR";
        FrankfurterResponse response = new FrankfurterResponse();
        response.rates = new HashMap<>();
        response.rates.put("GBP", 0.8750); // Different currency
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("exchange rate not found"));
    }

    @Test
    void calculateTotal_zeroExchangeRate_throwsBadRequestException() {
        // Given
        testLine.currency = "EUR";
        FrankfurterResponse response = createMockResponse("USD", 0.0);
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("invalid rate"));
    }

    @Test
    void calculateTotal_negativeExchangeRate_throwsBadRequestException() {
        // Given
        testLine.currency = "EUR";
        FrankfurterResponse response = createMockResponse("USD", -1.5);
        when(frankfurterClient.getHistoricalRate(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> invoiceService.calculateTotal(testPayload));
        
        assertTrue(exception.getMessage().contains("invalid rate"));
    }

    @Test
    void calculateTotal_multipleLinesSameCurrency_sumsCorrectly() {
        // Given
        InvoiceLine line1 = createLine("Item 1", "USD", "100.25");
        InvoiceLine line2 = createLine("Item 2", "USD", "200.50");
        InvoiceLine line3 = createLine("Item 3", "USD", "300.75");
        testPayload.lines = Arrays.asList(line1, line2, line3);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("601.50"), result);
    }

    @Test
    void calculateTotal_mixedCurrencies_convertsAndSums() {
        // Given
        InvoiceLine usdLine = createLine("USD Item", "USD", "100.00");
        InvoiceLine eurLine = createLine("EUR Item", "EUR", "50.00");
        InvoiceLine gbpLine = createLine("GBP Item", "GBP", "25.00");
        testPayload.lines = Arrays.asList(usdLine, eurLine, gbpLine);

        FrankfurterResponse eurResponse = createMockResponse("USD", 1.0850);
        FrankfurterResponse gbpResponse = createMockResponse("USD", 1.2500);
        
        when(frankfurterClient.getHistoricalRate("2023-01-15", "EUR", "USD"))
                .thenReturn(eurResponse);
        when(frankfurterClient.getHistoricalRate("2023-01-15", "GBP", "USD"))
                .thenReturn(gbpResponse);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        // 100.00 + (50.00 * 1.0850) + (25.00 * 1.2500) = 100.00 + 54.25 + 31.25 = 185.50
        assertEquals(new BigDecimal("185.50"), result);
    }

    @Test
    void calculateTotal_emptyLinesList_returnsZero() {
        // Given
        testPayload.lines = Arrays.asList();

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void calculateTotal_verySmallAmounts_handlesCorrectly() {
        // Given
        InvoiceLine line1 = createLine("Micro 1", "USD", "0.001");
        InvoiceLine line2 = createLine("Micro 2", "USD", "0.002");
        testPayload.lines = Arrays.asList(line1, line2);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("0.00"), result); // Rounds to 0.00
    }

    @Test
    void calculateTotal_largeAmounts_handlesCorrectly() {
        // Given
        InvoiceLine line = createLine("Large Item", "USD", "999999999.99");
        testPayload.lines = Arrays.asList(line);

        // When
        BigDecimal result = invoiceService.calculateTotal(testPayload);

        // Then
        assertEquals(new BigDecimal("999999999.99"), result);
    }

    private InvoiceLine createLine(String description, String currency, String amount) {
        InvoiceLine line = new InvoiceLine();
        line.description = description;
        line.currency = currency;
        line.amount = new BigDecimal(amount);
        return line;
    }

    private FrankfurterResponse createMockResponse(String targetCurrency, double rate) {
        FrankfurterResponse response = new FrankfurterResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put(targetCurrency, rate);
        response.rates = rates;
        return response;
    }
}
