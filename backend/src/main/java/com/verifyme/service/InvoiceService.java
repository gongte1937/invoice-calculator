package com.verifyme.service;

import com.verifyme.client.FrankfurterClient;
import com.verifyme.client.FrankfurterResponse;
import com.verifyme.model.InvoiceLine;
import com.verifyme.model.InvoicePayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class InvoiceService {

  @Inject
  FrankfurterClient frankfurter;

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

      // same currency: directly add the amount to 2 decimal places
      if (from.equals(base)) {
        total = total.add(Roundings.money2(line.amount));
        continue;
      }

      // get historical exchange rate
      FrankfurterResponse resp;
      try {
        resp = frankfurter.getHistoricalRate(date, from, base);
      } catch (Exception e) {
        throw new NotFoundException("cannot fetch exchange rate for %s->%s on %s"
            .formatted(from, base, date));
      }

      if (resp == null || resp.rates == null || !resp.rates.containsKey(base)) {
        throw new NotFoundException("exchange rate not found for %s->%s on %s"
            .formatted(from, base, date));
      }

      // exchange rate 4 decimal places
      BigDecimal rate = Roundings.rate4(BigDecimal.valueOf(resp.rates.get(base)));
      if (rate.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException("invalid rate for %s->%s on %s"
            .formatted(from, base, date));
      }

      // line total = amount * exchange rate → amount 2 decimal places
      BigDecimal lineTotal = Roundings.money2(line.amount.multiply(rate));
      total = total.add(lineTotal);
    }

    // total 2 decimal places
    return Roundings.money2(total);
  }
}
