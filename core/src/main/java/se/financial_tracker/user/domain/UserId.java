package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedUuidBase;
import se.financial_tracker.common.domain.Uuid7;

import java.util.UUID;

public final class UserId extends TypedUuidBase<UserId> {

    private UserId(UUID value) {
        super(value);
    }

    public static UserId newId() {
        return UserId.of(Uuid7.newUuid());
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }
}
