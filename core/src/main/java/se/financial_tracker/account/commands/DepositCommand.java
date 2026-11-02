package se.financial_tracker.account.commands;

import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.Amount;
import se.financial_tracker.account.domain.Note;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Record a deposit on an account (FR-3.6).
 *
 * @param note the user's remark, or {@code null} for none
 */
public record DepositCommand(AccountId accountId, Amount amount, LocalDate occurredOn, Note note) {

    public DepositCommand {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(occurredOn, "occurredOn");
    }
}
