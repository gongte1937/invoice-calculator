package com.verifyme.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

import com.verifyme.invoice.model.InvoiceLine;

public class InvoicePayload {
  @NotBlank
  public String currency;

  @NotNull
  public LocalDate date;

  @NotNull
  @Size(min = 1, message = "At least one invoice line is required")
  public List<@Valid InvoiceLine> lines;
}
