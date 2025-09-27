package com.verifyme.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Roundings {
  private Roundings() {}

  /** keep 4 decimal places, round to nearest */
  public static BigDecimal rate4(BigDecimal v) {
    return v.setScale(4, RoundingMode.HALF_UP);
  }

  /** keep 2 decimal places, round to nearest */
  public static BigDecimal money2(BigDecimal v) {
    return v.setScale(2, RoundingMode.HALF_UP);
  }
}
