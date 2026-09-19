package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.DomainException;

/** One email address identifies one user, so registering a second time is refused (FR-2.1). */
public final class EmailAlreadyRegistered extends DomainException {

    public EmailAlreadyRegistered(EmailAddress emailAddress) {
        super("a user is already registered with the email address " + emailAddress);
    }
}
