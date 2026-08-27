package se.financial_tracker.common.domain;

import java.util.UUID;

/**
 * A domain primitive backed by a UUID, so that a method taking {@code (UserId, GroupId)}
 * cannot be called with the arguments swapped.
 */
public interface TypedUuid {

    /**
     * The wrapped identifier.
     *
     * @return the identifier, never null
     */
    UUID value();
}
