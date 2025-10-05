package com.verifyme.invoice.service;

import com.verifyme.common.client.FrankfurterClient;
import com.verifyme.common.client.FrankfurterResponse;
import com.verifyme.common.utils.Roundings;
import com.verifyme.invoice.config.InvoiceConfig;
import com.verifyme.invoice.model.InvoiceLine;
import com.verifyme.invoice.dto.InvoicePayload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InvoiceService {

  private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

  @Inject
  @RestClient
  FrankfurterClient frankfurter;

  @Inject
  InvoiceConfig config;

  private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * calculate the total price of the invoice (return the target currency amount, keep 2 decimal places)
   */
  public BigDecimal calculateTotal(InvoicePayload payload) {
    final String base = payload.currency.trim().toUpperCase(); // target currency
    final String date = DF.format(payload.date);                // historical exchange rate date

    logger.debug("Starting invoice calculation - base currency: {}, date: {}, lines: {}", 
                base, date, payload.lines.size());

    BigDecimal total = BigDecimal.ZERO;

    for (InvoiceLine line : payload.lines) {
      final String from = line.currency.trim().toUpperCase();

      // same currency: directly add the amount with configured decimal places
      if (from.equals(base)) {
        logger.debug("Same currency conversion: {} {} -> {}", line.amount, from, base);
        total = total.add(Roundings.money(line.amount, config.decimal().moneyScale()));
        continue;
      }

      // get historical exchange rate
      logger.debug("Fetching exchange rate: {} -> {} for date {}", from, base, date);
      FrankfurterResponse resp;
      try {
        resp = frankfurter.getHistoricalRate(date, from, base);
      } catch (Exception e) {
        logger.error("Failed to fetch exchange rate: {} -> {} for date {}: {}", 
                    from, base, date, e.getMessage());
        throw new NotFoundException(config.error().exchangeRateFetchErrorTemplate()
            .formatted(from, base, date));
      }

      if (resp == null || resp.rates == null || !resp.rates.containsKey(base)) {
        logger.error("Exchange rate not found: {} -> {} for date {}", from, base, date);
        throw new NotFoundException(config.error().exchangeRateNotFoundTemplate()
            .formatted(from, base, date));
      }

      // exchange rate with configured decimal places
      BigDecimal rate = Roundings.rate(BigDecimal.valueOf(resp.rates.get(base)), config.decimal().rateScale());
      if (rate.compareTo(BigDecimal.ZERO) <= 0) {
        logger.error("Invalid exchange rate: {} for {} -> {} on {}", rate, from, base, date);
        throw new BadRequestException(config.error().invalidRateTemplate()
            .formatted(from, base, date));
      }

      // line total = amount * exchange rate with configured decimal places
      BigDecimal lineTotal = Roundings.money(line.amount.multiply(rate), config.decimal().moneyScale());
      logger.debug("Currency conversion: {} {} * {} = {} {}", 
                  line.amount, from, rate, lineTotal, base);
      total = total.add(lineTotal);
    }

    // total with configured decimal places
    BigDecimal finalTotal = Roundings.money(total, config.decimal().moneyScale());
    logger.info("Invoice calculation completed - total: {} {}", finalTotal, base);
    return finalTotal;
  }
}
