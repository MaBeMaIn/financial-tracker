package se.financial_tracker.account.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Money the user moved into or out of an account, on a date (FR-3.6). Part of the {@link Account}
 * aggregate: only an account creates its transactions, which is how an archived account gets to
 * refuse new ones (invariant 2).
 */
public final class Transaction {

    private final TransactionId id;
    private final AccountId accountId;
    private final Amount amount;
    private final LocalDate occurredOn;
    private final Direction direction;
    private final Note note;

    private Transaction(
            TransactionId id,
            AccountId accountId,
            Amount amount,
            LocalDate occurredOn,
            Direction direction,
            Note note) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.occurredOn = occurredOn;
        this.direction = direction;
        this.note = note;
    }

    /** Package-private: new transactions come from {@link Account}, never from outside it. */
    static Transaction create(
            AccountId accountId, Amount amount, LocalDate occurredOn, Direction direction, Note note) {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(occurredOn, "occurredOn");
        Objects.requireNonNull(direction, "direction");
        return new Transaction(TransactionId.newId(), accountId, amount, occurredOn, direction, note);
    }

    /** Reconstitutes a stored transaction. Called only by the persistence mapper. */
    public static Transaction hydrate(
            TransactionId id,
            AccountId accountId,
            Amount amount,
            LocalDate occurredOn,
            Direction direction,
            Note note) {
        return new Transaction(id, accountId, amount, occurredOn, direction, note);
    }

    public TransactionId id() {
        return id;
    }

    public AccountId accountId() {
        return accountId;
    }

    public Amount amount() {
        return amount;
    }

    public LocalDate occurredOn() {
        return occurredOn;
    }

    public Direction direction() {
        return direction;
    }

    public Optional<Note> note() {
        return Optional.ofNullable(note);
    }
}
