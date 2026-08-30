---
status: draft
owner: martin
last-updated: 2026-08-29
---

# Testing strategy

The hexagon gives us a natural test pyramid: the interesting rules sit in the middle,
where tests are fastest.

## Levels

|         Level          |                     What it covers                      |                                                How                                                 |                Speed                 |
|------------------------|---------------------------------------------------------|----------------------------------------------------------------------------------------------------|--------------------------------------|
| **Domain tests**       | Aggregates, value objects, invariants, calculations     | Plain JUnit 5, `new`, no Spring                                                                    | Milliseconds — the bulk of our tests |
| **Handler tests**      | Orchestration and authorization                         | Handler constructed directly, outbound ports replaced by hand-written fakes                        | Milliseconds                         |
| **Adapter tests**      | Persistence mapping, HTTP contract                      | In the adapter's own module — `@DataJpaTest` with Flyway, `@WebMvcTest` with a mocked inbound port | Seconds                              |
| **Architecture tests** | Slice isolation and the conventions the compiler misses | ArchUnit, one test class in `core`                                                                 | Milliseconds                         |
| **End-to-end tests**   | A few critical journeys                                 | `@SpringBootTest` with a real HTTP call, in `app`                                                  | Slow — keep to a handful             |

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

The core/adapter dependency rule is enforced by the build itself: `core` has no framework
on its classpath ([ADR-0005](../adr/0005-adapters-as-separate-maven-modules.md)). One
ArchUnit test in `core` covers what the compiler cannot see:

- `..domain..` depends on no other project package
- No slice depends on another slice's `domain`, `commands` or `queries` — only on its
  `port.in`
- Every class in `..commands..` or `..queries..` ending in `Handler` implements an
  interface from its slice's `port.in`
- Only classes in `..adapter.out.persistence..` call a `hydrate` method
  ([ADR-0003](../adr/0003-split-aggregate-creation-into-create-and-hydrate.md))
- Aggregates declare no public constructors

## Conventions

- Arrange / Act / Assert structure, separated by blank lines, with each phase marked by an
  `// Arrange`, `// Act` and `// Assert` comment. The comments are part of the convention,
  not clutter: they make the phase boundaries survive editing, and a test that cannot be
  split into three named phases is usually testing more than one thing.

  ```java
  @Test
  void user_with_an_existing_username_is_rejected() {
      // Arrange
      Username username = Username.of("martin");
      when(repository.exsistsByUsername(username)).thenReturn(true);

      // Act + Assert
      assertThatThrownBy(() -> commandHandler.handle(command))
              .isInstanceOf(UserException.class);
  }
  ```

  Every test carries them, the one-liners included: a value-object test that fits on a
  single line is usually hiding its phases inside nested calls, and naming the input and
  the result in local variables is what makes the three phases visible.

  When the act happens *inside* the assertion and cannot be pulled out — anything built on
  `assertThatThrownBy` or `assertThatExceptionOfType`, and the equality or comparison
  checks where the call under test *is* the assertion — the two phases share one `// Act +
  Assert`. That is the only permitted merge. A test with an empty body (`contextLoads`)
  has no phases to label.

- One assertion *concept* per test.

- **AssertJ only.** Every assertion goes through `assertThat(...)`; JUnit's
  `assertEquals`, `assertTrue` and `assertThrows` are not used, so a test reads one way
  throughout and the failure messages describe the value, not just the mismatch.

  ```java
  assertThat(Username.of("  martin  ").value()).isEqualTo("martin");
  assertThat(SampleId.of(A)).isEqualTo(SampleId.of(A)).hasSameHashCodeAs(SampleId.of(A));
  assertThat(ids).hasSize(2);
  assertThat(SampleId.of(A)).isLessThan(SampleId.of(B));

  assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> Username.of(""))
          .withMessageContaining("Username");
  ```

  Prefer the assertion that states the intent — `hasToString`, `hasSameHashCodeAs`,
  `isEqualByComparingTo`, `containsExactly` — over unpacking the object and comparing
  fields yourself. The snippets above show assertion *style*; inside a real test they
  still sit under an `// Assert` (or `// Act + Assert`) comment.

- Test data via small builders or factory methods (`anAccount().archived()`) that build
  through `create`, so a test states only what matters to it. Never reach for `hydrate` to
  fabricate a state the domain would refuse — if a test needs it, either the rule or the
  test is wrong. Persistence-mapping tests are the exception.

- Tests are deterministic: fixed clock, fixed ids, no `random`, no sleeping.

- A bug fix starts with a failing test that reproduces it.

- **TODO** — decide whether to enforce a coverage threshold in the build, and what to do
  about generated/DTO code if we do.

