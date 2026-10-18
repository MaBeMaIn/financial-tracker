package se.financial_tracker.common.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Uuid7Test {

    @Test
    void a_generated_id_carries_version_7_and_the_rfc_variant() {
        // Act
        UUID id = Uuid7.newUuid();

        // Assert
        assertThat(id.version()).isEqualTo(7);
        assertThat(id.variant()).isEqualTo(2);
        assertThat(Uuid7.isUuid7(id)).isTrue();
    }

    @Test
    void ids_minted_in_order_compare_in_order() {
        // Arrange
        List<UUID> ids =
                IntStream.range(0, 1_000).mapToObj(i -> Uuid7.newUuid()).toList();

        // Act
        List<UUID> sorted = new ArrayList<>(ids);
        sorted.sort(UUID::compareTo);

        // Assert
        assertThat(sorted).containsExactlyElementsOf(ids);
    }

    @Test
    void ids_are_distinct() {
        // Act
        Set<UUID> ids = new HashSet<>();
        IntStream.range(0, 10_000).forEach(i -> ids.add(Uuid7.newUuid()));

        // Assert
        assertThat(ids).hasSize(10_000);
    }

    @Test
    void the_timestamp_is_the_moment_the_id_was_made() {
        // Arrange
        Instant before = Instant.now();

        // Act
        UUID id = Uuid7.newUuid();

        // Assert
        // A second of slack, not a millisecond: when more than 4096 ids are made inside one
        // millisecond the generator borrows the next one, so a timestamp may sit slightly ahead
        // of the clock. The point of the assertion is that it is now, not 1970.
        assertThat(Uuid7.timestampOf(id))
                .isBetween(before.minusSeconds(1), Instant.now().plusSeconds(1));
    }

    @Test
    void a_version_4_uuid_is_not_a_version_7_one() {
        // Act + Assert
        assertThat(Uuid7.isUuid7(UUID.randomUUID())).isFalse();
        assertThat(Uuid7.isUuid7(null)).isFalse();
    }

    @Test
    void asking_for_the_timestamp_of_another_version_is_rejected() {
        // Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Uuid7.timestampOf(UUID.randomUUID()));
    }
}
