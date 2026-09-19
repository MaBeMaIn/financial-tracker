package se.financial_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.financial_tracker.user.commands.CreateUserCommandHandler;
import se.financial_tracker.user.port.in.CreateUser;
import se.financial_tracker.user.port.out.UserRepository;

/**
 * Handlers are plain Java, so the application module declares them as beans rather than
 * annotating them (ADR-0007). The bean is typed as the inbound port.
 */
@Configuration
class UserConfiguration {

    @Bean
    CreateUser createUser(UserRepository users) {
        return new CreateUserCommandHandler(users);
    }
}
