package se.financial_tracker.user.queries;

import se.financial_tracker.user.domain.UserId;

import java.util.Objects;

public record GetUserQuery(UserId userId) {

    public GetUserQuery {
        Objects.requireNonNull(userId, "userId");
    }
}
