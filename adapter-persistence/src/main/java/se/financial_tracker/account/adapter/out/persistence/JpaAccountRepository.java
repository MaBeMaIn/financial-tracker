package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.port.out.AccountRepository;

import java.util.Optional;

/**
 * The outbound port, implemented against JPA. Speaks domain types only.
 *
 * <p>Not {@code final}: {@code @Repository} asks Spring to proxy this bean so that persistence
 * exceptions are translated, and a CGLIB proxy has to subclass it.
 */
@Repository
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccount accounts;

    JpaAccountRepository(SpringDataAccount accounts) {
        this.accounts = accounts;
    }

    @Override
    public Optional<Account> getByAccountId(AccountId id) {
        return accounts.findById(id.value()).map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = accounts.findById(account.accountId().value())
                .map(existing -> {
                    existing.update(account.accountName().value(), account.isArchived());
                    return existing;
                })
                .orElseGet(() -> AccountMapper.toEntity(account));

        return AccountMapper.toDomain(accounts.save(entity));
    }
}
