package se.financial_tracker.user.port.out;

import se.financial_tracker.user.domain.User;

public interface UserRepository {
    User createUser(User user);
}
