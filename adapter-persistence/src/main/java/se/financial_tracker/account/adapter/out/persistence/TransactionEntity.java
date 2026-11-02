package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.Direction;
import se.financial_tracker.common.persistence.MutableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The stored shape of a transaction. Part of the account aggregate in the domain, but a row of
 * its own here, keyed by the domain's {@code TransactionId} (ADR-0009).
 */
@Entity
@Table(name = "transactions")
public class TransactionEntity extends MutableEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID transactionId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "occurred_on", nullable = false)
    private LocalDate occurredOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private Direction direction;

    @Column(name = "note", length = 500)
    private String note;

    protected TransactionEntity() {
        // for Hibernate
    }

    TransactionEntity(
            UUID transactionId,
            UUID accountId,
            BigDecimal amount,
            LocalDate occurredOn,
            Direction direction,
            String note) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.occurredOn = occurredOn;
        this.direction = direction;
        this.note = note;
    }

    /** For editing a transaction (FR-3.7); the identifier, account and direction never change. */
    void update(BigDecimal amount, LocalDate occurredOn, String note) {
        this.amount = amount;
        this.occurredOn = occurredOn;
        this.note = note;
    }

    UUID transactionId() {
        return transactionId;
    }

    UUID accountId() {
        return accountId;
    }

    BigDecimal amount() {
        return amount;
    }

    LocalDate occurredOn() {
        return occurredOn;
    }

    Direction direction() {
        return direction;
    }

    String note() {
        return note;
    }
}
