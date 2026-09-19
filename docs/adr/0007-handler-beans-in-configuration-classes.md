# ADR-0007: Handler beans are wired in `@Configuration` classes

- **Status:** Accepted
- **Date:** 2026-08-30
- **Deciders:** Maria

## Context

How a command or query handler reaches the Spring context was deliberately left open:
`@Configuration` factories in the application module, or `@Component` on the handler
itself. The question is listed under *Postponed* in [README.md](README.md) and as an open
decision in
[guidelines/architecture-principles.md](../guidelines/architecture-principles.md), to be
settled once the first few slices existed.

The first outbound adapter settles it. Something has to hand `CreateUserCommandHandler` an
implementation of `UserRepository`, and
[ADR-0005](0005-adapters-as-separate-maven-modules.md) has already decided that `core`
carries no Spring on its classpath. That turns the question from a preference into a
consequence: `@Component` on a handler would not compile.

## Decision

Handlers are plain Java — constructor injection, no Spring annotations — and are
registered as beans by `@Configuration` classes in the `app` module, one per slice, named
`<Slice>Configuration`. The bean is typed as the inbound port, not as the handler.

```java
@Configuration
class UserConfiguration {

    @Bean
    CreateUser createUser(UserRepository users) {
        return new CreateUserCommandHandler(users);
    }
}
```

This governs the inside of the hexagon only. Adapters live in modules that already depend
on Spring, so a persistence adapter carries `@Repository` and is found by component
scanning as usual.

## Alternatives considered

- **`@Component` on handlers** — less ceremony, and no factory method to keep in step with
  a constructor. Rejected because ADR-0005 makes it impossible rather than merely
  undesirable: the annotation is not on `core`'s classpath. Adopting it would mean either
  reversing ADR-0005 or granting a second "just one annotation" exception, and the one
  already granted to `@Transactional` shows how quickly that argument is reused.
- **A single application-wide configuration class** listing every handler — one place to
  look. Rejected: it is a file that every slice has to edit, which is exactly the shape of
  the `*Service` catch-all the architecture avoids elsewhere. Per-slice configuration
  keeps a change to one slice inside that slice.

## Consequences

- The dependency rule stays a compile error rather than a convention. Nothing in `core`
  imports Spring, and the module boundary is what enforces it.
- Handlers stay constructible with `new` in a test, which is what the handler tests in
  [guidelines/testing-strategy.md](../guidelines/testing-strategy.md) already assume.
- Every new handler costs two edits — the handler and its slice configuration — and a
  constructor change has to be mirrored in the factory method. The compiler catches the
  mismatch, but the ceremony is real and is the honest argument against this decision.
- Nothing in `core` can be discovered by scanning, so a handler that nobody registers
  simply does not exist at runtime. The application context test in `app` is the safety
  net, and it earns its place the moment a second slice appears.
- Where the transaction boundary lives is *not* settled here.
  [guidelines/architecture-principles.md](../guidelines/architecture-principles.md) still
  allows `@Transactional` on a handler, which would put `spring-tx` on `core`'s classpath
  and reopen the question this ADR just closed. That tension is recorded as postponed and
  needs an ADR of its own.

