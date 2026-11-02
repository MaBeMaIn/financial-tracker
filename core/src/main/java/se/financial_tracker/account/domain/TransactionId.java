package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedUuidBase;
import se.financial_tracker.common.domain.Uuid7;

import java.util.UUID;

public final class TransactionId extends TypedUuidBase<TransactionId> {

    private TransactionId(UUID value) {
        super(value);
    }

    public static TransactionId newId() {
        return TransactionId.of(Uuid7.newUuid());
    }

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }
}
