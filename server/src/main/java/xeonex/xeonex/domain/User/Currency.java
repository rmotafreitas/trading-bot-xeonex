package xeonex.xeonex.domain.User;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


@Getter
public enum Currency {
    TetherDollar("USDT"),
    Euro("EUR");

    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return currency + " ";
    }


    public static Currency fromString(String text) {
        for (Currency currency : Currency.values()) {
            if (currency.currency.equalsIgnoreCase(text)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
