package se.financial_tracker.user.domain;

import java.util.Objects;

/**
 * A registered person. Built through {@link #create} when someone registers, and through
 * {@link #hydrate} when a persistence mapper reads one back (ADR-0003).
 */
public final class User {

    private final UserId userId;
    private final EmailAddress emailAddress;
    private final DisplayName displayName;
    private final PasswordHash passwordHash;

    private User(UserId userId, EmailAddress emailAddress, DisplayName displayName, PasswordHash passwordHash) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
    }

    /** Someone registers. Creation-time rules live here. */
    public static User create(EmailAddress emailAddress, DisplayName displayName, PasswordHash passwordHash) {
        Objects.requireNonNull(emailAddress, "emailAddress");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(passwordHash, "passwordHash");
        return new User(UserId.newId(), emailAddress, displayName, passwordHash);
    }

    /** Reconstitution from stored state. No rules, no defaults, no new identity. */
    public static User hydrate(
            UserId userId, EmailAddress emailAddress, DisplayName displayName, PasswordHash passwordHash) {
        return new User(userId, emailAddress, displayName, passwordHash);
    }

    public UserId userId() {
        return userId;
    }

    public EmailAddress emailAddress() {
        return emailAddress;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }
}
