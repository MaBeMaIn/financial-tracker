package se.financial_tracker.common.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for strongly typed UUID domain primitives — identifiers, in practice.
 *
 * <p>The type parameter is the concrete subtype (a self type), which gives every subtype a
 * {@link Comparable} against its own kind only:
 *
 * <pre>{@code
 * public final class UserId extends TypedUuidBase<UserId> {
 *
 *     private UserId(UUID value) {
 *         super(value);
 *     }
 *
 *     // a brand new identifier, for User.create(...)
 *     public static UserId newId() {
 *         return new UserId(UUID.randomUUID());
 *     }
 *
 *     // an identifier that already exists, from storage or a request
 *     public static UserId of(UUID value) {
 *         return new UserId(value);
 *     }
 *
 *     public static UserId of(String value) {
 *         return new UserId(UUID.fromString(value));
 *     }
 * }
 * }</pre>
 *
 * <p>Two instances are equal only when they are of the same concrete class and hold the
 * same UUID, so a {@code UserId} never equals a {@code GroupId} carrying the same value.
 *
 * <p>There is deliberately no {@code static of(...)} or {@code newId()} here: static
 * methods are not polymorphic in Java, so a factory on the base class cannot know which
 * subtype to build. Each subtype declares its own, as above.
 *
 * <p>See {@link TypedStringBase} for primitives backed by a string.
 *
 * @param <T> the concrete subtype, so that {@code compareTo} and equality are typed
 */
public abstract class TypedUuidBase<T extends TypedUuidBase<T>> implements TypedUuid, Comparable<T> {

    private final UUID value;

    /**
     * Wraps an identifier that must already exist.
     *
     * @param value the identifier this primitive wraps
     * @throws IllegalArgumentException if the value is null
     */
    protected TypedUuidBase(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " must not be null");
        }
        this.value = value;
    }

    @Override
    public final UUID value() {
        return value;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return value.equals(((TypedUuidBase<?>) other).value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), value);
    }

    /**
     * Orders by {@link UUID#compareTo(UUID)}, which compares the two 64-bit halves as
     * signed longs. That is a stable order, but not the same as ordering the textual form —
     * do not rely on it to match {@code ORDER BY id} in the database.
     */
    @Override
    public final int compareTo(T other) {
        return value.compareTo(other.value());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
