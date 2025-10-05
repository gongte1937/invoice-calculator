package com.verifyme.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verifyme.common.client.FrankfurterResponse;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrankfurterResponseUnitTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void frankfurterResponse_canBeInstantiated() {
        // When
        FrankfurterResponse response = new FrankfurterResponse();
        
        // Then
        assertNotNull(response);
        assertNull(response.rates);
        assertNull(response.base);
        assertNull(response.date);
        assertNull(response.amount);
    }

    @Test
    void frankfurterResponse_canSetAllFields() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0850);
        rates.put("GBP", 0.8750);
        
        // When
        response.rates = rates;
        response.base = "EUR";
        response.date = "2023-01-15";
        response.amount = 1.0;
        
        // Then
        assertEquals(rates, response.rates);
        assertEquals("EUR", response.base);
        assertEquals("2023-01-15", response.date);
        assertEquals(1.0, response.amount);
    }

    @Test
    void frankfurterResponse_ratesCanBeEmpty() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        
        // When
        response.rates = new HashMap<>();
        
        // Then
        assertNotNull(response.rates);
        assertTrue(response.rates.isEmpty());
    }

    @Test
    void frankfurterResponse_ratesCanContainMultipleCurrencies() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0850);
        rates.put("GBP", 0.8750);
        rates.put("JPY", 140.25);
        rates.put("CAD", 1.4520);
        
        // When
        response.rates = rates;
        
        // Then
        assertEquals(4, response.rates.size());
        assertEquals(1.0850, response.rates.get("USD"));
        assertEquals(0.8750, response.rates.get("GBP"));
        assertEquals(140.25, response.rates.get("JPY"));
        assertEquals(1.4520, response.rates.get("CAD"));
    }

    @Test
    void frankfurterResponse_supportsJsonDeserialization() throws Exception {
        // Given
        String json = """
            {
                "amount": 1.0,
                "base": "EUR",
                "date": "2023-01-15",
                "rates": {
                    "USD": 1.0850,
                    "GBP": 0.8750
                }
            }
            """;
        
        // When
        FrankfurterResponse response = objectMapper.readValue(json, FrankfurterResponse.class);
        
        // Then
        assertEquals(1.0, response.amount);
        assertEquals("EUR", response.base);
        assertEquals("2023-01-15", response.date);
        assertEquals(2, response.rates.size());
        assertEquals(1.0850, response.rates.get("USD"));
        assertEquals(0.8750, response.rates.get("GBP"));
    }

    @Test
    void frankfurterResponse_ignoresUnknownJsonFields() throws Exception {
        // Given - JSON with extra unknown fields
        String json = """
            {
                "amount": 1.0,
                "base": "EUR",
                "date": "2023-01-15",
                "rates": {
                    "USD": 1.0850
                },
                "unknownField": "should be ignored",
                "anotherUnknownField": 123
            }
            """;
        
        // When
        FrankfurterResponse response = objectMapper.readValue(json, FrankfurterResponse.class);
        
        // Then - Should deserialize successfully, ignoring unknown fields
        assertEquals(1.0, response.amount);
        assertEquals("EUR", response.base);
        assertEquals("2023-01-15", response.date);
        assertEquals(1, response.rates.size());
        assertEquals(1.0850, response.rates.get("USD"));
    }

    @Test
    void frankfurterResponse_handlesNullRatesInJson() throws Exception {
        // Given
        String json = """
            {
                "amount": 1.0,
                "base": "EUR",
                "date": "2023-01-15",
                "rates": null
            }
            """;
        
        // When
        FrankfurterResponse response = objectMapper.readValue(json, FrankfurterResponse.class);
        
        // Then
        assertEquals(1.0, response.amount);
        assertEquals("EUR", response.base);
        assertEquals("2023-01-15", response.date);
        assertNull(response.rates);
    }

    @Test
    void frankfurterResponse_handlesEmptyRatesInJson() throws Exception {
        // Given
        String json = """
            {
                "amount": 1.0,
                "base": "EUR",
                "date": "2023-01-15",
                "rates": {}
            }
            """;
        
        // When
        FrankfurterResponse response = objectMapper.readValue(json, FrankfurterResponse.class);
        
        // Then
        assertEquals(1.0, response.amount);
        assertEquals("EUR", response.base);
        assertEquals("2023-01-15", response.date);
        assertNotNull(response.rates);
        assertTrue(response.rates.isEmpty());
    }

    @Test
    void frankfurterResponse_handlesPartialJsonData() throws Exception {
        // Given - JSON with only some fields
        String json = """
            {
                "base": "EUR",
                "rates": {
                    "USD": 1.0850
                }
            }
            """;
        
        // When
        FrankfurterResponse response = objectMapper.readValue(json, FrankfurterResponse.class);
        
        // Then
        assertNull(response.amount);
        assertEquals("EUR", response.base);
        assertNull(response.date);
        assertEquals(1, response.rates.size());
        assertEquals(1.0850, response.rates.get("USD"));
    }

    @Test
    void frankfurterResponse_ratesSupportsVariousExchangeRates() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        Map<String, Double> rates = new HashMap<>();
        
        // When - Add various types of exchange rates
        rates.put("USD", 1.0850);      // > 1
        rates.put("GBP", 0.8750);      // < 1
        rates.put("JPY", 140.25);      // Large number
        rates.put("KRW", 1234.56);     // Very large number
        rates.put("BTC", 0.000023);    // Very small number
        response.rates = rates;
        
        // Then
        assertEquals(5, response.rates.size());
        assertTrue(response.rates.get("USD") > 1.0);
        assertTrue(response.rates.get("GBP") < 1.0);
        assertTrue(response.rates.get("JPY") > 100.0);
        assertTrue(response.rates.get("KRW") > 1000.0);
        assertTrue(response.rates.get("BTC") < 0.001);
    }

    @Test
    void frankfurterResponse_dateSupportsVariousFormats() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        String[] validDates = {
            "2023-01-15",
            "2023-12-31",
            "2020-02-29", // Leap year
            "2023-06-01"
        };
        
        for (String date : validDates) {
            // When
            response.date = date;
            
            // Then
            assertEquals(date, response.date);
        }
    }

    @Test
    void frankfurterResponse_amountSupportsVariousValues() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        Double[] validAmounts = {
            1.0,
            100.0,
            0.01,
            999999.99,
            1.123456789
        };
        
        for (Double amount : validAmounts) {
            // When
            response.amount = amount;
            
            // Then
            assertEquals(amount, response.amount);
        }
    }

    @Test
    void frankfurterResponse_baseSupportsVariousCurrencies() {
        // Given
        FrankfurterResponse response = new FrankfurterResponse();
        String[] validCurrencies = {
            "EUR", "USD", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY"
        };
        
        for (String currency : validCurrencies) {
            // When
            response.base = currency;
            
            // Then
            assertEquals(currency, response.base);
        }
    }
}
