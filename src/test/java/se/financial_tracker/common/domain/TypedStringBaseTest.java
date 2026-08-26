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
        assertThat(SampleId.of("abc")).isEqualTo(SampleId.of("abc")).hasSameHashCodeAs(SampleId.of("abc"));
    }

    @Test
    void different_types_with_the_same_value_are_not_equal() {
        assertThat(SampleId.of("abc")).isNotEqualTo(OtherId.of("abc"));
        assertThat(OtherId.of("abc")).isNotEqualTo(SampleId.of("abc"));
    }

    @Test
    void different_types_do_not_collide_in_a_set() {
        var ids = Set.of(SampleId.of("abc"), OtherId.of("abc"));

        assertThat(ids).hasSize(2);
    }

    @Test
    void value_is_trimmed() {
        assertThat(SampleId.of("  abc  ").value()).isEqualTo("abc");
    }

    @Test
    void subtypes_may_normalize_in_their_factory() {
        assertThat(LowerCased.of(" ABC ").value()).isEqualTo("abc");
        assertThat(LowerCased.of("ABC")).isEqualTo(LowerCased.of("abc"));
    }

    @Test
    void null_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(null));
    }

    @Test
    void blank_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of("   "));
    }

    @Test
    void the_message_names_the_type() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SampleId.of(""))
                .withMessageContaining("SampleId");
    }

    @Test
    void comparison_is_by_value() {
        assertThat(SampleId.of("a")).isLessThan(SampleId.of("b"));
        assertThat(SampleId.of("a")).isEqualByComparingTo(SampleId.of("a"));
    }

    @Test
    void to_string_is_the_value() {
        assertThat(SampleId.of("abc")).hasToString("abc");
    }
}
