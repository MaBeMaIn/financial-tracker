---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Git workflow

Optimised for a small group working in parallel, with AI tools producing some of the
changes. Short-lived branches, small pull requests, `main` always green.

## Branches

- `main` is protected and always deployable. No direct pushes.
- One branch per change, off `main`, named `<type>/<short-description>`:
  `feat/record-deposit`, `fix/goal-period-boundary`, `docs/api-conventions`.
- Rebase on `main` rather than merging it in, so history stays readable.
- Delete branches after merge. A branch older than a few days is a signal the change is
  too big.

## Commits

Conventional Commits, because they are machine-readable and keep the log scannable:

```
<type>(<scope>): <imperative summary>

<why the change was needed, if not obvious>
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `build`. Scope is the slice:
`account`, `goal`, `group`, `user`, `docs`.

```
feat(account): record valuations separately from transactions

A deposit is a fact the user knows; a valuation is what the market says.
Deriving one from the other would hide the difference between contribution
and return. Implements FR-3.10 to FR-3.14.
```

Reference requirement numbers (`FR-3.10`) rather than restating the requirement.

## Pull requests

Small enough to review properly — one operation or one concept.

Checklist for the author:

- [] Tests at the lowest level that expresses the rule; the suite is green
- [] Requirements referenced, or `docs/product/requirements.md` updated if they changed
- [] Documentation updated in the same pull request when behaviour or structure changed
- [] No new dependency without a note on why
- [] Follows [architecture-principles.md](architecture-principles.md) — dependency rule
  intact, no new `*Service` catch-all
- [] If a non-obvious decision was made, an ADR is included

Reviewers: at least one approval. Review for correctness, naming and whether the rule
landed in the right place — formatting is the formatter's job.

## AI-generated changes

- The author of the pull request is responsible for the code, whoever or whatever wrote
  it.
- Point the tool at [AGENTS.md](../../AGENTS.md) and the relevant docs before it starts.
- Be sceptical of large generated diffs that touch many slices; ask for the change to be
  split.

## Not yet decided

- **TODO** — squash-merge vs merge commit
- **TODO** — CI setup (GitHub Actions running `./mvnw verify` on every pull request)
- **TODO** — versioning and release process, once there is something to release

