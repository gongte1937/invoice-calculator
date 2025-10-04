export enum Currency {
  NZD = "NZD",
  USD = "USD",
  AUD = "AUD",
  EUR = "EUR",
  GBP = "GBP",
  JPY = "JPY",
}

export const CURRENCIES = Object.values(Currency);

export const CURRENCIES_ENUM = CURRENCIES as [Currency, ...Currency[]];

export const DEFAULT_BASE_CURRENCY = Currency.NZD;
export const DEFAULT_LINE_CURRENCY = Currency.USD;
