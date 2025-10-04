package com.verifyme.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class RoundingsUnitTest {

    @ParameterizedTest
    @CsvSource({
        "1.23456, 1.2346",
        "1.23454, 1.2345",
        "1.23450, 1.2345",
        "1.2345, 1.2345",
        "1.0000, 1.0000",
        "0.0000, 0.0000",
        "0.00001, 0.0000",
        "0.99999, 1.0000",
        "-1.23456, -1.2346",
        "-1.23454, -1.2345"
    })
    void rate4_variousInputs_roundsToFourDecimalPlaces(String input, String expected) {
        // Given
        BigDecimal inputValue = new BigDecimal(input);
        BigDecimal expectedValue = new BigDecimal(expected);

        // When
        BigDecimal result = Roundings.rate4(inputValue);

        // Then
        assertEquals(expectedValue, result);
        assertEquals(4, result.scale());
    }

    @ParameterizedTest
    @CsvSource({
        "123.456, 123.46",
        "123.454, 123.45",
        "123.450, 123.45",
        "123.45, 123.45",
        "100.00, 100.00",
        "0.00, 0.00",
        "0.001, 0.00",
        "0.999, 1.00",
        "-123.456, -123.46",
        "-123.454, -123.45"
    })
    void money2_variousInputs_roundsToTwoDecimalPlaces(String input, String expected) {
        // Given
        BigDecimal inputValue = new BigDecimal(input);
        BigDecimal expectedValue = new BigDecimal(expected);

        // When
        BigDecimal result = Roundings.money2(inputValue);

        // Then
        assertEquals(expectedValue, result);
        assertEquals(2, result.scale());
    }

    @Test
    void rate4_usesHalfUpRounding() {
        // Test the exact boundary case for HALF_UP rounding
        BigDecimal input = new BigDecimal("1.23455"); // Exactly halfway
        BigDecimal result = Roundings.rate4(input);
        
        assertEquals(new BigDecimal("1.2346"), result);
    }

    @Test
    void money2_usesHalfUpRounding() {
        // Test the exact boundary case for HALF_UP rounding
        BigDecimal input = new BigDecimal("123.455"); // Exactly halfway
        BigDecimal result = Roundings.money2(input);
        
        assertEquals(new BigDecimal("123.46"), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "999999999.99999",
        "0.00001",
        "1.00000",
        "123456789.123456789"
    })
    void rate4_preservesSignificantDigits(String input) {
        // Given
        BigDecimal inputValue = new BigDecimal(input);

        // When
        BigDecimal result = Roundings.rate4(inputValue);

        // Then
        assertEquals(4, result.scale());
        assertEquals(RoundingMode.HALF_UP, getRoundingMode(inputValue, result, 4));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "999999999.999",
        "0.001",
        "1.000",
        "123456789.123"
    })
    void money2_preservesSignificantDigits(String input) {
        // Given
        BigDecimal inputValue = new BigDecimal(input);

        // When
        BigDecimal result = Roundings.money2(inputValue);

        // Then
        assertEquals(2, result.scale());
        assertEquals(RoundingMode.HALF_UP, getRoundingMode(inputValue, result, 2));
    }

    @Test
    void rate4_withZero_returnsZeroWithFourDecimals() {
        // Given
        BigDecimal zero = BigDecimal.ZERO;

        // When
        BigDecimal result = Roundings.rate4(zero);

        // Then
        assertEquals(new BigDecimal("0.0000"), result);
        assertEquals(4, result.scale());
    }

    @Test
    void money2_withZero_returnsZeroWithTwoDecimals() {
        // Given
        BigDecimal zero = BigDecimal.ZERO;

        // When
        BigDecimal result = Roundings.money2(zero);

        // Then
        assertEquals(new BigDecimal("0.00"), result);
        assertEquals(2, result.scale());
    }

    @Test
    void rate4_withVeryLargeNumber_handlesCorrectly() {
        // Given
        BigDecimal largeNumber = new BigDecimal("999999999999.123456789");

        // When
        BigDecimal result = Roundings.rate4(largeNumber);

        // Then
        assertEquals(new BigDecimal("999999999999.1235"), result);
        assertEquals(4, result.scale());
    }

    @Test
    void money2_withVeryLargeNumber_handlesCorrectly() {
        // Given
        BigDecimal largeNumber = new BigDecimal("999999999999.123");

        // When
        BigDecimal result = Roundings.money2(largeNumber);

        // Then
        assertEquals(new BigDecimal("999999999999.12"), result);
        assertEquals(2, result.scale());
    }

    @Test
    void rate4_withVerySmallNumber_handlesCorrectly() {
        // Given
        BigDecimal smallNumber = new BigDecimal("0.000000001");

        // When
        BigDecimal result = Roundings.rate4(smallNumber);

        // Then
        assertEquals(new BigDecimal("0.0000"), result);
        assertEquals(4, result.scale());
    }

    @Test
    void money2_withVerySmallNumber_handlesCorrectly() {
        // Given
        BigDecimal smallNumber = new BigDecimal("0.000001");

        // When
        BigDecimal result = Roundings.money2(smallNumber);

        // Then
        assertEquals(new BigDecimal("0.00"), result);
        assertEquals(2, result.scale());
    }

    @Test
    void rate4_immutability_doesNotModifyOriginal() {
        // Given
        BigDecimal original = new BigDecimal("1.23456789");
        BigDecimal originalCopy = new BigDecimal("1.23456789");

        // When
        Roundings.rate4(original);

        // Then
        assertEquals(originalCopy, original); // Original unchanged
    }

    @Test
    void money2_immutability_doesNotModifyOriginal() {
        // Given
        BigDecimal original = new BigDecimal("123.456789");
        BigDecimal originalCopy = new BigDecimal("123.456789");

        // When
        Roundings.money2(original);

        // Then
        assertEquals(originalCopy, original); // Original unchanged
    }

    private RoundingMode getRoundingMode(BigDecimal input, BigDecimal result, int scale) {
        // This is a helper method to verify the rounding mode used
        BigDecimal expected = input.setScale(scale, RoundingMode.HALF_UP);
        return expected.equals(result) ? RoundingMode.HALF_UP : null;
    }
}
