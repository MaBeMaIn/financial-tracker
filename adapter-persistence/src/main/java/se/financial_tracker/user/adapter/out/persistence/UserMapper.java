package se.financial_tracker.user.adapter.out.persistence;

import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.PasswordHash;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.UserId;

/**
 * Translates between the two models by hand, so the reconciliation stays visible (ADR-0002).
 * This is the only place allowed to call {@link User#hydrate} (ADR-0003).
 */
final class UserMapper {

    private UserMapper() {}

    static UserEntity toEntity(User user) {
        return new UserEntity(
                user.userId().value(),
                user.emailAddress().value(),
                user.displayName().value(),
                user.passwordHash().value());
    }

    static User toDomain(UserEntity entity) {
        return User.hydrate(
                UserId.of(entity.id()),
                EmailAddress.of(entity.email()),
                DisplayName.of(entity.displayName()),
                PasswordHash.of(entity.passwordHash()));
    }
}
