package se.financial_tracker.common.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TypedStringBaseTest {

    private static final class SampleId extends TypedStringBase<SampleId> {

        private SampleId(String value) {
            super(value);
        }

        static SampleId of(String value) {
            return new SampleId(value);
        }
    }

    private static final class OtherId extends TypedStringBase<OtherId> {

        private OtherId(String value) {
            super(value);
        }

        static OtherId of(String value) {
            return new OtherId(value);
        }
    }

    private static final class LowerCased extends TypedStringBase<LowerCased> {

        private LowerCased(String value) {
            super(value);
        }

        static LowerCased of(String raw) {
            return new LowerCased(raw == null ? null : raw.toLowerCase());
        }
    }

    @Test
    void same_type_and_value_are_equal() {
        // Arrange
        SampleId id = SampleId.of("abc");

        // Act
        SampleId sameValue = SampleId.of("abc");

        // Assert
        assertThat(id).isEqualTo(sameValue).hasSameHashCodeAs(sameValue);
    }

    @Test
    void different_types_with_the_same_value_are_not_equal() {
        // Arrange
        SampleId sampleId = SampleId.of("abc");
        OtherId otherId = OtherId.of("abc");

        // Act + Assert
        assertThat(sampleId).isNotEqualTo(otherId);
        assertThat(otherId).isNotEqualTo(sampleId);
    }

    @Test
    void different_types_do_not_collide_in_a_set() {
        // Arrange
        SampleId sampleId = SampleId.of("abc");
        OtherId otherId = OtherId.of("abc");

        // Act
        var ids = Set.of(sampleId, otherId);

        // Assert
        assertThat(ids).hasSize(2);
    }

    @Test
    void value_is_trimmed() {
        // Arrange
        String padded = "  abc  ";

        // Act
        SampleId id = SampleId.of(padded);

        // Assert
        assertThat(id.value()).isEqualTo("abc");
    }

    @Test
    void subtypes_may_normalize_in_their_factory() {
        // Arrange
        String upperCased = " ABC ";

        // Act
        LowerCased normalized = LowerCased.of(upperCased);

        // Assert
        assertThat(normalized.value()).isEqualTo("abc");
        assertThat(normalized).isEqualTo(LowerCased.of("abc"));
    }

    @Test
    void null_is_rejected() {
        // Arrange
        String value = null;

        // Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(value));
    }

    @Test
    void blank_is_rejected() {
        // Arrange
        String blank = "   ";

        // Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(blank));
    }

    @Test
    void the_message_names_the_type() {
        // Arrange
        String empty = "";

        // Act + Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SampleId.of(empty))
                .withMessageContaining("SampleId");
    }

    @Test
    void comparison_is_by_value() {
        // Arrange
        SampleId a = SampleId.of("a");
        SampleId b = SampleId.of("b");

        // Act + Assert
        assertThat(a).isLessThan(b);
        assertThat(a).isEqualByComparingTo(SampleId.of("a"));
    }

    @Test
    void to_string_is_the_value() {
        // Arrange
        SampleId id = SampleId.of("abc");

        // Act
        String text = id.toString();

        // Assert
        assertThat(text).isEqualTo("abc");
    }
}
