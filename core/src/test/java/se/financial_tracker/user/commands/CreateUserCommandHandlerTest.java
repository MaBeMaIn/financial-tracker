package se.financial_tracker.user.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.EmailAlreadyRegistered;
import se.financial_tracker.user.domain.PasswordHash;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.UserId;
import se.financial_tracker.user.port.out.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandHandlerTest {

    private static final EmailAddress EMAIL = EmailAddress.of("test@test.se");
    private static final DisplayName DISPLAY_NAME = DisplayName.of("Test Person");
    private static final PasswordHash PASSWORD_HASH = PasswordHash.of("hashed-password");

    @Mock
    private UserRepository users;

    @InjectMocks
    private CreateUserCommandHandler commandHandler;

    @Test
    void can_create_user() {
        // Arrange
        CreateUserCommand createUserCommand = new CreateUserCommand(EMAIL, DISPLAY_NAME, PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserId createdId = commandHandler.handle(createUserCommand);

        // Assert
        ArgumentCaptor<User> created = ArgumentCaptor.forClass(User.class);
        verify(users).save(created.capture());
        User user = created.getValue();
        assertThat(user.emailAddress()).isEqualTo(EMAIL);
        assertThat(user.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(user.passwordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(createdId).isEqualTo(user.userId());
    }

    @Test
    void throw_exception_when_email_is_already_registered() {
        // Arrange
        CreateUserCommand createUserCommand = new CreateUserCommand(EMAIL, DISPLAY_NAME, PASSWORD_HASH);
        when(users.existsByEmail(EMAIL)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> commandHandler.handle(createUserCommand))
                .isInstanceOf(EmailAlreadyRegistered.class)
                .hasMessageContaining(EMAIL.value());

        verify(users, never()).save(any());
    }
}
