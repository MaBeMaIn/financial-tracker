package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.*;
import se.financial_tracker.user.domain.UserId;

public class AccountMapper {

    private AccountMapper() {}

    static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.accountId().value(),
                account.accountType(),
                account.accountName().value(),
                account.owner().value(),
                account.isArchived().value());
    }

    static Account toDomain(AccountEntity entity) {
        return Account.hydrate(
                AccountId.of(entity.accountId()),
                AccountType.valueOf(entity.accountType().name()),
                AccountName.of(entity.accountName()),
                UserId.of(entity.owner()),
                Archived.of(entity.archived()));
    }
}
