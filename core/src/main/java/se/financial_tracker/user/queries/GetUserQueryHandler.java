package se.financial_tracker.user.queries;

import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.port.in.GetUser;
import se.financial_tracker.user.port.out.UserRepository;

import java.util.Optional;

public final class GetUserQueryHandler implements GetUser {

    private final UserRepository users;

    public GetUserQueryHandler(UserRepository users) {
        this.users = users;
    }

    @Override
    public Optional<UserSummary> handle(GetUserQuery query) {
        return users.byId(query.userId()).map(GetUserQueryHandler::toSummary);
    }

    private static UserSummary toSummary(User user) {
        return new UserSummary(user.userId(), user.emailAddress(), user.displayName());
    }
}
