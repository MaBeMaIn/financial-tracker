---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Glossary

The shared vocabulary of the project. These words are used exactly as defined here in
code, database columns, API resources, tests and discussion. If a concept needs a new
word, add it here first.

| Term | Definition | Notes |
|---|---|---|
| **User** | A registered person using the application. | Owns accounts, goals and group memberships. |
| **Account** | A user's record of a place where their money sits. Has a type, a currency and one owner. | Called *financial account* when it might be confused with a login. Never shared between users. |
| **Savings account** | Account of type `SAVINGS`. Its value is derived from transactions. | Bank savings, cash buffer. |
| **Investment account** | Account of type `INVESTMENT`. Its value comes from valuations. | ISK, fund/depot account, pension. |
| **Transaction** | Money the user moved into or out of an account, on a date. Direction is `DEPOSIT` or `WITHDRAWAL`. | A fact the user knows exactly. Never inferred by the system. |
| **Deposit** | A transaction moving money into an account. | The unit of progress for goals. |
| **Withdrawal** | A transaction moving money out of an account. | Reduces goal progress in its period. |
| **Valuation** | A dated snapshot of what an investment account was worth. | Only for investment accounts. At most one per account per date. |
| **Balance** | Derived value of a *savings* account: deposits − withdrawals up to a date. | Never stored, never entered. |
| **Current value** | Latest valuation of an *investment* account. | Unknown if no valuation exists — not zero. |
| **Net contributions** | Deposits − withdrawals on an account over a period. | The user's own effort. |
| **Return** | Current value − net contributions, for an investment account. | What the market did. Deliberately kept separate from contributions. |
| **Goal** | A user's or group's savings intention. v1 has one type: recurring contribution. | Informational; never enforces anything. |
| **Recurring contribution goal** | "Set aside *target amount* per *period*", linked to one or more accounts. | Progress measured from deposits on the linked accounts. |
| **Period** | The recurring window a goal is measured in. v1: `MONTHLY`. | See open question in requirements §9. |
| **Contributed** | Sum of deposits minus withdrawals on a goal's linked accounts within one period. | Compared against the goal's target amount. |
| **Group** | A set of users who share goals and full visibility of each other's data. | Created and deleted by its owner. |
| **Group owner** | The user who created a group. | Can invite and remove members. |
| **Shared goal** | A goal belonging to a group, with a group-level target per period. | Progress is the sum of members' contributions. |

## Words we avoid

- *Portfolio* on its own — say **investment account** (a user may have several).
- *Balance* for an investment account — say **current value**.
- *Contribution* when meaning a valuation change — that is **return**.
- *Transfer* — implies moving money, which this application never does.
