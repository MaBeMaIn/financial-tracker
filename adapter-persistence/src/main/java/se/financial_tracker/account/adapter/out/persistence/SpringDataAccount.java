package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Spring Data's view of the table. Package-private: nothing outside this adapter may use it. */
interface SpringDataAccount extends JpaRepository<AccountEntity, UUID> {}
