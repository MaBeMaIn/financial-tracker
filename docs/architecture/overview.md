---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Architecture overview

Describes how the system is built **today**. When something changes, this document changes
with it — it is not a history. Reasoning behind decisions lives in [../adr/](../adr/); the
rules contributors follow live in
[../guidelines/architecture-principles.md](../guidelines/architecture-principles.md).

## Context

```mermaid
flowchart LR
    U["User<br/>(browser)"] --> A["financial-tracker<br/>Spring Boot application"]
    A --> DB[("Relational database<br/>H2 in development")]
```

There are no external systems. No bank, broker or payment provider is integrated, now or
later (see
[requirements NFR-1](../product/requirements.md#7-non-functional-requirements)).

## Technology

|   Concern   |                                       Choice                                       |
|-------------|------------------------------------------------------------------------------------|
| Language    | Java 26                                                                            |
| Framework   | Spring Boot 4.1                                                                    |
| Web         | Spring Web MVC, JSON REST endpoints                                                |
| Persistence | Spring Data JPA / Hibernate                                                        |
| Schema      | Flyway migrations, in `adapter-persistence`                                        |
| Database    | H2 in development; a server database to be decided before deployment               |
| Build       | Maven multi-module (`./mvnw`): `core`, `adapter-web`, `adapter-persistence`, `app` |
| Tests       | JUnit 5, AssertJ, Spring test slices, ArchUnit                                     |

## Shape: a hexagon per feature slice

The application is **ports and adapters**
([ADR-0001](../adr/0001-hexagonal-architecture-with-use-case-slices.md)). The domain is
plain Java with no framework dependencies; command and query handlers orchestrate it
behind inbound ports; adapters implement outbound ports.

```mermaid
flowchart TD
    WEB["Web adapter<br/>REST controllers, DTOs"] --> IN["Inbound port<br/>RecordDeposit"]
    IN --> UC["Handler<br/>RecordDepositCommandHandler"]
    UC --> DOM["Domain<br/>Account, Money, invariants"]
    UC --> OUT["Outbound port<br/>Accounts, Transactions"]
    PERS["Persistence adapter<br/>JPA entities, mappers"] --> OUT
    PERS --> DB[("Database")]
```

Dependencies point inward only, and the build enforces it: the slices live in a `core`
module with no Spring, JPA or web library on its classpath, and each adapter is a module
of its own depending on `core`
([ADR-0005](../adr/0005-adapters-as-separate-maven-modules.md)). Importing `@Entity` into
a domain class is a compile error, not a review comment. Aggregates are plain Java with
separate JPA entities in the persistence adapter
([ADR-0002](../adr/0002-domain-free-of-jpa-annotations.md)), and are built through
`create` (new) or `hydrate` (reconstitution from storage)
([ADR-0003](../adr/0003-split-aggregate-creation-into-create-and-hydrate.md)).

## Package structure

Feature first, hexagon inside:

```
se.financial_tracker
├── user         registration, authentication, profile
├── account      accounts, transactions, valuations        (not built yet)
├── goal         goals and progress calculation            (not built yet)
├── group        groups, memberships, shared goals         (not built yet)
└── common       Money, TypedString primitives, shared value objects
```

Each slice contains `domain`, `commands`, `queries` and `port/{in,out}`. Writes are
command handlers, reads are query handlers, and each implements an inbound port
([ADR-0004](../adr/0004-command-and-query-handlers-behind-inbound-ports.md)). Slices talk
to each other through inbound ports only. The full rules, with a worked example, are in
[../guidelines/architecture-principles.md](../guidelines/architecture-principles.md).

The slices are packages in `core`. Adapters are separate modules:

```
core                  the feature slices                              (no framework)
adapter-web           REST controllers, request/response DTOs         → core
adapter-persistence   JPA entities, Spring Data repositories, mappers → core
app                   Spring Boot application, configuration, wiring  → all of the above
```

The modules do not exist yet — the project is still a single module — but no adapter has
been written, so adopting the split costs nothing beyond creating the poms.

## Derived values

Balances, current values, net contributions, returns and goal progress are **never
stored** — they are computed from transactions and valuations on read
([NFR-4](../product/requirements.md#7-non-functional-requirements)). This keeps stored
data free of figures that can drift out of sync with the facts they came from. If reads
become slow, caching is added deliberately and documented here; the calculation stays the
source of truth.

## Money

Amounts are a `Money` value object — `BigDecimal` plus an ISO-4217 currency code — living
in `common`. Stored as `DECIMAL(19,4)` plus a `CHAR(3)` currency column. Each account has
one currency; the application never converts between currencies.

## Not yet decided

- Authentication mechanism (session vs token) and whether Spring Security is introduced
  now
- How handler beans are wired: Spring `@Configuration` factories vs `@Component`
  (postponed deliberately — see architecture principles §8)
- Production database and hosting
- Whether the front end is server-rendered or a separate client application

Each becomes an ADR once settled.
