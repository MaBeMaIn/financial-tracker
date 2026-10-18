package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.AccountName;
import se.financial_tracker.user.domain.UserId;

/** Translates between the two models by hand, so the reconciliation stays visible (ADR-0002). */
final class AccountMapper {

    private AccountMapper() {}

    static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.accountId().value(),
                account.accountType(),
                account.accountName().value(),
                account.owner().value(),
                account.isArchived());
    }

    static Account toDomain(AccountEntity entity) {
        return Account.hydrate(
                AccountId.of(entity.accountId()),
                entity.accountType(),
                AccountName.of(entity.accountName()),
                UserId.of(entity.owner()),
                entity.archived());
    }
}
