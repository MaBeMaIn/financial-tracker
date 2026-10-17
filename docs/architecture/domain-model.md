---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Domain model

Entities, their relationships and the rules that must always hold. Vocabulary is defined
in [glossary.md](../product/glossary.md); behaviour is specified in
[requirements.md](../product/requirements.md).

## Entities

```mermaid
erDiagram
    USER ||--o{ ACCOUNT : owns
    USER ||--o{ GOAL : owns
    USER ||--o{ GROUP_MEMBERSHIP : has
    GROUP ||--o{ GROUP_MEMBERSHIP : has
    GROUP ||--o{ GOAL : owns
    ACCOUNT ||--o{ TRANSACTION : records
    ACCOUNT ||--o{ VALUATION : records
    GOAL ||--o{ GOAL_ACCOUNT : links
    ACCOUNT ||--o{ GOAL_ACCOUNT : "is linked by"

    USER {
        uuid id PK
        string email UK
        string password_hash
        string display_name
    }
    ACCOUNT {
        uuid id PK
        uuid user_id FK
        string name
        string type "SAVINGS | INVESTMENT"
        boolean archived
    }
    TRANSACTION {
        uuid id PK
        uuid account_id FK
        date occurred_on
        decimal amount "positive"
        string direction "DEPOSIT | WITHDRAWAL"
        string note
    }
    VALUATION {
        uuid id PK
        uuid account_id FK
        date valued_on
        decimal total_value
    }
    GOAL {
        uuid id PK
        uuid owner_user_id FK "null when group goal"
        uuid owner_group_id FK "null when personal goal"
        string name
        decimal target_amount
        string period "MONTHLY"
        date start_date
        date end_date "nullable"
        boolean paused
    }
    GOAL_ACCOUNT {
        uuid goal_id FK
        uuid account_id FK
    }
    GROUP {
        uuid id PK
        uuid owner_user_id FK
        string name
    }
    GROUP_MEMBERSHIP {
        uuid id PK
        uuid group_id FK
        uuid user_id FK
        date joined_on
        date left_on "nullable"
    }
```

## Invariants

These must hold at all times; each should have a test. They are enforced in aggregate
factories (`create`), never validated after the fact; see
[guidelines/domain-modelling.md](../guidelines/domain-modelling.md).

**Account**

1. An account belongs to exactly one user and never changes owner.
2. An archived account accepts no new transactions or valuations.

**Transaction**

3. `amount` is strictly positive; direction carries the sign.
4. Transactions may be back-dated, but calculations key on `occurred_on`, never on the
   creation timestamp.

**Valuation**

5. Valuations exist only on `INVESTMENT` accounts.
6. At most one valuation per (account, `valued_on`).
7. `total_value` is zero or positive.

**Goal**

8. A goal has exactly one owner: either a user or a group, never both and never neither.
9. Every account linked to a personal goal is owned by the goal's owner.
10. Every account linked to a shared goal is owned by a current member of that group.
11. `end_date`, when set, is after `start_date`.

**Group**

12. A group always has an owner, and the owner is a current member.

13. A user has at most one active membership (`left_on is null`) per group.

14. Deleting a group deletes memberships and shared goals, never accounts or transactions.

## Derived calculations

None of these are stored.

|          Value           |       Applies to       |                                          Rule                                           |
|--------------------------|------------------------|-----------------------------------------------------------------------------------------|
| **Balance**              | Savings account        | Σ deposits − Σ withdrawals with `occurred_on <= date`                                   |
| **Current value**        | Investment account     | `total_value` of the valuation with the greatest `valued_on <= date`; *unknown* if none |
| **Net contributions**    | Any account            | Σ deposits − Σ withdrawals within the period                                            |
| **Return**               | Investment account     | current value − net contributions since the account's start                             |
| **Contributed**          | Goal, per period       | Σ deposits − Σ withdrawals on linked accounts within the period                         |
| **Period met**           | Goal, per period       | contributed ≥ `target_amount`                                                           |
| **Shared goal progress** | Group goal, per period | Σ contributed across all linked accounts of all members                                 |

## Deliberate omissions

- No holdings, instruments or prices inside an investment account — only total value.
- Only one currency (SEK) — no conversion, and therefore no exchange-rate entity.
- No audit log beyond the entities themselves in v1.

