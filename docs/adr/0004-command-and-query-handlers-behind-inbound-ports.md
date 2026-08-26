# ADR-0004: Command and query handlers behind inbound ports

- **Status:** Accepted
- **Date:** 2026-08-21
- **Deciders:** Martin

## Context

[ADR-0001](0001-hexagonal-architecture-with-use-case-slices.md) committed to use-case
slicing but left the shape of the application layer described as
`application/usecase/RecordDepositUseCase`. The first slice built in anger — `user` — came
out differently: `user/commands/CreateUserCommandHandler`, with `user/port/out` beside it
and no `application` package wrapping them.

That layout is flatter and reads better: the packages inside a slice are `domain`,
`commands`, `queries` and `port`, which is the vocabulary the team actually uses when
talking about the work.

## Decision

Each slice contains:

```
<slice>/domain           aggregates, value objects, invariants
<slice>/commands         XCommand + XCommandHandler   (writes)
<slice>/queries          XQuery + XQueryHandler       (reads)
<slice>/port/in          the interfaces handlers implement
<slice>/port/out         the interfaces adapters implement
```

Writes and reads are separated at the package level. Every handler implements a small
inbound port named after the operation (`CreateUser`), so adapters and other slices depend
on an interface rather than on the handler class. One operation = one command or query =
one handler = one public method, `handle(...)`.

## Alternatives considered

- **Handlers with no inbound port** — one class fewer per operation, and the interface has
  exactly one implementation. Rejected: adapters would depend directly on handlers, and
  the test fake for a cross-slice call would have nothing to implement. The interface is
  also where the operation's contract is documented.
- **`application/usecase` with a `UseCase` suffix** (as originally documented) — no
  better, and one package level deeper. Rejected in favour of the layout that emerged in
  the code.
- **Commands and queries sharing one package** — rejected: the read side is allowed to
  bypass the domain and the write side is not, and that difference should be visible in
  the structure.

## Consequences

- Refines ADR-0001; it does not supersede it. The hexagon, the dependency rule and
  feature-first slicing are unchanged — only the names and depth of the packages inside a
  slice.
- Every operation costs three files: port, command/query, handler. For trivial reads this
  will feel like ceremony, and the query side is where we should watch for it.
- The read side has an explicit escape hatch: a query handler may return read models
  straight from a query port without loading an aggregate. Commands never may.
- Adapters still have no home. That decision is postponed and does not block anything,
  because both candidate layouts sit behind the same ports.

