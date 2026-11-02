package se.financial_tracker.account.port.out;

import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;

import java.util.Optional;

public interface AccountRepository {

    /** The account with its transactions, oldest {@code occurredOn} first. */
    Optional<Account> getByAccountId(AccountId id);

    /** Saves the account and any of its transactions that are not stored yet. */
    Account save(Account account);
}
