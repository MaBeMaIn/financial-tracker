package se.financial_tracker.account.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * How much money a transaction moved. Always strictly positive: the transaction's {@link
 * Direction} carries the sign (invariant 3). v1 is SEK only, so there is no currency yet.
 *
 * <p>Held at two decimals, so {@code 10}, {@code 10.0} and {@code 10.00} are the same amount, and
 * a value with more decimals than that is refused rather than silently rounded.
 */
public final class Amount implements Comparable<Amount> {

    private static final int SCALE = 2;

    private final BigDecimal value;

    private Amount(BigDecimal value) {
        this.value = value;
    }

    public static Amount of(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be strictly positive, but was " + value.toPlainString());
        }
        try {
            return new Amount(value.setScale(SCALE, RoundingMode.UNNECESSARY));
        } catch (ArithmeticException tooPrecise) {
            throw new IllegalArgumentException(
                    "Amount must have at most " + SCALE + " decimals, but was " + value.toPlainString());
        }
    }

    public static Amount of(String value) {
        return of(new BigDecimal(value));
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Amount amount && value.equals(amount.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Amount other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
