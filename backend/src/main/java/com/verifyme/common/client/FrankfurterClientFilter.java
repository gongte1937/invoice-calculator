package com.verifyme.common.client;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Provider
public class FrankfurterClientFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(FrankfurterClientFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info("Calling Frankfurter API: {} {}", 
                   requestContext.getMethod(), 
                   requestContext.getUri());
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        logger.info("Frankfurter API response: {} - Status: {}", 
                   requestContext.getUri(), 
                   responseContext.getStatus());
        
        if (responseContext.getStatus() >= 400) {
            logger.error("Frankfurter API error: {} returned status {}", 
                        requestContext.getUri(), 
                        responseContext.getStatus());
        }
    }
}
