package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.DomainException;

/** A transaction is recorded on an account, and that account has to exist (FR-3.6). */
public final class UnknownAccount extends DomainException {

    public UnknownAccount(AccountId accountId) {
        super("no account exists with the id " + accountId);
    }
}
