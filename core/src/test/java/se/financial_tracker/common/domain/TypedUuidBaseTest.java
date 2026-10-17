package se.financial_tracker.common.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TypedUuidBaseTest {

    // Version 7 and the RFC 9562 variant, which is all TypedUuidBase accepts. A sorts before B.
    private static final UUID A = UUID.fromString("01900000-0000-7000-8000-00000000000a");
    private static final UUID B = UUID.fromString("01900000-0000-7000-8000-00000000000b");
    private static final UUID VERSION_4 = UUID.fromString("00000000-0000-4000-8000-00000000000a");

    private static final class SampleId extends TypedUuidBase<SampleId> {

        private SampleId(UUID value) {
            super(value);
        }

        static SampleId of(UUID value) {
            return new SampleId(value);
        }

        static SampleId newId() {
            return new SampleId(Uuid7.newUuid());
        }
    }

    private static final class OtherId extends TypedUuidBase<OtherId> {

        private OtherId(UUID value) {
            super(value);
        }

        static OtherId of(UUID value) {
            return new OtherId(value);
        }
    }

    @Test
    void same_type_and_value_are_equal() {
        assertThat(SampleId.of(A)).isEqualTo(SampleId.of(A)).hasSameHashCodeAs(SampleId.of(A));
    }

    @Test
    void different_types_with_the_same_value_are_not_equal() {
        assertThat(SampleId.of(A)).isNotEqualTo(OtherId.of(A));
        assertThat(OtherId.of(A)).isNotEqualTo(SampleId.of(A));
    }

    @Test
    void different_types_do_not_collide_in_a_set() {
        var ids = Set.of(SampleId.of(A), OtherId.of(A));

        assertThat(ids).hasSize(2);
    }

    @Test
    void the_value_is_kept() {
        assertThat(SampleId.of(A).value()).isEqualTo(A);
    }

    @Test
    void a_typed_uuid_never_equals_its_raw_value() {
        assertThat(SampleId.of(A)).isNotEqualTo(A);
    }

    @Test
    void generated_ids_are_distinct() {
        assertThat(SampleId.newId()).isNotEqualTo(SampleId.newId());
    }

    @Test
    void a_version_4_uuid_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SampleId.of(VERSION_4))
                .withMessageContaining("SampleId")
                .withMessageContaining("version 7");
    }

    @Test
    void a_random_uuid_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(UUID.randomUUID()));
    }

    @Test
    void the_nil_uuid_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(new UUID(0L, 0L)));
    }

    @Test
    void a_generated_id_is_accepted() {
        assertThat(SampleId.newId().value().version()).isEqualTo(7);
    }

    @Test
    void null_is_rejected() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> SampleId.of(null));
    }

    @Test
    void the_message_names_the_type() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SampleId.of(null))
                .withMessageContaining("SampleId");
    }

    @Test
    void comparison_is_by_value() {
        assertThat(SampleId.of(A)).isLessThan(SampleId.of(B));
        assertThat(SampleId.of(A)).isEqualByComparingTo(SampleId.of(A));
    }

    @Test
    void ids_sort_by_value() {
        var sorted = new TreeSet<>(Set.of(SampleId.of(B), SampleId.of(A)));

        assertThat(sorted).containsExactly(SampleId.of(A), SampleId.of(B));
    }

    @Test
    void to_string_is_the_uuid() {
        assertThat(SampleId.of(A)).hasToString(A.toString());
    }
}
