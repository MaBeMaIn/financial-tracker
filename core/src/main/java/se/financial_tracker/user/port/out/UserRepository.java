package se.financial_tracker.user.port.out;

import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.User;

import java.util.Optional;

/** What the user slice needs from storage, in the domain's own terms. */
public interface UserRepository {

    User save(User user);

    Optional<User> byEmail(EmailAddress emailAddress);

    boolean existsByEmail(EmailAddress emailAddress);
}
