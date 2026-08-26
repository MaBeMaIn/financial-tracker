# ADR-0002: The domain stays free of JPA annotations

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Martin

## Context

[ADR-0001](0001-hexagonal-architecture-with-use-case-slices.md) puts a framework-free
domain at the centre. The cheapest way to persist that domain is to annotate the
aggregates themselves with `@Entity` and let Hibernate map them directly — but doing so
makes the domain model answer to Hibernate as well as to the business.

Hibernate's requirements are not neutral. Mapped classes need a no-argument constructor
and mutable fields, which conflicts with enforcing invariants in constructors; entity
identity and equality follow JPA's rules rather than the domain's; and lazy loading means
a domain object can throw from a getter depending on whether a transaction is open.
Collections silently become proxies. None of these are visible in the domain code, which
is what makes them expensive: the model looks pure and behaves otherwise.

## Decision

The `domain` package contains plain Java only. Persistence lives in
`adapter/out/persistence` as separate JPA entity classes, with hand-written mappers
translating between them and domain objects. Outbound ports accept and return domain
types; a JPA entity never leaves the persistence adapter.

An ArchUnit test fails the build if any `jakarta.persistence` type is referenced from
`..domain..` or `..application..`.

## Alternatives considered

- **JPA annotations on aggregates** — far less code and no mapping layer. Rejected: it
  puts Hibernate's lifecycle rules inside the model the project exists to protect, and it
  quietly prevents immutable value objects and constructor-enforced invariants.
- **A mapping framework (MapStruct, ModelMapper)** — removes some boilerplate. Rejected
  for now: mapping is where the two models are reconciled, and doing it by hand keeps the
  reconciliation visible and debuggable. Revisit if mappers become genuinely repetitive.

## Consequences

- Every aggregate has a twin: a domain class and a JPA entity, plus a mapper and its
  tests. This is the accepted cost, and the main argument the next contributor will make
  against this decision.
- The domain is unit-testable with `new` and plain JUnit, with no database and no context.
- The database schema can change shape without the domain noticing, and vice versa.
- Mappers need the reconstitution path described in
  [ADR-0003](0003-split-aggregate-creation-into-create-and-hydrate.md).
- Some JPA conveniences are unavailable: no dirty checking on domain objects, so a use
  case must explicitly save what it changed. That explicitness is considered a benefit.

