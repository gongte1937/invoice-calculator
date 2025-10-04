package com.verifyme.service;

import com.verifyme.client.FrankfurterClient;
import com.verifyme.client.FrankfurterResponse;
import com.verifyme.config.InvoiceConfig;
import com.verifyme.model.InvoiceLine;
import com.verifyme.model.InvoicePayload;
import com.verifyme.utils.Roundings;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class InvoiceService {

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

    BigDecimal total = BigDecimal.ZERO;

    for (InvoiceLine line : payload.lines) {
      final String from = line.currency.trim().toUpperCase();

      // same currency: directly add the amount with configured decimal places
      if (from.equals(base)) {
        total = total.add(Roundings.money(line.amount, config.decimal().moneyScale()));
        continue;
      }

      // get historical exchange rate
      FrankfurterResponse resp;
      try {
        resp = frankfurter.getHistoricalRate(date, from, base);
      } catch (Exception e) {
        throw new NotFoundException(config.error().exchangeRateFetchErrorTemplate()
            .formatted(from, base, date));
      }

      if (resp == null || resp.rates == null || !resp.rates.containsKey(base)) {
        throw new NotFoundException(config.error().exchangeRateNotFoundTemplate()
            .formatted(from, base, date));
      }

      // exchange rate with configured decimal places
      BigDecimal rate = Roundings.rate(BigDecimal.valueOf(resp.rates.get(base)), config.decimal().rateScale());
      if (rate.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException(config.error().invalidRateTemplate()
            .formatted(from, base, date));
      }

      // line total = amount * exchange rate with configured decimal places
      BigDecimal lineTotal = Roundings.money(line.amount.multiply(rate), config.decimal().moneyScale());
      total = total.add(lineTotal);
    }

    // total with configured decimal places
    return Roundings.money(total, config.decimal().moneyScale());
  }
}
