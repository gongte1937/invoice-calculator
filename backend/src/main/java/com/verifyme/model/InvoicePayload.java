package com.verifyme.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @JsonFormat(pattern = "yyyy-MM-dd")
  public LocalDate date;

  @NotNull
  @Size(min = 1)
  public List<@Valid InvoiceLine> lines;
}
