package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.TypedStringBase;

public class AccountName extends TypedStringBase<AccountName> {

    private AccountName(String value) {
        super(value);
    }

    public static AccountName of(String value) {
        return new AccountName(value);
    }
}
