package com.verifyme.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "invoice")
public interface InvoiceConfig {

    /**
     * Decimal precision configuration
     */
    DecimalConfig decimal();

    /**
     * Error handling configuration
     */
    ErrorConfig error();

    /**
     * Validation configuration
     */
    ValidationConfig validation();

    interface DecimalConfig {
        /**
         * Number of decimal places for exchange rates
         */
        @WithDefault("4")
        int rateScale();

        /**
         * Number of decimal places for money amounts
         */
        @WithDefault("2")
        int moneyScale();
    }

    interface ErrorConfig {
        /**
         * HTTP status codes for different error types
         */
        @WithDefault("400")
        int badRequestStatus();

        @WithDefault("404")
        int notFoundStatus();

        @WithDefault("500")
        int internalErrorStatus();

        /**
         * Error message templates
         */
        @WithDefault("Error: Invalid request data")
        String validationErrorMessage();

        @WithDefault("Error: internal server error")
        String internalErrorMessage();

        @WithDefault("cannot fetch exchange rate for %s->%s on %s")
        String exchangeRateFetchErrorTemplate();

        @WithDefault("exchange rate not found for %s->%s on %s")
        String exchangeRateNotFoundTemplate();

        @WithDefault("invalid rate for %s->%s on %s")
        String invalidRateTemplate();
    }

    interface ValidationConfig {
        /**
         * Minimum number of invoice lines required
         */
        @WithDefault("1")
        int minInvoiceLines();

        /**
         * Minimum amount value for invoice lines
         */
        @WithDefault("0.0")
        String minAmount();
    }
}
