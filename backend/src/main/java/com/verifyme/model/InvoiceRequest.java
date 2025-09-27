package com.verifyme.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class InvoiceRequest {
  @NotNull @Valid
  public InvoicePayload invoice;
}
