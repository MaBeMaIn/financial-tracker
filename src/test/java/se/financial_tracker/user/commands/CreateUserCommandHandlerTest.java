package se.financial_tracker.user.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.Username;
import se.financial_tracker.user.exception.UserException;
import se.financial_tracker.user.port.out.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandHandlerTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CreateUserCommandHandler commandHandler;

    @Test
    void can_create_user() {
        // Arrange
        Username username = Username.of("test");
        EmailAddress emailAddress = EmailAddress.of("test@test.se");
        CreateUserCommand createUserCommand = new CreateUserCommand(username, emailAddress);

        // Act
        commandHandler.handle(createUserCommand);

        // Assert
        ArgumentCaptor<User> created = ArgumentCaptor.forClass(User.class);
        verify(repository).createUser(created.capture());
        User user = created.getValue();
        assertThat(user.username()).isEqualTo(username);
        assertThat(user.emailAddress()).isEqualTo(emailAddress);
    }

    @Test
    void throw_exception_when_user_exsists() {
        // Arrange
        Username username = Username.of("test");
        EmailAddress emailAddress = EmailAddress.of("test@test.se");
        CreateUserCommand createUserCommand = new CreateUserCommand(username, emailAddress);
        when(repository.exsistsByUsername(username)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> commandHandler.handle(createUserCommand))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).createUser(any());
    }
}
