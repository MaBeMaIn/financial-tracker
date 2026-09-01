package se.financial_tracker.user.domain;

import se.financial_tracker.common.domain.TypedStringBase;

public final class Username extends TypedStringBase<Username> {

    private Username(String value) {
        super(value);
    }

    public static Username of(String value) {
        return new Username(value);
    }
}
