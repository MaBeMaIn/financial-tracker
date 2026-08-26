---
status: draft
owner: martin
last-updated: 2026-08-21
---

# Domain modelling

How we build the inside of the hexagon. Structure and the dependency rule are in
[architecture-principles.md](architecture-principles.md); the entities and their
invariants are in [../architecture/domain-model.md](../architecture/domain-model.md); the
words are in [../product/glossary.md](../product/glossary.md).

The domain is plain Java, testable with plain JUnit and no Spring context.

## Aggregates

Aggregates are consistency boundaries: everything inside one is saved together and its
invariants always hold. Proposed boundaries for this project:

|  Aggregate  |     Root      |               Contains               |                          Why                           |
|-------------|---------------|--------------------------------------|--------------------------------------------------------|
| Account     | `Account`     | name, type, currency, archived flag  | Small, always loaded whole                             |
| Transaction | `Transaction` | one entry, references `AccountId`    | Unbounded in number — not held inside `Account`        |
| Valuation   | `Valuation`   | one snapshot, references `AccountId` | Same reason                                            |
| Goal        | `Goal`        | target, period, linked account ids   | Linked accounts are references, not owned objects      |
| Group       | `Group`       | memberships                          | Bounded and small; membership rules must hold together |

References **across** aggregates are by identifier (`AccountId`), never by object. Rules
spanning aggregates (for example "a linked account must belong to a group member") are
checked in the handler, which is allowed to load both.

## Creation: `create` and `hydrate`

An aggregate comes into existence in two different situations, and we keep them visibly
apart ([ADR-0003](../adr/0003-split-aggregate-creation-into-create-and-hydrate.md)).
**Aggregates have no public constructors.**

|                         |           `create(...)`            |         `hydrate(...)`          |
|-------------------------|------------------------------------|---------------------------------|
| Means                   | Something happened in the business | We read stored state back       |
| Applies business rules  | Yes — creation-time invariants     | No — structural validity only   |
| Assigns identity        | Yes                                | No, the identity already exists |
| Raises lifecycle events | Yes (when we add them)             | Never                           |
| Called by               | Use cases, test builders           | Persistence mappers, only       |

```java
public final class Account {

    private final AccountId id;
    private final UserId owner;
    private final AccountType type;
    private final Currency currency;
    private String name;
    private boolean archived;

    private Account(AccountId id, UserId owner, AccountType type,
                    Currency currency, String name, boolean archived) { ... }

    /** A user opens a new account. Creation-time rules live here. */
    public static Account create(AccountId id, UserId owner, AccountType type,
                                 Currency currency, String name) {
        requireNonNull(owner, "owner");
        requireText(name, "name");
        // a new account is never archived, and its currency is fixed from now on
        return new Account(id, owner, type, currency, name, false);
        // later: record(new AccountOpened(id, owner, type));
    }

    /** Reconstitution from stored state. No rules, no events, no defaults. */
    public static Account hydrate(AccountId id, UserId owner, AccountType type,
                                  Currency currency, String name, boolean archived) {
        // deliberately accepts states create() would reject, e.g. archived == true
        return new Account(id, owner, type, currency, name, archived);
    }
}
```

Why bother: with one constructor the two paths are indistinguishable, and the difference
gets papered over with nullable parameters or "if the id is null it must be new". That is
fine until we add lifecycle events — at which point reading a row from the database
silently raises an `AccountOpened` event. Naming the paths now makes that change local and
safe.

Rules:

- Only persistence mappers call `hydrate`. Enforced by an ArchUnit test; tests build
  aggregates with `create` or a builder that uses it.
- `hydrate` never applies defaults, timestamps or derived state. If stored data is missing
  something, fix it in a migration, not in `hydrate`.
- When an aggregate grows past a handful of fields, `hydrate` takes a state record
  (`AccountState`) rather than a long parameter list.
- Both paths must be updated together when a field is added — the compiler will say so.

## Value objects

Value objects are immutable records with meaning and behaviour: `Money`, `AccountId`,
`Period`, `DateRange`. Prefer them over bare `BigDecimal`, `UUID` and `String` — a method
taking `(Money, AccountId)` cannot be called with the arguments swapped.

## Typed string primitives

Identifiers and names are `TypedString`s, not bare `String`s, so that `transfer(UserId,
GroupId)` cannot be called with the arguments swapped and a `Username` cannot be stored
where an email belongs.

`common.domain.TypedStringBase<T>` is the shared base: it trims, rejects null and blank,
and gives value equality that also compares the concrete class, so a `Username` never
equals a `GroupName` holding the same text. The type parameter is the subtype itself,
which makes each primitive `Comparable` against its own kind only.

```java
public final class Username extends TypedStringBase<Username> {

    private Username(String value) {
        super(value);
    }

    public static Username of(String value) {
        return new Username(value);
    }
}
```

- Subtypes are `final`, have a private constructor and a static `of(...)`.
- There is no `of` on the base class: static methods are not polymorphic in Java, so a
  base factory cannot know which subtype to build.
- Normalization beyond trimming (lower-casing an email) happens in the subtype's factory
  *before* calling the constructor; extra validation happens *after*, on the normalized
  value. Neither belongs in an overridable method called from the constructor.
- A `record` implementing `TypedString` would be the more idiomatic Java 26 alternative,
  since records cannot extend a class. We chose the base class so the null/blank check,
  equality and comparison exist once rather than in every primitive. **TODO** — record as
  an ADR if we keep it past the first few slices.

## Behaviour, not data

**No anemic domain model.** If `Account` is only getters and setters and every rule lives
in a handler, we have layered code wearing a hexagon's clothes. The test: can you read
`Account` and learn what an account is allowed to do?

## Invariants

The invariants listed in [domain-model.md](../architecture/domain-model.md) are enforced
in constructors and factory methods, so an invalid object cannot exist — not validated
after the fact.

## Domain errors

Domain errors are domain exceptions (`AccountArchived`,
`ValuationNotAllowedForSavingsAccount`), translated to HTTP status codes in the web
adapter. The domain never mentions 404 or 400.
