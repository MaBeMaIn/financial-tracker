package se.financial_tracker.user.commands;

import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.PasswordHash;

import java.util.Objects;

public record CreateUserCommand(EmailAddress emailAddress, DisplayName displayName, PasswordHash passwordHash) {

    public CreateUserCommand {
        Objects.requireNonNull(emailAddress, "emailAddress");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(passwordHash, "passwordHash");
    }
}
