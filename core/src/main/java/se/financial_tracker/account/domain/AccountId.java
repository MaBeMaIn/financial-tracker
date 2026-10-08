package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedUuidBase;

import java.util.UUID;

public class AccountId extends TypedUuidBase<AccountId> {
    private AccountId(UUID value) {
        super(value);
    }

    public static AccountId newId() {
        UUID id = UUID.randomUUID();
        return AccountId.of(id);
    }

    public static AccountId of(UUID value) {
        return new AccountId(value);
    }
}
