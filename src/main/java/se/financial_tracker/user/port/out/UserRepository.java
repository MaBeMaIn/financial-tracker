package se.financial_tracker.user.port.out;

import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.Username;

public interface UserRepository {
    User createUser(User user);

    Username getUsername(Username username);

    boolean exsistsByUsername(Username username);
}
