package se.financial_tracker.user.domain;

import java.util.UUID;

public final class User {

    private final UserId userId;
    private final Username username;

    private final EmailAddress emailAddress;

    private User(UserId userId, Username username, EmailAddress emailAddress) {
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
    }

    public static User createUser(Username username, EmailAddress emailAddress) {
        return new User(UserId.newId(), username, emailAddress);
    }

    public Username username() {
        return username;
    }

    public EmailAddress emailAddress() {
        return emailAddress;
    }
}
