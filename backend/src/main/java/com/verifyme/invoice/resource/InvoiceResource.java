    package com.verifyme.invoice.resource;

import com.verifyme.invoice.dto.InvoiceRequest;
import com.verifyme.invoice.service.InvoiceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/invoice")
public class InvoiceResource {

  @Inject
  InvoiceService invoiceService;

  @POST
  @Path("/total")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response total(@Valid InvoiceRequest body) {
    var result = invoiceService.calculateTotal(body.invoice);
    return Response.ok(result.toPlainString(), MediaType.TEXT_PLAIN).build();
  }
}