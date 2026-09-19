package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedStringBase;

/**
 * An already-hashed password, opaque to the domain: it is stored and compared, never inspected
 * or reversed. Hashing itself belongs to an adapter behind a {@code PasswordHasher} port, which
 * arrives with FR-2.1 and FR-2.2.
 *
 * <p>{@link #toString()} is redacted so the hash cannot reach a log line by accident.
 */
public final class PasswordHash extends TypedStringBase<PasswordHash> {

    private PasswordHash(String value) {
        super(value);
    }

    public static PasswordHash of(String value) {
        return new PasswordHash(value);
    }

    @Override
    public String toString() {
        return "PasswordHash[redacted]";
    }
}
