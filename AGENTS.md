# AGENTS.md

Entry point for AI coding agents working in this repository. Keep this file short; put
depth in `docs/` and link to it from here.

## What this project is

A **financial tracking** application. Users manually register savings accounts, deposits
and withdrawals, and portfolio valuations, set savings goals, and form groups with shared
goals.

## Hard rules

1. **No money is ever moved.** The application is strictly informational. Never add
   payment, transfer, or bank-integration functionality.
2. **Transactions and portfolio value are separate concepts.** A deposit is not a
   valuation. See [docs/architecture/domain-model.md](docs/architecture/domain-model.md).
3. **The dependency rule.** `domain` and `application` contain no Spring, JPA, HTTP or
   SQL. Dependencies point inward only. See
   [docs/guidelines/architecture-principles.md](docs/guidelines/architecture-principles.md).
4. **One operation = one command or query = one handler = one public method.** Never add a
   method to a generic `*Service`; create `RecordDeposit` (inbound port),
   `RecordDepositCommand` and `RecordDepositCommandHandler`.
5. **Aggregates have no public constructors.** New instances come from `static
   create(...)`; reconstitution from storage comes from `static hydrate(...)`, called only
   by persistence mappers.
6. **No JPA in the domain.** Aggregates are plain Java; JPA entities and hand-written
   mappers live in the `adapter-persistence` module.
7. **Schema changes only via Flyway migrations** (in `adapter-persistence`), and
   migrations are append-only.
8. **Money is `BigDecimal` + an ISO-4217 currency code**, wrapped in `Money`. Never
   `double`/`float`.
9. Update the relevant document in `docs/` in the same change that alters behaviour.

## Stack

- Java 26, Spring Boot 4.1 (Web MVC, Data JPA, Flyway) — in the adapter and app modules
  only
- H2 in development; Maven wrapper (`./mvnw`)

## Commands

```bash
./mvnw spring-boot:run     # run the application
./mvnw test                # run tests
./mvnw verify              # full build
```

## Layout

The build is Maven multi-module (ADR-0005). Slices are packages in `core`; adapters are
their own modules:

```
core                  feature slices: user, account, goal, group, common — no framework
adapter-web           REST controllers, DTOs                     → depends on core
adapter-persistence   JPA entities, repositories, mappers        → depends on core
app                   Spring Boot application, config, wiring    → depends on all
docs/                 project documentation (see docs/README.md)
```

The modules are not created yet; the code is still one module under
`src/main/java/se/financial_tracker/`. Flyway migrations belong to `adapter-persistence`
once it exists.

Each slice is a hexagon:

```
<slice>/domain      aggregates, value objects, invariants
<slice>/commands    XCommand + XCommandHandler   (writes)
<slice>/queries     XQuery + XQueryHandler       (reads)
<slice>/port/in     interfaces the handlers implement
<slice>/port/out    interfaces the adapters implement
```

Only `user` and `common` exist so far. Never add Spring, JPA or web imports to a slice —
they belong in an adapter module.

## Documentation map

|                Question                |                                         Document                                         |
|----------------------------------------|------------------------------------------------------------------------------------------|
| Why does this exist, who is it for?    | [docs/product/vision.md](docs/product/vision.md)                                         |
| What must it do?                       | [docs/product/requirements.md](docs/product/requirements.md)                             |
| What do these words mean?              | [docs/product/glossary.md](docs/product/glossary.md)                                     |
| How is it built?                       | [docs/architecture/overview.md](docs/architecture/overview.md)                           |
| What are the entities and their rules? | [docs/architecture/domain-model.md](docs/architecture/domain-model.md)                   |
| How do I write code here?              | [docs/guidelines/architecture-principles.md](docs/guidelines/architecture-principles.md) |
| How do I model the domain?             | [docs/guidelines/domain-modelling.md](docs/guidelines/domain-modelling.md)               |
| Conventions, tests, git                | [docs/guidelines/](docs/guidelines/)                                                     |
| Why is it like this?                   | [docs/adr/](docs/adr/)                                                                   |

## Working conventions

- Use the vocabulary in the glossary in code, tests, APIs and commit messages.
- Reference requirement numbers (`FR-3.10`) in commits and pull requests.
- Prefer small, single-purpose documents; if a doc passes ~200 lines, split it.
- Diagrams are Mermaid, so they stay readable as text.
- If a requirement is ambiguous, ask rather than inventing one; requirements live in
  `docs/product/requirements.md` and are the source of truth. **TODO** markers in docs
  mean a human decision is pending — do not resolve them silently.
- A non-obvious decision made while coding gets an ADR (copy
  [docs/adr/0000-template.md](docs/adr/0000-template.md)).

