package se.financial_tracker.common.domain;

import java.util.Objects;

/**
 * Base class for strongly typed string domain primitives.
 *
 * <p>The type parameter is the concrete subtype (a self type), which gives every subtype a
 * {@link Comparable} against its own kind only:
 *
 * <pre>{@code
 * public final class UserId extends TypedStringBase<UserId> {
 *
 *     private UserId(String value) {
 *         super(value);
 *     }
 *
 *     public static UserId of(String value) {
 *         return new UserId(value);
 *     }
 * }
 * }</pre>
 *
 * <p>Two instances are equal only when they are of the same concrete class and hold the
 * same value, so a {@code UserId} never equals a {@code GroupId} carrying the same text.
 *
 * <p>There is deliberately no {@code static of(String)} here: static methods are not
 * polymorphic in Java, so a factory on the base class cannot know which subtype to build.
 * Each subtype declares its own factory, as above.
 *
 * <p>The value is trimmed and must not be blank. Any further normalization or validation
 * belongs in the subtype's factory, which keeps it out of the constructor:
 *
 * <pre>{@code
 * public final class EmailAddress extends TypedStringBase<EmailAddress> {
 *
 *     private static final Pattern PATTERN = Pattern.compile(".+@.+\\..+");
 *
 *     private EmailAddress(String value) {
 *         super(value);
 *     }
 *
 *     public static EmailAddress of(String raw) {
 *         // normalize before construction, validate after
 *         EmailAddress email = new EmailAddress(raw == null ? null : raw.toLowerCase());
 *         if (!PATTERN.matcher(email.value()).matches()) {
 *             throw new IllegalArgumentException("not a valid email address");
 *         }
 *         return email;
 *     }
 * }
 * }</pre>
 */
public abstract class TypedStringBase<T extends TypedStringBase<T>> implements TypedString, Comparable<T> {

    private final String value;

    /**
     * @param value the raw value; trimmed before it is stored
     * @throws IllegalArgumentException if the value is null or blank
     */
    protected TypedStringBase(String value) {
        String trimmed = value == null ? null : value.trim();
        if (trimmed == null || trimmed.isEmpty()) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " must not be null or blank");
        }
        this.value = trimmed;
    }

    @Override
    public final String value() {
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
        return value.equals(((TypedStringBase<?>) other).value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), value);
    }

    @Override
    public final int compareTo(T other) {
        return value.compareTo(other.value());
    }

    @Override
    public String toString() {
        return value;
    }
}
