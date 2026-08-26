---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Coding standards

Structure is covered in [architecture-principles.md](architecture-principles.md); this
document is about the code itself. When in doubt, follow the surrounding code, and prefer
boring and obvious over clever.

## Language level

Java 26. Use the modern constructs where they make code plainer:

- **Records** for value objects, commands, results and read models.
- **Sealed interfaces + pattern matching** for closed sets of alternatives (`sealed
  interface GoalProgress permits Met, Missed, NotStarted`), instead of enums plus `switch`
  on a type field.
- **`var`** where the type is obvious from the right-hand side, not to hide it.
- No Lombok. Records and constructor injection cover the cases it was invented for.

## Naming

- Use glossary words exactly: `Valuation`, not `Snapshot` or `PortfolioValue`.
- Inbound ports are imperative verb phrases: `RecordDeposit`, `ArchiveAccount`, `GetUser`.
  Their command/query and handler add the suffix: `RecordDepositCommand`,
  `RecordDepositCommandHandler`. The single public method is `handle(...)`.
- Aggregate factories are always `create(...)` and `hydrate(...)` — never `of`, `from`,
  `newInstance` or a public constructor (see [domain-modelling.md](domain-modelling.md)).
  Value objects, which have no lifecycle, use `of(...)`: `Username.of("martin")`.
- Outbound ports are named for the need in domain terms — `UserRepository`, `Accounts`,
  `Clock` — never after the technology behind them.
- Booleans read as predicates: `archived`, `isOwnedBy(...)`.
- No abbreviations (`acc`, `txn`, `val`) and no Hungarian prefixes/suffixes on interfaces
  (`IAccount`, `AccountImpl`).
- Test methods say the rule in words: `deposit_on_archived_account_is_rejected()`.

## Nulls and Optional

- `null` never crosses a method boundary you own. Validate in constructors.
- `Optional` as a **return type** only — never a field, parameter or collection element.
- Empty collections, never `null` collections.

## Money and time

- All monetary values are `Money` (a `BigDecimal` amount plus an ISO-4217 currency).
  `BigDecimal` alone appears only inside `Money`.
- Arithmetic on `Money` fails fast on mixed currencies.
- Comparison uses `compareTo`, never `equals`, on `BigDecimal`.
- Dates a user chose are `LocalDate` and mean a calendar day in the user's terms;
  timestamps the system generated are `Instant` in UTC.
- Current time comes from an injected clock port, never `LocalDate.now()`.

## Errors

- Domain rule violations throw domain exceptions named after the rule (`AccountArchived`,
  `DuplicateValuationForDate`), extending a common `DomainException`.
- Not-found is a domain exception too (`AccountNotFound`) — the domain never mentions
  HTTP.
- A single `@RestControllerAdvice` maps exception types to status codes and a consistent
  error body. No `try/catch` translating errors inside handlers.
- Never catch an exception to log it and rethrow, and never catch `Exception`.

## Spring usage

- Constructor injection, always. No `@Autowired` on fields.
- No Spring annotations in `domain`, `commands`, `queries` or `port`, except
  `@Transactional` on handler classes (see architecture principles §6).
- Configuration values are bound to `@ConfigurationProperties` records, not scattered
  `@Value` annotations.
- `application.yaml` per profile; no secrets in the repository, ever.

## Persistence

- Persistence code lives in the `adapter-persistence` module — JPA entities, Spring Data
  repositories, mappers and Flyway migrations. None of it appears in `core`.
- Every schema change is a Flyway migration under that module's
  `src/main/resources/db/migration`, named `V<n>__snake_case_description.sql`.
- **Migrations are append-only.** Never edit a migration that has been merged; write a new
  one.
- `spring.jpa.hibernate.ddl-auto` is `validate` (or `none`). Never `update`.
- Table and column names are `snake_case`. Watch out for SQL reserved words — the group
  concept needs a table name such as `saving_group`, not `group`.
- Indexes are added in the same migration as the query that needs them.

## API conventions

Until `docs/architecture/api.md` exists:

- Resources are plural nouns: `/api/accounts`, `/api/accounts/{id}/transactions`.
- JSON is `camelCase`; dates are ISO-8601; money is an object `{"amount": "1000.00",
  "currency": "SEK"}` with the amount as a string to avoid float rounding in clients.
- Request and response DTOs live in the web adapter and are separate from commands and
  results. They may not be reused across endpoints just because the fields match today.

## Formatting and hygiene

- Formatting is not a matter of taste here: Spotless runs
  [palantir-java-format](https://github.com/palantir/palantir-java-format) over the Java
  sources, and `spotless:check` is bound to `verify`, so unformatted code fails the build
  ([ADR-0006](../adr/0006-formatting-with-spotless-and-palantir-java-format.md)).

  ```
  ./mvnw spotless:apply    # reformat everything
  ./mvnw spotless:check    # what the build does
  ```

  Install the *palantir-java-format* IntelliJ plugin and enable it for this project, so
  the IDE and the build agree and the check never fires. The formatter has no settings —
  four-space indent, 120 columns — so there is nothing to configure and nothing to debate.
  Spotless also strips unused imports, fixes import order, and reflows the markdown under
  `docs/` to 90 columns.

- 120-column limit, enforced by the formatter; one statement per line.

- No commented-out code, no `TODO` without a name and an issue reference.

- No unused dependencies in `pom.xml`; no `System.out.println` — use a logger.

- Log at the adapter boundary, not inside the domain. Never log personal financial data.

