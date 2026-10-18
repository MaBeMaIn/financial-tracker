package se.financial_tracker.account.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountName;
import se.financial_tracker.account.domain.AccountType;
import se.financial_tracker.account.domain.UnknownOwner;
import se.financial_tracker.account.port.out.AccountRepository;
import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.UserId;
import se.financial_tracker.user.port.in.GetUser;
import se.financial_tracker.user.queries.GetUserQuery;
import se.financial_tracker.user.queries.UserSummary;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAccountCommandHandlerTest {

    private static final UserId OWNER = UserId.newId();
    private static final AccountName ACCOUNT_NAME = AccountName.of("Buffer");

    @Mock
    private AccountRepository accounts;

    @Mock
    private GetUser users;

    @InjectMocks
    private CreateAccountCommandHandler commandHandler;

    @Test
    void can_create_account() {
        // Arrange
        CreateAccountCommand command = new CreateAccountCommand(AccountType.SAVINGS, ACCOUNT_NAME, OWNER);
        when(users.handle(new GetUserQuery(OWNER))).thenReturn(Optional.of(anOwner()));
        when(accounts.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        commandHandler.handle(command);

        // Assert
        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accounts).save(saved.capture());
        assertThat(saved.getValue().owner()).isEqualTo(OWNER);
        assertThat(saved.getValue().accountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(saved.getValue().accountName()).isEqualTo(ACCOUNT_NAME);
    }

    @Test
    void the_new_account_id_is_returned() {
        // Arrange
        CreateAccountCommand command = new CreateAccountCommand(AccountType.INVESTMENT, ACCOUNT_NAME, OWNER);
        when(users.handle(new GetUserQuery(OWNER))).thenReturn(Optional.of(anOwner()));
        when(accounts.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var accountId = commandHandler.handle(command);

        // Assert
        ArgumentCaptor<Account> saved = ArgumentCaptor.forClass(Account.class);
        verify(accounts).save(saved.capture());
        assertThat(accountId).isEqualTo(saved.getValue().accountId());
    }

    @Test
    void an_account_for_an_unknown_user_is_rejected() {
        // Arrange
        CreateAccountCommand command = new CreateAccountCommand(AccountType.SAVINGS, ACCOUNT_NAME, OWNER);
        when(users.handle(new GetUserQuery(OWNER))).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(UnknownOwner.class)
                .hasMessageContaining(OWNER.toString());
        verify(accounts, never()).save(any(Account.class));
    }

    private static UserSummary anOwner() {
        return new UserSummary(OWNER, EmailAddress.of("test@test.se"), DisplayName.of("Test Person"));
    }
}
