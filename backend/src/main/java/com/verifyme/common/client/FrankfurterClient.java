package com.verifyme.common.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@Produces(MediaType.APPLICATION_JSON)
public interface FrankfurterClient {
  @GET
  @Path("{date}")
  FrankfurterResponse getHistoricalRate(
      @PathParam("date") String date,
      @QueryParam("from") String from,
      @QueryParam("to") String to
  );
}
