package se.financial_tracker.user.commands;

import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.exception.UserException;
import se.financial_tracker.user.port.out.UserRepository;

public final class CreateUserCommandHandler {

    private final UserRepository userRepository;

    public CreateUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handle(CreateUserCommand command) {
        if (userRepository.exsistsByUsername(command.username())) {
            throw new UserException("User with username: " + command.username() + " already exists.");
        }

        User user = User.createUser(command.username(), command.emailAddress());

        userRepository.createUser(user);
    }
}
