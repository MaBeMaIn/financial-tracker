package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.DomainException;
import se.financial_tracker.user.domain.UserId;

/** An account belongs to exactly one user, and that user has to exist (FR-3.4). */
public final class UnknownOwner extends DomainException {

    public UnknownOwner(UserId owner) {
        super("no user is registered with the id " + owner + ", so no account can belong to them");
    }
}
