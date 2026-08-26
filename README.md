# financial-tracker

A collaborative, **informational** financial tracking application. Users register their
own savings accounts, deposits and withdrawals, and portfolio valuations, follow savings
goals, and form groups around shared goals.

No money is handled or moved by this application — all figures are entered manually by
users.

## Status

Early development. The domain and API are still being designed; see the documentation
before writing code.

## Getting started

Requires JDK 26.

```bash
./mvnw spring-boot:run     # http://localhost:8080
./mvnw test
```

The development database is in-memory H2; schema is managed by Flyway.

## Documentation

Start at [docs/README.md](docs/README.md). AI tools should read [AGENTS.md](AGENTS.md)
first.

## Contributing

Read these before your first change:

1. [docs/product/glossary.md](docs/product/glossary.md) — the domain vocabulary is
   deliberate and used consistently in code.
2. [docs/guidelines/architecture-principles.md](docs/guidelines/architecture-principles.md)
   — hexagonal architecture, DDD, SOLID and use-case slicing, with a worked example.
3. [docs/guidelines/git-workflow.md](docs/guidelines/git-workflow.md) — branches, commits
   and the pull request checklist.

