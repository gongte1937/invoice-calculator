package com.verifyme.invoice.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceLine {
  @NotBlank
  public String description;

  @NotBlank
  public String currency;

  @NotNull
  @DecimalMin("0.0")
  public BigDecimal amount;
}
