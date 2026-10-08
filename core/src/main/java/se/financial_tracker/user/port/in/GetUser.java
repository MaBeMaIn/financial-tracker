package se.financial_tracker.user.port.in;

import se.financial_tracker.user.queries.GetUserQuery;
import se.financial_tracker.user.queries.UserSummary;

import java.util.Optional;

/**
 * Reading one user (FR-2.3). This is also how other slices ask about a user: a slice may depend on
 * another slice's inbound ports only, never on its domain, handlers or persistence.
 */
public interface GetUser {

    Optional<UserSummary> handle(GetUserQuery query);
}
