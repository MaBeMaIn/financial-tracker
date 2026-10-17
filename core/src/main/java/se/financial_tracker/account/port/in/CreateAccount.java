package se.financial_tracker.account.port.in;

import se.financial_tracker.account.commands.CreateAccountCommand;
import se.financial_tracker.account.domain.AccountId;

public interface CreateAccount {

    AccountId handle(CreateAccountCommand command);
}
