package se.financial_tracker.user.adapter.out.persistence;

import se.financial_tracker.common.persistence.MutableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * The stored shape of a user. Hibernate's rules — a no-argument constructor, mutable fields,
 * a non-final class — stop at this package (ADR-0002).
 */
@Entity
@Table(name = "users")
class UserEntity extends MutableEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID userId;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    protected UserEntity() {
        // for Hibernate
    }

    UserEntity(UUID userId, String email, String displayName, String passwordHash) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
    }

    UUID userId() {
        return userId;
    }

    String email() {
        return email;
    }

    String displayName() {
        return displayName;
    }

    String passwordHash() {
        return passwordHash;
    }
}
