package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedUuidBase;
import se.financial_tracker.common.domain.Uuid7;

import java.util.UUID;

public class AccountId extends TypedUuidBase<AccountId> {
    private AccountId(UUID value) {
        super(value);
    }

    public static AccountId newId() {
        return AccountId.of(Uuid7.newUuid());
    }

    public static AccountId of(UUID value) {
        return new AccountId(value);
    }
}
