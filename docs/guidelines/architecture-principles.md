---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Architecture principles

How we structure code. The four commitments below are deliberate and apply to every
feature; everything else in this document follows from them.

1. **Hexagonal architecture (ports and adapters)** — the domain sits in the middle and
   knows nothing about HTTP, JPA or Spring. Everything technical plugs into it.
2. **Domain-Driven Design** — the model uses the language in
   [glossary.md](../product/glossary.md), and business rules live in the domain objects,
   not in services that push data around.
3. **SOLID** — see [§5](#5-solid-applied-here) for what each letter means *here*.
4. **Use-case-driven slicing** — the application layer is a set of named use cases
   (`RecordDeposit`, `RecordValuation`, `EvaluateGoalPeriod`), not generic
   `AccountService` classes that accumulate every operation touching an account.

## 1. The dependency rule

Dependencies point **inward only**. The domain depends on nothing. The application layer
depends on the domain. Adapters depend on the application layer's ports. Nothing inside
ever imports anything outside.

```mermaid
flowchart LR
    WEB["Web adapter<br/>REST controllers"] -->|calls| IN["Inbound port<br/>RecordDeposit"]
    IN --- APP
    APP["Application<br/>use case"] --> DOM["Domain<br/>Account, Money, rules"]
    APP -->|depends on| OUT["Outbound port<br/>Accounts"]
    PERS["Persistence adapter<br/>JPA"] -->|implements| OUT
    PERS --> DB[("Database")]
```

Concretely, in the domain and application packages you will not find: `@Entity`,
`@RestController`, `@Repository`, `javax`/`jakarta.persistence`, `HttpServletRequest`,
Jackson annotations, or SQL. `@Transactional` is the one pragmatic exception — see
[§6](#6-practices).

This rule is enforced by an ArchUnit test, not by discipline alone; see
[testing-strategy.md](testing-strategy.md).

## 2. Package structure

Slice by **feature first**, then by hexagon layer. Everything about one concept lives
together, and each slice has the same internal shape:

```
se.financial_tracker
├── account
│   ├── domain                     Account, Transaction, Valuation, invariants
│   ├── application
│   │   ├── port/in                RecordDeposit, RecordValuation (interfaces + commands)
│   │   ├── port/out               Accounts, Transactions, Valuations (interfaces)
│   │   └── usecase                RecordDepositUseCase, RecordValuationUseCase
│   └── adapter
│       ├── in/web                 AccountController, request/response DTOs
│       └── out/persistence        JPA entities, repositories, mappers
├── goal
├── group
├── user
└── common                         Money, shared value objects, error handling, config
```

Rules:

- A slice may depend on **another slice's inbound ports only** — never on its domain,
  its use case classes, or its persistence. Cross-slice access goes through a port like
  any other collaborator.
- Classes are **package-private by default**. Only ports, commands, results and domain
  types that other slices legitimately need are `public`.
- `common` holds genuinely shared types. If something is used by one slice, it belongs to
  that slice. `common` is not a junk drawer.

## 3. Use cases

**One use case = one class = one public method.** This is the heart of the slicing
commitment: the set of class names in `usecase/` is the list of things the application
can do, readable at a glance.

```java
// port/in — what the outside world may ask for
public interface RecordDeposit {
    TransactionId handle(RecordDepositCommand command);
}

public record RecordDepositCommand(
        AccountId accountId,
        UserId requestedBy,
        Money amount,
        LocalDate occurredOn,
        String note) {
    public RecordDepositCommand {
        requireNonNull(accountId, "accountId");
        if (amount.isNotPositive()) throw new IllegalArgumentException("amount must be positive");
    }
}

// usecase — the orchestration, and only the orchestration
class RecordDepositUseCase implements RecordDeposit {

    private final Accounts accounts;          // outbound port
    private final Transactions transactions;  // outbound port

    RecordDepositUseCase(Accounts accounts, Transactions transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Override
    public TransactionId handle(RecordDepositCommand command) {
        Account account = accounts.byId(command.accountId())
                .orElseThrow(() -> new AccountNotFound(command.accountId()));

        account.assertOwnedBy(command.requestedBy());     // authorization is a domain rule
        Transaction deposit = account.deposit(command.amount(), command.occurredOn(), command.note());

        return transactions.save(deposit).id();
    }
}
```

Conventions:

- Name a use case after **what the user does**, in the imperative: `RecordDeposit`,
  `ArchiveAccount`, `InviteMemberToGroup`, `EvaluateGoalPeriod`. Never
  `AccountManagementService`.
- Input is a **command** (write) or a **query** (read) record, validated in its compact
  constructor. Output is a plain record or an identifier — never a JPA entity, never a DTO
  shaped for one screen.
- The use case **orchestrates**: load, delegate to the domain, save. Business rules that
  are visible in the code of a use case usually belong in an aggregate instead.
- Read-heavy screens may bypass the domain with a dedicated query port returning a
  read-model record. This is intentional (CQRS-lite): it keeps dashboards fast without
  distorting the domain model.

## 4. Ports

| | Inbound (driving) | Outbound (driven) |
|---|---|---|
| Who calls | Web adapter, scheduler, tests | The use case |
| Who implements | The use case | Persistence adapter, clock, etc. |
| Lives in | `application/port/in` | `application/port/out` |
| Named after | The action: `RecordDeposit` | The need, in domain terms: `Accounts`, `Transactions`, `Clock` |

- Ports are owned by the **inside**. An outbound port describes what the domain needs, in
  the domain's language — not what JPA offers. `Accounts.byId(...)`, not
  `AccountJpaRepository.findById(...)`.
- Keep ports **narrow** (Interface Segregation). A use case that only reads accounts
  should not be able to delete them. Several small ports beat one repository interface
  with twenty methods.
- Ports return **domain types**, never persistence entities.

Aggregates, value objects, invariants and the `create`/`hydrate` split are covered in
[domain-modelling.md](domain-modelling.md).

## 5. SOLID applied here

| | Meaning in this codebase |
|---|---|
| **S**ingle responsibility | One use case class does one thing. A class needing "and" in its description is two classes. |
| **O**pen/closed | New behaviour arrives as a new use case or a new adapter, not as another `if` branch in an existing one. |
| **L**iskov substitution | Any adapter implementing a port must honour its contract — including a fake used in tests. If the fake needs to lie, the port is wrong. |
| **I**nterface segregation | Narrow ports (§4). No "God repository". |
| **D**ependency inversion | The inside defines the interfaces; the outside implements them. This *is* the hexagon — DIP is the principle the whole architecture rests on. |

## 6. Practices

- **Constructor injection only.** No `@Autowired` fields, no field or setter injection.
  Domain and use case classes must be constructible in a test with `new`.
- **Wire in configuration, not annotations.** Use case classes are registered as beans in a
  Spring `@Configuration` class inside the slice's adapter or a `config` package, so the
  application layer stays free of Spring imports. (A `@Component` on a use case is the
  pragmatic alternative. **This decision is postponed** — see
  [§8](#8-decisions-still-open) — so match the existing slices rather than choosing anew.)
- **Transaction boundary is the use case.** `@Transactional` sits on the use case class.
  This is the one Spring annotation allowed inward, and it is a conscious trade-off.
- **Immutability by default.** Records for commands, results and value objects. Mutable
  state only inside aggregates, and only where the domain genuinely changes.
- **Validation twice, for different reasons.** The web adapter rejects malformed input
  (missing field, unparseable date). The domain rejects invalid *business* states
  (deposit on an archived account). Neither replaces the other.
- **Time is a port.** Never call `LocalDate.now()` in domain or use case code; inject a
  clock. Period-boundary bugs are otherwise untestable.
- **The domain has no JPA.** Aggregates are plain Java; separate JPA entities live in
  `adapter/out/persistence` ([ADR-0002](../adr/0002-domain-free-of-jpa-annotations.md)).
  There is no dirty checking on domain objects, so a use case explicitly saves what it
  changed.
- **Mapping is explicit.** Hand-written mappers between JPA entities and domain objects.
  No mapping framework, no shared class doing double duty. Mappers use `hydrate` on the way
  in and read state on the way out.
- **Derived values are computed, not stored** (see
  [architecture/overview.md](../architecture/overview.md)).
- **Money never touches `double`.** `Money` is `BigDecimal` + currency, and only `Money`
  crosses layer boundaries.
- **Don't abstract on speculation.** A port with exactly one implementation and no test
  fake is often just indirection. Add the seam when a second reason to change appears.

## 7. Smells we reject

- `*Service` classes that grow one method per endpoint
- JPA entities used as domain objects, DTOs, or API responses
- `Optional` fields, or returning `null` from a port
- Business rules inside controllers, mappers, or database queries
- Static access to the current time, user or transaction
- `common` or `util` packages that everything depends on
- Comments explaining what code does instead of naming things properly

## 8. Decisions still open

- **TODO — bean wiring. Postponed deliberately.** Spring `@Configuration` factories (keeps
  the inside Spring-free) vs `@Component` on use cases (less ceremony). Until this is
  decided, follow whatever the existing slices do and do not introduce a third way; revisit
  once the first few slices exist. Listed under *Postponed* in [../adr/](../adr/).
- **TODO — read models.** How far to take the CQRS-lite escape hatch in §3 before it needs
  its own structure.
- **TODO — module enforcement.** ArchUnit only, or Spring Modulith / Maven modules once the
  slices settle.
