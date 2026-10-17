package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.AccountType;
import se.financial_tracker.common.persistence.MutableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * The stored shape of an account. The key is the domain's own {@code AccountId}; the audit columns
 * come from {@link MutableEntity}.
 */
@Entity
@Table(name = "accounts")
public class AccountEntity extends MutableEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID accountId;

    @Column(name = "user_id", nullable = false)
    private UUID owner;

    @Column(name = "name", nullable = false, length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "archived", nullable = false)
    private boolean archived;

    protected AccountEntity() {
        // for Hibernate
    }

    AccountEntity(UUID accountId, AccountType accountType, String accountName, UUID owner, boolean archived) {
        this.accountId = accountId;
        this.owner = owner;
        this.accountName = accountName;
        this.accountType = accountType;
        this.archived = archived;
    }

    UUID accountId() {
        return accountId;
    }

    UUID owner() {
        return owner;
    }

    String accountName() {
        return accountName;
    }

    AccountType accountType() {
        return accountType;
    }

    boolean archived() {
        return archived;
    }
}
