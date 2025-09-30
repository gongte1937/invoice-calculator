package com.verifyme.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class InvoicePayload {
  @NotBlank
  public String currency;

  @NotNull
  public LocalDate date;

  @NotNull
  @Size(min = 1)
  public List<@Valid InvoiceLine> lines;
}
