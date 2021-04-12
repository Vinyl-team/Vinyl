package com.vinylteam.vinyl.entity;

import java.util.Optional;

public enum Currency {
    UAH("₴"), EUR("€"), USD("$"), GBP("£");

    Currency(String currencySymbol) {
    }

    static Optional<Currency> getCurrency(String currencyDescription) {
        if (currencyDescription.equals(" грн.") || currencyDescription.equals("₴")) {
         return Optional.of(UAH);
        }
        if (currencyDescription.equals("EUR") || currencyDescription.equals("€")) {
            return Optional.of(EUR);
        }
        if (currencyDescription.equals("USD") || currencyDescription.equals("$")) {
            return Optional.of(USD);
        }
        if (currencyDescription.equals("GBP") || currencyDescription.equals("£")){
            return Optional.of(GBP);
        }
        return Optional.empty();
    }
}
