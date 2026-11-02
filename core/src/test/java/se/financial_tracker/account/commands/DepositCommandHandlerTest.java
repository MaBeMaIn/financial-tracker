package se.financial_tracker.account.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountArchived;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.AccountName;
import se.financial_tracker.account.domain.AccountType;
import se.financial_tracker.account.domain.Amount;
import se.financial_tracker.account.domain.Direction;
import se.financial_tracker.account.domain.Note;
import se.financial_tracker.account.domain.UnknownAccount;
import se.financial_tracker.account.port.out.AccountRepository;
import se.financial_tracker.user.domain.UserId;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositCommandHandlerTest {

    private static final Amount AMOUNT = Amount.of("1500.00");
    private static final LocalDate OCCURRED_ON = LocalDate.of(2026, 10, 1);

    @Mock
    private AccountRepository accounts;

    @InjectMocks
    private DepositCommandHandler commandHandler;

    @Test
    void a_deposit_is_recorded_on_the_account_and_saved() {
        // Arrange
        Account account = anAccount();
        when(accounts.getByAccountId(account.accountId())).thenReturn(Optional.of(account));
        DepositCommand command = new DepositCommand(account.accountId(), AMOUNT, OCCURRED_ON, Note.of("Salary"));

        // Act
        var transactionId = commandHandler.handle(command);

        // Assert
        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accounts).save(saved.capture());
        assertThat(saved.getValue().transactions()).singleElement().satisfies(deposit -> {
            assertThat(deposit.id()).isEqualTo(transactionId);
            assertThat(deposit.accountId()).isEqualTo(account.accountId());
            assertThat(deposit.amount()).isEqualTo(AMOUNT);
            assertThat(deposit.occurredOn()).isEqualTo(OCCURRED_ON);
            assertThat(deposit.direction()).isEqualTo(Direction.DEPOSIT);
            assertThat(deposit.note()).contains(Note.of("Salary"));
        });
    }

    @Test
    void a_deposit_needs_no_note() {
        // Arrange
        Account account = anAccount();
        when(accounts.getByAccountId(account.accountId())).thenReturn(Optional.of(account));
        DepositCommand command = new DepositCommand(account.accountId(), AMOUNT, OCCURRED_ON, null);

        // Act
        commandHandler.handle(command);

        // Assert
        assertThat(account.transactions())
                .singleElement()
                .satisfies(deposit -> assertThat(deposit.note()).isEmpty());
    }

    @Test
    void a_deposit_on_an_unknown_account_is_rejected() {
        // Arrange
        AccountId unknown = AccountId.newId();
        when(accounts.getByAccountId(unknown)).thenReturn(Optional.empty());
        DepositCommand command = new DepositCommand(unknown, AMOUNT, OCCURRED_ON, null);

        // Act + Assert
        assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(UnknownAccount.class)
                .hasMessageContaining(unknown.toString());
        verify(accounts, never()).save(any(Account.class));
    }

    @Test
    void a_deposit_on_an_archived_account_is_rejected() {
        // Arrange
        Account account = anAccount();
        account.archiveAccount(account);
        when(accounts.getByAccountId(account.accountId())).thenReturn(Optional.of(account));
        DepositCommand command = new DepositCommand(account.accountId(), AMOUNT, OCCURRED_ON, null);

        // Act + Assert
        assertThatThrownBy(() -> commandHandler.handle(command)).isInstanceOf(AccountArchived.class);
        verify(accounts, never()).save(any(Account.class));
    }

    private static Account anAccount() {
        return Account.createAccount(AccountType.SAVINGS, AccountName.of("Buffer"), UserId.newId());
    }
}
