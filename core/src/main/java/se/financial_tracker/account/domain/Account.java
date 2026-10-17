package se.financial_tracker.account.domain;

import se.financial_tracker.user.domain.UserId;

public final class Account {

    private final AccountId accountId;
    private final AccountType accountType;
    private AccountName accountName;

    private final UserId owner;

    private boolean archived;

    public Account(
            AccountId accountId, AccountType accountType, AccountName accountName, UserId owner, boolean archived) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.owner = owner;
        this.archived = archived;
    }

    public static Account createAccount(AccountType type, AccountName accountName, UserId owner) {
        return new Account(AccountId.newId(), type, accountName, owner, false);
    }

    public static Account hydrate(
            AccountId accountId, AccountType accountType, AccountName accountName, UserId owner, boolean archived) {
        return new Account(accountId, accountType, accountName, owner, archived);
    }

    public void archiveAccount(Account account) {
        account.archived = true;
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
}
