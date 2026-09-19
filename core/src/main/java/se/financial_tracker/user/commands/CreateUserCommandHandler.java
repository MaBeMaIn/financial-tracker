package se.financial_tracker.user.commands;

import se.financial_tracker.user.domain.EmailAlreadyRegistered;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.UserId;
import se.financial_tracker.user.port.in.CreateUser;
import se.financial_tracker.user.port.out.UserRepository;

public final class CreateUserCommandHandler implements CreateUser {

    private final UserRepository users;

    public CreateUserCommandHandler(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserId handle(CreateUserCommand command) {
        if (users.existsByEmail(command.emailAddress())) {
            throw new EmailAlreadyRegistered(command.emailAddress());
        }

        User user = User.create(command.emailAddress(), command.displayName(), command.passwordHash());

        return users.save(user).userId();
    }
}
