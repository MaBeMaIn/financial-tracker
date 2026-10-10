package se.financial_tracker.account.adapter.out.persistence;

import se.financial_tracker.account.domain.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID accountId;

    @Column(name = "user_id", nullable = false)
    private UUID owner;

    @Column(name = "name", nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType accountType;

    @Column(name = "archived")
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
