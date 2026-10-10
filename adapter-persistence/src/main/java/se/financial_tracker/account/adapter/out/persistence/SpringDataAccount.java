package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import se.financial_tracker.account.domain.AccountId;

import java.util.Optional;
import java.util.UUID;

interface SpringDataAccount extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findById(AccountId id);
}
