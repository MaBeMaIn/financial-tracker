package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.port.out.AccountRepository;

import java.util.Optional;

@Repository
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccount accounts;

    JpaAccountRepository(SpringDataAccount accounts) {
        this.accounts = accounts;
    }

    @Override
    public Optional<Account> getById(AccountId id) {
        return accounts.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        var entity = AccountMapper.toEntity(account);

        var saved = accounts.save(entity);

        AccountMapper.toDomain(saved);
        return account;
    }

}
