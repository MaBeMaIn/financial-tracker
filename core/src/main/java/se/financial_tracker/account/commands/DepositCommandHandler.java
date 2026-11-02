package se.financial_tracker.account.commands;

import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.Transaction;
import se.financial_tracker.account.domain.TransactionId;
import se.financial_tracker.account.domain.UnknownAccount;
import se.financial_tracker.account.port.in.Deposit;
import se.financial_tracker.account.port.out.AccountRepository;

public final class DepositCommandHandler implements Deposit {

    private final AccountRepository accounts;

    public DepositCommandHandler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public TransactionId handle(DepositCommand command) {
        Account account =
                accounts.getByAccountId(command.accountId()).orElseThrow(() -> new UnknownAccount(command.accountId()));

        Transaction deposit = account.deposit(command.amount(), command.occurredOn(), command.note());
        accounts.save(account);
        return deposit.id();
    }
}
