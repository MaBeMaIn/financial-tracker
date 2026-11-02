package se.financial_tracker.account.domain;

import se.financial_tracker.common.domain.DomainException;

/** An archived account accepts no new transactions or valuations (invariant 2). */
public final class AccountArchived extends DomainException {

    public AccountArchived(AccountId accountId) {
        super("the account " + accountId + " is archived, so it accepts no new transactions");
    }
}
