package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedBooleanBase;

public class Archived extends TypedBooleanBase<Archived> {

    private Archived(Boolean value) {
        super(value);
    }

    public static Archived of(boolean value) {return new Archived(value);}
}
