---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Requirements

Source of truth for what the application must do. Requirements are numbered so that
code, tests, issues and discussions can reference them (`FR-3.2`, `NFR-4`).

Terms used here are defined in [glossary.md](glossary.md).

## 1. Actors

| Actor | Description |
|---|---|
| **User** | A registered person who tracks their own accounts, transactions and goals. |
| **Group member** | A user who has joined a group. Every group member is a user. |
| **Group owner** | The user who created a group; can invite, remove members and delete the group. |

There is no administrator role in the first version.

## 2. Accounts and identity

- **FR-2.1** A person can register a user account with an email address and a password.
- **FR-2.2** A user can log in and log out.
- **FR-2.3** A user can view and edit their display name.
- **FR-2.4** A user can delete their user account, which removes their financial data.

## 3. Financial accounts

A *financial account* is a user's record of somewhere their money sits. Two kinds exist,
and they are tracked differently.

- **FR-3.1** A user can create, rename, archive and delete financial accounts.
- **FR-3.2** Each financial account has a **type**: `SAVINGS` or `INVESTMENT`.
- **FR-3.3** Each financial account has a single currency (ISO-4217), set at creation.
- **FR-3.4** An account belongs to exactly one user. Accounts are never shared or merged.
- **FR-3.5** Archived accounts remain visible in history but cannot receive new entries.

### 3a. Transactions (both account types)

A **transaction** is money the user moved *into* or *out of* an account. It is a fact the
user knows exactly.

- **FR-3.6** A user can record a transaction on any account with: date, amount,
  direction (`DEPOSIT` or `WITHDRAWAL`), and an optional note.
- **FR-3.7** A user can edit and delete their own transactions.
- **FR-3.8** For a `SAVINGS` account, the **balance** is derived as the sum of deposits
  minus withdrawals up to a given date. It is never entered directly.
- **FR-3.9** Transactions may be dated in the past; the derived history must reflect the
  transaction date, not the entry date.

### 3b. Valuations (investment accounts only)

A **valuation** is a dated snapshot of what an `INVESTMENT` account was *worth* at that
moment. Its value moves with the market, so it cannot be derived from transactions.

- **FR-3.10** A user can record a valuation on an `INVESTMENT` account with a date and a
  total value.
- **FR-3.11** At most one valuation per account per date; recording a second one for the
  same date replaces it.
- **FR-3.12** The **current value** of an investment account is its most recent valuation.
  If none exists, the account has no known value (it is *not* zero, and *not* the sum of
  transactions).
- **FR-3.13** Valuations and transactions are stored and displayed separately. A deposit
  never changes a valuation, and a valuation never implies a transaction.
- **FR-3.14** For an investment account, the system derives **net contributions**
  (deposits − withdrawals) and **return** (current value − net contributions) for a
  chosen period.
- **FR-3.15** `SAVINGS` accounts do not accept valuations.

> *Why the split:* if a portfolio is worth 105 000 after the user deposited 100 000, the
> 5 000 is market movement, not something the user did. Deriving one number from the
> other would make it impossible to tell contribution from performance — the single most
> useful thing this application can show.

## 4. Goals

The first version supports one goal type: the **recurring contribution goal** —
"put aside X per period".

- **FR-4.1** A user can create a goal with: name, target amount, period (`MONTHLY`),
  start date, and an optional end date.
- **FR-4.2** A goal is linked to one or more of the owner's financial accounts. Only
  deposits to those accounts count toward it.
- **FR-4.3** For each period, the system computes **contributed** (sum of deposits in that
  period on the linked accounts), and marks the period as met or missed against the
  target amount.
- **FR-4.4** A user can see, for a goal: the current period's progress, and a history of
  met/missed periods.
- **FR-4.5** Withdrawals in a period reduce that period's contributed amount.
- **FR-4.6** A user can edit, pause and delete their goals. Editing a target amount
  applies to the current and future periods, never retroactively.
- **FR-4.7** Goals are informational only — no automation, no reminders in v1.

Other goal types (target amount by date, open-ended target, emergency fund) are
deliberately deferred; see [§8](#8-out-of-scope-for-the-first-version).

## 5. Groups and shared goals

- **FR-5.1** A user can create a group with a name and becomes its owner.
- **FR-5.2** The group owner can invite users, and remove members. A member can leave.
- **FR-5.3** A group can have shared goals, using the same goal type as FR-4.
- **FR-5.4** A shared goal's target is a group-level amount per period. Progress is the
  sum of contributions from the accounts that members have linked to it.
- **FR-5.5** Each member decides which of their accounts they link to a shared goal.
- **FR-5.6** **Group members can see each other's financial accounts, transactions and
  valuations in full.** Joining a group is an explicit, informed decision to share.
- **FR-5.7** The invitation flow must state clearly, before acceptance, that joining
  reveals the invitee's financial data to existing members and theirs to the invitee.
- **FR-5.8** Leaving a group removes the leaver's future data from the group's view;
  historical contributions to a shared goal remain, attributed to that member.
- **FR-5.9** Only the group owner can delete a group. Deleting a group never deletes any
  member's accounts or transactions.

## 6. Overview and reporting

- **FR-6.1** A user sees a dashboard with total value across accounts (savings balances
  plus latest investment valuations) and progress on active goals.
- **FR-6.2** A user can see the history of an account over a chosen period: derived
  balance over time for savings, valuations plus contributions for investments.
- **FR-6.3** Totals combining accounts in different currencies must not be shown as a
  single number in v1; group by currency instead.

## 7. Non-functional requirements

- **NFR-1 Informational only.** No payment, transfer or banking integration exists or
  will be added. All data is entered manually by users.
- **NFR-2 Privacy by default.** Data is private to its owner, except where a group
  membership explicitly opens it (FR-5.6). No sharing mechanism outside groups.
- **NFR-3 Correctness of money.** Monetary amounts use `BigDecimal` with an explicit
  currency. No floating-point arithmetic anywhere near money.
- **NFR-4 Traceability.** Derived figures (balances, progress, returns) must always be
  reproducible from stored transactions and valuations.
- **NFR-5 Schema evolution.** All database schema changes go through Flyway migrations.
- **NFR-6 Testability.** Every derived calculation (FR-3.8, FR-3.14, FR-4.3) has unit
  tests covering period boundaries and empty data.
- **NFR-7 Approachability.** This is a learning project with mixed experience levels:
  favour clear, conventional Spring structure over clever abstractions, and document
  non-obvious decisions.
- **NFR-8 Security basics.** Passwords hashed, authorization checked on every request at
  the account/group level, no data leakage across users outside groups.

## 8. Out of scope for the first version

- Moving money, connecting to banks, importing statements
- Goal types other than recurring contribution
- Multi-currency conversion and FX rates
- Per-holding tracking inside an investment account (only total value is tracked)
- Notifications, reminders, mobile apps, data export
- Roles beyond user / group owner

## 9. Open questions

1. Should a goal be allowed to span accounts in different currencies? (Proposal: no.)
2. When a member leaves a group, should other members keep seeing their historical
   transactions, or only the aggregate contribution?
3. Is a period strictly a calendar month, or a rolling period from the goal start date?
4. Should archived accounts still count toward dashboard totals?
