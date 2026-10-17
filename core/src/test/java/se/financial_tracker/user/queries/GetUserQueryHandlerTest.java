package se.financial_tracker.user.queries;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.PasswordHash;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.UserId;
import se.financial_tracker.user.port.out.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserQueryHandlerTest {

    @Mock
    private UserRepository users;

    @InjectMocks
    private GetUserQueryHandler queryHandler;

    @Test
    void a_known_user_is_summarized() {
        // Arrange
        User user = User.create(
                EmailAddress.of("test@test.se"), DisplayName.of("Test Person"), PasswordHash.of("hashed-password"));
        when(users.byId(user.userId())).thenReturn(Optional.of(user));

        // Act
        Optional<UserSummary> summary = queryHandler.handle(new GetUserQuery(user.userId()));

        // Assert
        assertThat(summary).get().satisfies(found -> {
            assertThat(found.userId()).isEqualTo(user.userId());
            assertThat(found.emailAddress()).isEqualTo(user.emailAddress());
            assertThat(found.displayName()).isEqualTo(user.displayName());
        });
    }

    @Test
    void an_unknown_user_is_not_found() {
        // Arrange
        UserId unknown = UserId.newId();
        when(users.byId(unknown)).thenReturn(Optional.empty());

        // Act
        Optional<UserSummary> summary = queryHandler.handle(new GetUserQuery(unknown));

        // Assert
        assertThat(summary).isEmpty();
    }
}
