package se.financial_tracker.account.port.in;

import se.financial_tracker.account.commands.DepositCommand;
import se.financial_tracker.account.domain.TransactionId;

public interface Deposit {

    TransactionId handle(DepositCommand command);
}
