package se.financial_tracker.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;

/**
 * The audit columns every stored row carries. The primary key is not here: each entity's key is
 * the domain's own identifier, a UUID version 7 assigned when the aggregate is created, so there
 * is nothing for a base class to generate.
 *
 * <p>{@code @MappedSuperclass} is what makes these fields and the callbacks below part of each
 * entity's mapping; without it Hibernate ignores the whole class.
 */
@MappedSuperclass
public abstract class MutableEntity {

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Both columns are set on insert, so neither is ever null and both can be {@code not null}. */
    @PrePersist
    private void onInsert() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
