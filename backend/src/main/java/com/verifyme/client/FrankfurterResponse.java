package com.verifyme.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FrankfurterResponse {
  public Map<String, Double> rates; 
  public String base;               
  public String date;               
  public Double amount;             
}
