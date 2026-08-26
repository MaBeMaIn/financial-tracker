package se.financial_tracker.common.domain;

/**
 * A domain primitive backed by a single string, so that a method taking
 * {@code (UserId, GroupId)} cannot be called with the arguments swapped.
 */
public interface TypedString {
    String value();
}
