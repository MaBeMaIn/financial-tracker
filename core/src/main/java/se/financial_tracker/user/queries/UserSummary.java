package se.financial_tracker.user.queries;

import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.UserId;

/**
 * What a reader is told about a user. A read model, not the aggregate: the password hash is
 * deliberately absent, so no caller can leak it.
 */
public record UserSummary(UserId userId, EmailAddress emailAddress, DisplayName displayName) {}
