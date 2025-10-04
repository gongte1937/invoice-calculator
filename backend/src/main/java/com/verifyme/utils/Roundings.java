package com.verifyme.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Roundings {
  private Roundings() {}

  /** keep specified decimal places, round to nearest */
  public static BigDecimal rate(BigDecimal v, int scale) {
    return v.setScale(scale, RoundingMode.HALF_UP);
  }

  /** keep specified decimal places for money amounts, round to nearest */
  public static BigDecimal money(BigDecimal v, int scale) {
    return v.setScale(scale, RoundingMode.HALF_UP);
  }

  /** keep 4 decimal places, round to nearest (backward compatibility) */
  @Deprecated
  public static BigDecimal rate4(BigDecimal v) {
    return rate(v, 4);
  }

  /** keep 2 decimal places, round to nearest (backward compatibility) */
  @Deprecated
  public static BigDecimal money2(BigDecimal v) {
    return money(v, 2);
  }
}
