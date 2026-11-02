package se.financial_tracker.account.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @Test
    void an_amount_is_held_at_two_decimals() {
        // Act
        Amount amount = Amount.of("10");

        // Assert
        assertThat(amount.value()).isEqualTo(new BigDecimal("10.00"));
        assertThat(amount).isEqualTo(Amount.of("10.000"));
    }

    @Test
    void zero_is_rejected() {
        // Act + Assert
        assertThatThrownBy(() -> Amount.of("0")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void a_negative_amount_is_rejected() {
        // Act + Assert
        assertThatThrownBy(() -> Amount.of("-5.00")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void more_than_two_decimals_is_rejected_rather_than_rounded() {
        // Act + Assert
        assertThatThrownBy(() -> Amount.of("10.005"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at most 2 decimals");
    }

    @Test
    void null_is_rejected() {
        // Act + Assert
        assertThatThrownBy(() -> Amount.of((BigDecimal) null)).isInstanceOf(IllegalArgumentException.class);
    }
}
