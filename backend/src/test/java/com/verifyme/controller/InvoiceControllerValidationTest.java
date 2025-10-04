package com.verifyme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class InvoiceControllerValidationTest {

  @Test
  void total_missingInvoice_returnsBadRequestWithPlainTextError() {
    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .when()
        .post("/invoice/total")
        .then()
        .statusCode(400)
        .header("Content-Type", startsWith(MediaType.TEXT_PLAIN))
        .body(equalTo("Error: invoice must not be null"));
  }
}
