# ADR-0003: Split aggregate creation into `create` and `hydrate`

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Martin

## Context

An aggregate comes into existence in two entirely different situations, which a single
constructor cannot distinguish:

1. **Something happens in the business.** A user opens an account, sets a goal, creates a
   group. This is the moment a rule applies ("a new account may not be archived", "a goal's
   end date must be after its start date"), an identity is assigned, and — once we add
   them — a lifecycle event is raised.
2. **We read stored state back.** The persistence adapter rebuilds an aggregate that
   already exists. No business event is happening; it happened months ago. Re-running
   creation logic here would raise a duplicate event, reset derived state, or reject an
   object that is legitimately in a state today's creation rules would not allow.

With one constructor these paths are indistinguishable, and the difference tends to be
papered over with nullable parameters, boolean flags, or "if id == null then it's new".

## Decision

Aggregates have **no public constructors**. They are created through exactly two named
paths:

- `static X create(...)` — a genuine new instance. Applies creation-time business rules,
  assigns identity, sets initial state, and is the single place a future creation event
  would be recorded.
- `static X hydrate(...)` — reconstitution from stored state. Accepts the aggregate exactly
  as stored, checks structural validity only (no nulls, consistent fields), records no
  events, applies no defaults and no timestamps.

`hydrate` is called **only by persistence mappers**. An ArchUnit test enforces this: no
class outside `..adapter.out.persistence..` may call a `hydrate` method. Tests build
aggregates with `create` or a test builder that uses it; only persistence-mapping tests use
`hydrate`.

## Alternatives considered

- **A single constructor or factory for both paths** — fewer concepts. Rejected: it is
  exactly the ambiguity that makes lifecycle events, defaults and identity assignment
  unsafe to add later.
- **A separate reconstitution class or interface per aggregate** — more explicit still, but
  more machinery than a small project needs.
- **Package-private `hydrate`** — would enforce the restriction by the compiler, but the
  mapper lives in the adapter package by design. ArchUnit is the trade-off.

## Consequences

- The two paths must stay in sync as fields are added; forgetting one is a compile error,
  which is the point of naming them.
- `hydrate` deliberately accepts states `create` would reject — an archived account, a
  goal that ended last year. That asymmetry is intended and should be stated in a comment
  where it is not obvious.
- Adding domain events later is a local change to `create`, with no risk of firing them
  while reading from the database.
- Slightly more code per aggregate, and a rule contributors will not guess on their own —
  so it is listed in `AGENTS.md` and in
  [guidelines/domain-modelling.md](../guidelines/domain-modelling.md), not only here.
