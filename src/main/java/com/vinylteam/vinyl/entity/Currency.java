package com.vinylteam.vinyl.entity;

import java.util.Optional;

public enum Currency {
    UAH("₴"),
    USD("$"),
    GBP("£"),
    EUR("€");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Optional<Currency> getCurrency(String currencyDescription) {
        Currency resultingCurrency = null;
        if (currencyDescription.equals("грн") || currencyDescription.equals("₴")) {
            resultingCurrency = UAH;
        }
        if (currencyDescription.equals("GBP") || currencyDescription.equals("£")) {
            resultingCurrency = GBP;
        }
        if (currencyDescription.equals("USD") || currencyDescription.equals("$")) {
            resultingCurrency = USD;
        }
        if (currencyDescription.equals(" EUR") || currencyDescription.equals("EUR") || currencyDescription.equals("€")
                || currencyDescription.equals("&nbsp;€")) {
            resultingCurrency = EUR;
        }
        return Optional.ofNullable(resultingCurrency);
    }
}
