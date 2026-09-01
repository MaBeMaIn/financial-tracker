package se.financial_tracker.user.commands;

import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.Username;

public record CreateUserCommand(Username username, EmailAddress emailAddress) {}
