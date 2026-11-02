package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.Amount;
import se.financial_tracker.account.domain.Note;
import se.financial_tracker.account.domain.Transaction;
import se.financial_tracker.account.domain.TransactionId;

/** Translates between the two models by hand, so the reconciliation stays visible (ADR-0002). */
final class TransactionMapper {

    private TransactionMapper() {}

    static TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.id().value(),
                transaction.accountId().value(),
                transaction.amount().value(),
                transaction.occurredOn(),
                transaction.direction(),
                transaction.note().map(Note::value).orElse(null));
    }

    static Transaction toDomain(TransactionEntity entity) {
        return Transaction.hydrate(
                TransactionId.of(entity.transactionId()),
                AccountId.of(entity.accountId()),
                Amount.of(entity.amount()),
                entity.occurredOn(),
                entity.direction(),
                entity.note() == null ? null : Note.of(entity.note()));
    }
}
