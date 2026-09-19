package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedStringBase;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A user's email address, and the identity they log in with (FR-2.1).
 *
 * <p>The value is lower-cased before it is validated, so that {@code Maria@Example.com} and
 * {@code maria@example.com} are one address. Without that, the unique index on the column would
 * not mean what it claims.
 */
public final class EmailAddress extends TypedStringBase<EmailAddress> {

    private static final Pattern PATTERN = Pattern.compile(".+@.+\\..+");

    private EmailAddress(String value) {
        super(value);
    }

    public static EmailAddress of(String raw) {
        EmailAddress email = new EmailAddress(raw == null ? null : raw.toLowerCase(Locale.ROOT));
        if (!PATTERN.matcher(email.value()).matches()) {
            throw new IllegalArgumentException("not a valid email address: " + email.value());
        }
        return email;
    }
}
