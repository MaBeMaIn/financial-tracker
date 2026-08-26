# ADR-0001: Hexagonal architecture with use-case slices

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Martin

## Context

financial-tracker's value is in its rules, not its plumbing: how a savings balance is
derived, why a valuation is not a deposit, when a goal period counts as met, who in a
group may see what. Those rules must stay clear and testable while several collaborators
— and AI tools — work on the code in parallel.

The first draft of the architecture used conventional Spring layering
(controller → service → repository) on the grounds that it is familiar. That structure
tends to produce services that accumulate a method per endpoint and a domain model that is
only data, which puts the rules in the least testable place and makes parallel work collide
in large shared classes.

## Decision

We structure the code as a hexagon: a framework-free domain, an application layer of
named use cases behind inbound ports, and adapters implementing outbound ports. Code is
sliced by feature first (`account`, `goal`, `group`, `user`), with the same hexagon shape
inside each slice. DDD tactical patterns (aggregates, value objects, invariants in
constructors) apply to the domain; SOLID guides the seams, with dependency inversion as
the load-bearing principle.

The details are in
[guidelines/architecture-principles.md](../guidelines/architecture-principles.md).

## Alternatives considered

- **Conventional three-layer architecture** — simplest to start, familiar to everyone.
  Rejected because it puts rules in services over an anemic model, which is exactly what
  this domain cannot afford, and because slices collide in shared service classes.
- **Full CQRS with separate read and write models** — good fit for the dashboard, but
  disproportionate to the project's size. We keep a narrow escape hatch instead: read-only
  query ports returning read models.
- **Modular monolith with enforced Maven modules** — the boundaries are not settled enough
  yet. Revisit once the slices are stable; ArchUnit enforces the rule in the meantime.

## Consequences

- Rules become unit-testable without Spring, and the list of use case classes documents
  what the application does.
- More classes and explicit mapping: a single "record a deposit" feature touches a port, a
  command, a use case, an aggregate method and a mapper. For a learning project this is a
  real cost, and partly the point — but it must not become ceremony for its own sake.
- Contributors need to learn the pattern before their first change; the guidelines carry
  that burden and must stay readable.
- Two decisions are deferred: whether the domain stays free of JPA annotations, and how
  use case beans are wired. Both are listed as open in the guidelines and will get their
  own ADRs.
- `docs/architecture/overview.md` is rewritten to match.
