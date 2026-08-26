---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Testing strategy

The hexagon gives us a natural test pyramid: the interesting rules sit in the middle,
where tests are fastest.

## Levels

| Level | What it covers | How | Speed |
|---|---|---|---|
| **Domain tests** | Aggregates, value objects, invariants, calculations | Plain JUnit 5, `new`, no Spring | Milliseconds — the bulk of our tests |
| **Use case tests** | Orchestration and authorization | Use case constructed directly, outbound ports replaced by hand-written fakes | Milliseconds |
| **Adapter tests** | Persistence mapping, HTTP contract | Spring test slices: `@DataJpaTest` with Flyway, `@WebMvcTest` with a mocked inbound port | Seconds |
| **Architecture tests** | The dependency rule | ArchUnit, one test class in `src/test` | Milliseconds |
| **End-to-end tests** | A few critical journeys | `@SpringBootTest` with a real HTTP call | Slow — keep to a handful |

Write the test at the **lowest level that can express the rule**. A rule about withdrawals
reducing goal progress is a domain test, not an end-to-end test.

## Fakes over mocks

Outbound ports get hand-written in-memory fakes (`InMemoryAccounts`) kept in the test
sources beside the port. They are reusable, they force the port to stay narrow, and they
fail honestly when the contract is wrong — unlike a mock, which happily returns whatever
the test told it to. Mocking frameworks are for the rare awkward case, not the default.

## What must be tested

From [requirements NFR-6](../product/requirements.md#7-non-functional-requirements), these
have non-negotiable coverage including empty data and period boundaries:

- Savings balance derivation (FR-3.8)
- Investment current value and unknown-value handling (FR-3.12)
- Net contributions and return (FR-3.14)
- Goal period contribution and met/missed (FR-4.3, FR-4.5)
- Group visibility rules (FR-5.6) — including that a non-member sees nothing

## Architecture test

One ArchUnit test enforces what review would otherwise have to catch every time:

- `..domain..` depends on no other project package and on no framework
- `..application..` does not depend on `..adapter..`
- No class in `..domain..` or `..application..` is annotated with `@Entity`,
  `@RestController` or `@Repository`, and neither references `jakarta.persistence`
  ([ADR-0002](../adr/0002-domain-free-of-jpa-annotations.md))
- No slice depends on another slice's `domain`, `usecase` or `adapter` packages
- Only classes in `..adapter.out.persistence..` call a `hydrate` method
  ([ADR-0003](../adr/0003-split-aggregate-creation-into-create-and-hydrate.md))
- Aggregates declare no public constructors

## Conventions

- Given / When / Then structure, separated by blank lines. Comments only where the setup
  is genuinely obscure.
- One assertion *concept* per test; use AssertJ for readable assertions.
- Test data via small builders or factory methods (`anAccount().archived()`) that build
  through `create`, so a test states only what matters to it. Never reach for `hydrate` to
  fabricate a state the domain would refuse — if a test needs it, either the rule or the
  test is wrong. Persistence-mapping tests are the exception.
- Tests are deterministic: fixed clock, fixed ids, no `random`, no sleeping.
- A bug fix starts with a failing test that reproduces it.
- **TODO** — decide whether to enforce a coverage threshold in the build, and what to do
  about generated/DTO code if we do.
