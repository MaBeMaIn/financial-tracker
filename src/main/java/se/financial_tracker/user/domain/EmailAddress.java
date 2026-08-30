package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedStringBase;
import se.financial_tracker.user.exception.UserException;

public class EmailAddress extends TypedStringBase<EmailAddress> {

    private EmailAddress(String value) {
        super(value);
        if (!value.contains("@")) {
            throw new UserException("Invalid email");
        }
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }
}
