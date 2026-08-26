# ADR-0005: Adapters live in separate Maven modules

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Martin

## Context

[ADR-0001](0001-hexagonal-architecture-with-use-case-slices.md) and
[ADR-0004](0004-command-and-query-handlers-behind-inbound-ports.md) settled the inside of
the hexagon but left adapters homeless: either packages within each slice
(`user/adapter/{in/web,out/persistence}`) or separate Maven modules depending on a core
module. Nothing built so far forces the choice, because both sit behind the same ports.

The dependency rule is the single most important structural rule in this codebase, and in
a single module nothing stops a contributor — or an AI tool — from importing
`jakarta.persistence` into a domain class. An ArchUnit test catches it, but only once
someone runs the tests, and only for the patterns the test happens to describe.

## Decision

The build is split into Maven modules. The core module contains the feature slices —
`domain`, `commands`, `queries`, `port/{in,out}` — and does not depend on Spring, JPA or
any web library. Each adapter is its own module depending on core, and a thin application
module assembles them and runs Spring Boot.

The starting set, adjustable when the modules are actually created:

|        Module         |                       Contains                       |     Depends on      |
|-----------------------|------------------------------------------------------|---------------------|
| `core`                | all feature slices: domain, commands, queries, ports | nothing but the JDK |
| `adapter-web`         | REST controllers, request/response DTOs              | `core`              |
| `adapter-persistence` | JPA entities, Spring Data repositories, mappers      | `core`              |
| `app`                 | Spring Boot application, configuration, bean wiring  | all of the above    |

Slices are packages inside `core`, not modules of their own. Splitting per slice would
multiply modules without protecting anything the compiler cannot already see.

## Alternatives considered

- **Adapter packages inside each slice, single module** — simplest build, everything for
  one feature in one place, and an easy `./mvnw` story. Rejected: it leaves the dependency
  rule as a convention enforced only by a test.
- **Spring Modulith** — enforces module boundaries within a single Maven module, with less
  build ceremony. Rejected for the core/adapter split: it is a runtime and test-time
  check, not a compile-time one. Still a candidate for slice-to-slice isolation (see the
  open decisions in the guidelines).

## Consequences

- **The dependency rule becomes a compile error.** `core` has no Spring, JPA or servlet
  dependency on its classpath, so a domain class *cannot* import `@Entity` — the mistake
  is impossible rather than merely detectable. This is the whole point of the decision.
- The ArchUnit test shrinks to what the compiler still cannot see: slice-to-slice
  isolation, `hydrate` restricted to persistence mappers, handlers implementing an inbound
  port, and aggregates without public constructors.
- Build ceremony: a parent pom, four modules, and a rebuild of `core` before an adapter
  picks up a change. IDE navigation gains a step. For a learning project this is a real
  cost, and the honest counter-argument to this decision.
- A feature no longer lives in one folder: adding an endpoint touches `core` and
  `adapter-web`, and the reviewer follows a port between them.
- Test placement follows the module: domain and handler tests in `core` with no Spring on
  the classpath (which is itself a useful guarantee), adapter tests in their own module.
- Moving here later would have been cheap, since ports were already the seam — but doing
  it before the first adapter exists means no migration at all.

