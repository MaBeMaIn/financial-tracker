package se.financial_tracker.account.commands;

import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.UnknownOwner;
import se.financial_tracker.account.port.in.CreateAccount;
import se.financial_tracker.account.port.out.AccountRepository;
import se.financial_tracker.user.port.in.GetUser;
import se.financial_tracker.user.queries.GetUserQuery;

public final class CreateAccountCommandHandler implements CreateAccount {

    private final AccountRepository accounts;
    private final GetUser users;

    public CreateAccountCommandHandler(AccountRepository accounts, GetUser users) {
        this.accounts = accounts;
        this.users = users;
    }

    @Override
    public AccountId handle(CreateAccountCommand command) {
        if (users.handle(new GetUserQuery(command.userId())).isEmpty()) {
            throw new UnknownOwner(command.userId());
        }
        // TODO regel: Kan inte ha flera konton med samma typ och namn per användare
        Account account = Account.createAccount(command.type(), command.accountName(), command.userId());
        return accounts.save(account).accountId();
    }
}
