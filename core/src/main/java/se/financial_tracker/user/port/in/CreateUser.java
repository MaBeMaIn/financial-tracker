package se.financial_tracker.user.port.in;

import se.financial_tracker.user.commands.CreateUserCommand;
import se.financial_tracker.user.domain.UserId;

/** Registering a user (FR-2.1). */
public interface CreateUser {

    UserId handle(CreateUserCommand command);
}
