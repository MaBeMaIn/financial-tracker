package se.financial_tracker.account.commands;

import se.financial_tracker.account.domain.AccountName;
import se.financial_tracker.account.domain.AccountType;
import se.financial_tracker.user.domain.UserId;

import java.util.Objects;

public record CreateAccountCommand(AccountType type, AccountName accountName, UserId userId) {

    public CreateAccountCommand {
        Objects.requireNonNull(type, "accountType");
        Objects.requireNonNull(accountName, "accountNAme");
        Objects.requireNonNull(userId, "userId");
    }
}
