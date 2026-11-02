package se.financial_tracker.account.domain;

import se.financial_tracker.user.domain.UserId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A user's record of a place their money sits. The account is the aggregate root for its
 * transactions: they are recorded through it, so its rules (such as refusing new transactions
 * once archived) cannot be bypassed. Transactions are stored in their own table (ADR-0009).
 */
public final class Account {

    private final AccountId accountId;
    private final AccountType accountType;
    private AccountName accountName;

    private final UserId owner;

    private boolean archived;

    private final List<Transaction> transactions;

    private Account(
            AccountId accountId,
            AccountType accountType,
            AccountName accountName,
            UserId owner,
            boolean archived,
            List<Transaction> transactions) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.owner = owner;
        this.archived = archived;
        this.transactions = new ArrayList<>(transactions);
    }

    public static Account createAccount(AccountType type, AccountName accountName, UserId owner) {
        return new Account(AccountId.newId(), type, accountName, owner, false, List.of());
    }

    public static Account hydrate(
            AccountId accountId,
            AccountType accountType,
            AccountName accountName,
            UserId owner,
            boolean archived,
            List<Transaction> transactions) {
        return new Account(accountId, accountType, accountName, owner, archived, transactions);
    }

    public void archiveAccount(Account account) {
        account.archived = true;
    }

    /**
     * Records money the user moved into this account (FR-3.6). The date may be in the past
     * (FR-3.9).
     *
     * @param note the user's remark, or {@code null} for none
     * @return the new transaction
     * @throws AccountArchived if this account is archived
     */
    public Transaction deposit(Amount amount, LocalDate occurredOn, Note note) {
        if (archived) {
            throw new AccountArchived(accountId);
        }
        Transaction deposit = Transaction.create(accountId, amount, occurredOn, Direction.DEPOSIT, note);
        transactions.add(deposit);
        return deposit;
    }

    public AccountId accountId() {
        return accountId;
    }

    public AccountType accountType() {
        return accountType;
    }

    public AccountName accountName() {
        return accountName;
    }

    public UserId owner() {
        return owner;
    }

    public boolean isArchived() {
        return archived;
    }

    public List<Transaction> transactions() {
        return List.copyOf(transactions);
    }
}
