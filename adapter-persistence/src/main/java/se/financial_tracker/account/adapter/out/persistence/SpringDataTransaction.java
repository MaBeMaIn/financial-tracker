package se.financial_tracker.account.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Spring Data's view of the table. Package-private: nothing outside this adapter may use it. */
interface SpringDataTransaction extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByAccountIdOrderByOccurredOnAscTransactionIdAsc(UUID accountId);

    @Query("select t.transactionId from TransactionEntity t where t.accountId = :accountId")
    Set<UUID> findIdsByAccountId(@Param("accountId") UUID accountId);
}
