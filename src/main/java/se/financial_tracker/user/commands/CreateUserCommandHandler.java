package se.financial_tracker.user.commands;

import se.financial_tracker.user.port.out.UserRepository;

public final class CreateUserCommandHandler {

    private final UserRepository userRepository;

    public CreateUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handle(CreateUserCommand command) {
        // 1. Check if user already exists
        // 2. Create User domain object
        // 3. Persist in repository
    }
}
