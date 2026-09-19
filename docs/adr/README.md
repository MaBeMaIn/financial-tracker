---
status: active
owner: martin
last-updated: 2026-08-21
---

# Architecture decision records

One file per decision that was not obvious, numbered in order. ADRs are **immutable**:
when a decision changes, write a new ADR and mark the old one `Superseded by ADR-nnnn`.

This lets `docs/architecture/` describe only the present, while the reasoning stays
available for anyone — human or AI — who asks "why is it like this?".

Copy [0000-template.md](0000-template.md) to start one.

|                                 #                                 |                       Decision                       |  Status  |
|-------------------------------------------------------------------|------------------------------------------------------|----------|
| [0001](0001-hexagonal-architecture-with-use-case-slices.md)       | Hexagonal architecture with use-case slices          | Accepted |
| [0002](0002-domain-free-of-jpa-annotations.md)                    | The domain stays free of JPA annotations             | Accepted |
| [0003](0003-split-aggregate-creation-into-create-and-hydrate.md)  | Split aggregate creation into `create` and `hydrate` | Accepted |
| [0004](0004-command-and-query-handlers-behind-inbound-ports.md)   | Command and query handlers behind inbound ports      | Accepted |
| [0005](0005-adapters-as-separate-maven-modules.md)                | Adapters live in separate Maven modules              | Accepted |
| [0006](0006-formatting-with-spotless-and-palantir-java-format.md) | Formatting with Spotless and palantir-java-format    | Accepted |
| [0007](0007-handler-beans-in-configuration-classes.md)            | Handler beans are wired in `@Configuration` classes  | Accepted |

## Postponed

Decisions we have consciously deferred, so nobody quietly makes them in a pull request:

|                               Question                               |                         Why postponed                         |           Decide by           |
|----------------------------------------------------------------------|---------------------------------------------------------------|-------------------------------|
| Where the transaction boundary lives (`@Transactional` on a handler) | ADR-0007 closed the wiring question; this one would reopen it | Before a handler writes twice |

