package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedStringBase;

/** The name a user is shown as. Not an identifier: two users may share one (FR-2.3). */
public final class DisplayName extends TypedStringBase<DisplayName> {

    private DisplayName(String value) {
        super(value);
    }

    public static DisplayName of(String value) {
        return new DisplayName(value);
    }
}
