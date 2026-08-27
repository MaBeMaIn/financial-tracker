package se.financial_tracker.common.domain;

/**
 * A domain primitive backed by a single string, so that a method taking
 * {@code (Username, GroupName)} cannot be called with the arguments swapped.
 *
 * <p>See {@link TypedUuid} for primitives backed by a UUID.
 */
public interface TypedString {

    /**
     * The wrapped value.
     *
     * @return the value, never null or blank
     */
    String value();
}
