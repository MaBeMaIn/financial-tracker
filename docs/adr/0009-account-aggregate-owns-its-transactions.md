# ADR-0009: The account aggregate owns its transactions

- **Status:** Accepted
- **Date:** 2026-10-31
- **Deciders:** Maria Berg

## Context

A transaction is always recorded on one account (FR-3.6), and an archived account must
refuse new transactions (invariant 2). That rule needs the account's state at the moment a
transaction is created.

## Decision

`Account` is the aggregate root for its transactions. New transactions are created only
through it (`account.deposit(...)`); `Transaction.create` is package-private. In the
database, transactions keep a table of their own (`transactions`, keyed by
`TransactionId`, with a foreign key to `accounts`). `AccountRepository` loads an account
together with its transactions and, on save, inserts the transactions not stored yet.

## Alternatives considered

- **Transaction as its own aggregate with its own repository** — the archived-account rule
  would have to live in the handler, where any other caller could skip it.
- **Transactions stored inside the account row** (serialized) — no way to query or index
  by date for balances and goal periods.

## Consequences

- The account's rules guard every transaction, and the handler only orchestrates.
- Loading an account loads all its transactions. Fine for v1 volumes; if it grows, load by
  period or page instead.
- Editing and deleting transactions (FR-3.7) must also go through the account, and the
  repository's save will need to sync changed and removed rows, not only insert new ones.

