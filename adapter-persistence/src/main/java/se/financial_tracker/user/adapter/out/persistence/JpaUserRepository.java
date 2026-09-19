package se.financial_tracker.user.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.port.out.UserRepository;

import java.util.Optional;

/**
 * The outbound port, implemented against JPA. Speaks domain types only.
 *
 * <p>Not {@code final}: {@code @Repository} asks Spring to proxy this bean so that persistence
 * exceptions are translated, and a CGLIB proxy has to subclass it.
 */
@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUsers users;

    JpaUserRepository(SpringDataUsers users) {
        this.users = users;
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(users.save(UserMapper.toEntity(user)));
    }

    @Override
    public Optional<User> byEmail(EmailAddress emailAddress) {
        return users.findByEmail(emailAddress.value()).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress emailAddress) {
        return users.existsByEmail(emailAddress.value());
    }
}
