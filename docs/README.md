# Documentation

This folder is the shared context for both human collaborators and AI tools.

## Structure

- **product/** — what we are building and why. Changes when the product changes.
  - [vision.md](product/vision.md) — purpose, users, non-goals
  - [requirements.md](product/requirements.md) — functional and non-functional
    requirements
  - [glossary.md](product/glossary.md) — the shared vocabulary
- **architecture/** — how it is built *today*. Always describes the current state.
  - [overview.md](architecture/overview.md) — stack, shape, structure
  - [domain-model.md](architecture/domain-model.md) — entities, relations, invariants
- **guidelines/** — how we write code. The contributor contract.
  - [architecture-principles.md](guidelines/architecture-principles.md) — hexagonal,
    use-case slicing, SOLID
  - [domain-modelling.md](guidelines/domain-modelling.md) — aggregates, value objects,
    `create`/`hydrate`
  - [coding-standards.md](guidelines/coding-standards.md)
  - [testing-strategy.md](guidelines/testing-strategy.md)
  - [git-workflow.md](guidelines/git-workflow.md)
- **adr/** — [architecture decision records](adr/): why things are the way they are.

Planned as the project grows: `architecture/api.md`, `architecture/security.md`, `ops/`
(environments, deployment, runbook).

## Conventions

- Every document starts with front-matter: `status`, `owner`, `last-updated`.
- One topic per file, ideally under ~200 lines.
- Link instead of duplicating: every fact has exactly one home.
- Diagrams are Mermaid code blocks.
- Requirements are numbered (`FR-x`, `NFR-x`) so code, tests and discussions can reference
  them.
- Current state goes in `architecture/`; the reasoning and the rejected alternatives go in
  an ADR. Never write history into an architecture document.
- **TODO** in a document means a team decision is pending — not a task someone forgot.

