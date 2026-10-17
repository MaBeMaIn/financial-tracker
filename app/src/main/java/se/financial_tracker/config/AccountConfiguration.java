package se.financial_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.financial_tracker.account.commands.CreateAccountCommandHandler;
import se.financial_tracker.account.port.in.CreateAccount;
import se.financial_tracker.account.port.out.AccountRepository;
import se.financial_tracker.user.port.in.GetUser;

@Configuration
public class AccountConfiguration {

    @Bean
    CreateAccount createAccount(AccountRepository accounts, GetUser users) {
        return new CreateAccountCommandHandler(accounts, users);
    }
}
