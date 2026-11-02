package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.port.out.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The outbound port, implemented against JPA. Speaks domain types only.
 *
 * <p>An account and its transactions are one aggregate but two tables (ADR-0009): loading an
 * account loads its transactions, and saving one inserts the transactions not stored yet.
 *
 * <p>Not {@code final}: {@code @Repository} asks Spring to proxy this bean so that persistence
 * exceptions are translated, and a CGLIB proxy has to subclass it.
 */
@Repository
public class JpaAccountRepository implements AccountRepository {

    private final SpringDataAccount accounts;
    private final SpringDataTransaction transactions;

    JpaAccountRepository(SpringDataAccount accounts, SpringDataTransaction transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Override
    public Optional<Account> getByAccountId(AccountId id) {
        return accounts.findById(id.value()).map(entity -> AccountMapper.toDomain(entity, transactionsOf(entity)));
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = accounts.findById(account.accountId().value())
                .map(existing -> {
                    existing.update(account.accountName().value(), account.isArchived());
                    return existing;
                })
                .orElseGet(() -> AccountMapper.toEntity(account));
        AccountEntity saved = accounts.save(entity);

        Set<UUID> stored = transactions.findIdsByAccountId(saved.accountId());
        List<TransactionEntity> added = account.transactions().stream()
                .filter(transaction -> !stored.contains(transaction.id().value()))
                .map(TransactionMapper::toEntity)
                .toList();
        transactions.saveAll(added);

        return AccountMapper.toDomain(saved, transactionsOf(saved));
    }

    private List<TransactionEntity> transactionsOf(AccountEntity account) {
        return transactions.findByAccountIdOrderByOccurredOnAscTransactionIdAsc(account.accountId());
    }
}
