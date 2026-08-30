package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedUuidBase;

import java.util.UUID;

public final class UserId extends TypedUuidBase<UserId> {

    private UserId(UUID value) {
        super(value);
    }

    public static UserId newId() {
        UUID id = UUID.randomUUID();
        return UserId.of(id);
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }
}
