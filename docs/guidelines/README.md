---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Guidelines

The contributor contract. If a change conflicts with a guideline, either change the code
or change the guideline in the same pull request — never leave them disagreeing.

- [architecture-principles.md](architecture-principles.md) — hexagonal architecture,
  use-case slicing and SOLID, applied concretely to this codebase. **Read this first.**
- [domain-modelling.md](domain-modelling.md) — DDD inside the hexagon: aggregates, value
  objects, invariants, and the `create`/`hydrate` split
- [coding-standards.md](coding-standards.md) — Java and Spring conventions, naming, null
  handling, money
- [testing-strategy.md](testing-strategy.md) — what to test, where, and with what
- [git-workflow.md](git-workflow.md) — branches, commits, pull requests

Sections marked **TODO** need a team decision; bring them to a discussion rather than
deciding alone in a pull request.
