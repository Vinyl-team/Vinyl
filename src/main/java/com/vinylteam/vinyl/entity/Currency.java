package com.vinylteam.vinyl.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Logger logger = LoggerFactory.getLogger(Currency.class);
        logger.debug("getCurrency started with {currencyDescription':{}}", currencyDescription);
        Currency resultingCurrency = null;
        if (currencyDescription.equals("грн") || currencyDescription.equals("грн.") || currencyDescription.equals("₴")) {
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
        logger.debug("Resulting optional with currency is {'Optional.ofNullable(resultingCurrency)':{}}",
                Optional.ofNullable(resultingCurrency));
        return Optional.ofNullable(resultingCurrency);
    }

}
