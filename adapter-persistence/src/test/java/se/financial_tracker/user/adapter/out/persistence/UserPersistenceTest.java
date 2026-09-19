package se.financial_tracker.user.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.PasswordHash;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.port.out.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs against the schema Flyway built, on H2. {@code replace = NONE} keeps the configured
 * datasource, so the migration is what the mapping is validated against.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaUserRepository.class)
class UserPersistenceTest {

    @Autowired
    private UserRepository users;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void a_saved_user_is_found_by_email() {
        // Arrange
        User user = aUser("test@test.se");

        // Act
        users.save(user);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(users.byEmail(EmailAddress.of("test@test.se"))).get().satisfies(found -> {
            assertThat(found.userId()).isEqualTo(user.userId());
            assertThat(found.emailAddress()).isEqualTo(user.emailAddress());
            assertThat(found.displayName()).isEqualTo(user.displayName());
            assertThat(found.passwordHash()).isEqualTo(user.passwordHash());
        });
    }

    @Test
    void an_unknown_email_is_not_found() {
        // Arrange
        EmailAddress unknown = EmailAddress.of("nobody@test.se");

        // Act + Assert
        assertThat(users.byEmail(unknown)).isEmpty();
        assertThat(users.existsByEmail(unknown)).isFalse();
    }

    @Test
    void a_saved_email_exists() {
        // Arrange
        User user = aUser("test@test.se");

        // Act
        users.save(user);
        entityManager.flush();

        // Assert
        assertThat(users.existsByEmail(EmailAddress.of("test@test.se"))).isTrue();
    }

    @Test
    void an_email_is_matched_whatever_case_it_was_written_in() {
        // Arrange
        User user = aUser("Test.Person@Test.SE");

        // Act
        users.save(user);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(users.existsByEmail(EmailAddress.of("test.person@test.se"))).isTrue();
        assertThat(users.byEmail(EmailAddress.of("TEST.PERSON@TEST.SE"))).isPresent();
    }

    @Test
    void two_users_cannot_share_an_email() {
        // Arrange
        users.save(aUser("test@test.se"));
        entityManager.flush();

        // Act + Assert
        users.save(aUser("test@test.se"));
        assertThatThrownBy(entityManager::flush).isInstanceOf(PersistenceException.class);
    }

    private static User aUser(String email) {
        return User.create(EmailAddress.of(email), DisplayName.of("Test Person"), PasswordHash.of("hashed-password"));
    }
}
