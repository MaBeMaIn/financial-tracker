package se.financial_tracker.account.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import se.financial_tracker.account.domain.Account;
import se.financial_tracker.account.domain.AccountId;
import se.financial_tracker.account.domain.AccountName;
import se.financial_tracker.account.domain.AccountType;
import se.financial_tracker.account.port.out.AccountRepository;
import se.financial_tracker.user.adapter.out.persistence.JpaUserRepository;
import se.financial_tracker.user.domain.DisplayName;
import se.financial_tracker.user.domain.EmailAddress;
import se.financial_tracker.user.domain.PasswordHash;
import se.financial_tracker.user.domain.User;
import se.financial_tracker.user.domain.UserId;
import se.financial_tracker.user.port.out.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Runs against the schema Flyway built, on H2. {@code replace = NONE} keeps the configured
 * datasource, so the migration is what the mapping is validated against.
 *
 * <p>The user repository is imported too: an account needs an owner that exists, and the test
 * should go through the same path the application does rather than insert rows behind its back.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAccountRepository.class, JpaUserRepository.class})
class AccountPersistenceTest {

    @Autowired
    private AccountRepository accounts;

    @Autowired
    private UserRepository users;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void a_saved_account_is_found_by_its_id() {
        // Arrange
        UserId owner = anOwner();
        Account account = Account.createAccount(AccountType.SAVINGS, AccountName.of("Buffer"), owner);

        // Act
        accounts.save(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(accounts.getByAccountId(account.accountId())).get().satisfies(found -> {
            assertThat(found.accountId()).isEqualTo(account.accountId());
            assertThat(found.owner()).isEqualTo(owner);
            assertThat(found.accountType()).isEqualTo(AccountType.SAVINGS);
            assertThat(found.accountName()).isEqualTo(AccountName.of("Buffer"));
            assertThat(found.isArchived()).isFalse();
        });
    }

    @Test
    void an_unknown_account_id_is_not_found() {
        // Arrange
        AccountId unknown = AccountId.newId();

        // Act + Assert
        assertThat(accounts.getByAccountId(unknown)).isEmpty();
    }

    @Test
    void a_new_account_is_not_archived() {
        // Arrange
        Account account = Account.createAccount(AccountType.INVESTMENT, AccountName.of("ISK"), anOwner());

        // Act
        accounts.save(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(accounts.getByAccountId(account.accountId())).get().satisfies(found -> {
            assertThat(found.isArchived()).isFalse();
        });
    }

    @Test
    void the_archived_flag_survives_a_round_trip() {
        // Arrange
        Account account = Account.createAccount(AccountType.SAVINGS, AccountName.of("Old buffer"), anOwner());
        accounts.save(account);
        entityManager.flush();

        // Act
        account.archiveAccount(account);
        accounts.save(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(accounts.getByAccountId(account.accountId())).get().satisfies(found -> {
            assertThat(found.isArchived()).isTrue();
        });
    }

    @Test
    void the_type_is_stored_as_its_name_not_its_ordinal() {
        // Arrange
        Account account = Account.createAccount(AccountType.INVESTMENT, AccountName.of("ISK"), anOwner());

        // Act
        accounts.save(account);
        entityManager.flush();

        // Assert
        Object storedType = entityManager
                .createNativeQuery("select type from accounts where id = :id")
                .setParameter("id", account.accountId().value())
                .getSingleResult();
        assertThat(storedType).hasToString("INVESTMENT");
    }

    @Test
    void two_accounts_can_share_a_name_across_users() {
        // Arrange
        AccountName sameName = AccountName.of("Buffer");

        // Act
        accounts.save(Account.createAccount(AccountType.SAVINGS, sameName, anOwner()));
        accounts.save(Account.createAccount(AccountType.SAVINGS, sameName, anOwner()));

        // Assert
        assertThatNoException().isThrownBy(entityManager::flush);
    }

    @Test
    void an_account_cannot_belong_to_an_unknown_user() {
        // Arrange
        Account orphan = Account.createAccount(AccountType.SAVINGS, AccountName.of("Nobody's"), UserId.newId());

        // Act + Assert
        // The surrogate key is generated by the database, so Hibernate inserts as soon as the
        // entity is persisted; the foreign key can therefore fail either at save or at flush.
        // Spring translates what the repository throws, so both exception families count.
        assertThatThrownBy(() -> {
                    accounts.save(orphan);
                    entityManager.flush();
                })
                .isInstanceOfAny(PersistenceException.class, DataIntegrityViolationException.class);
    }

    private UserId anOwner() {
        User user = User.create(
                EmailAddress.of("owner-" + UserId.newId().value() + "@test.se"),
                DisplayName.of("Test Person"),
                PasswordHash.of("hashed-password"));
        users.save(user);
        entityManager.flush();
        return user.userId();
    }
}
