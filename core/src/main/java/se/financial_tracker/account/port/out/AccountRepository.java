package se.financial_tracker.account.port.out;

import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;

import java.util.Optional;

public interface AccountRepository {

    Optional<Account> getByAccountId(AccountId id);

    Account save(Account account);

}
