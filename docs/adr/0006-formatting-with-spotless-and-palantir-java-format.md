# ADR-0006: Formatting with Spotless and palantir-java-format

- **Status:** Accepted
- **Date:** 2026-08-26
- **Deciders:** Martin

## Context

[coding-standards.md](../guidelines/coding-standards.md) listed "agree on a formatter" as
an open TODO, with the code meanwhile formatted by whatever each contributor's IDE
happened to do. That is affordable in a repository this small and stops being affordable
the moment two people — or a person and an AI tool — touch the same file: reformatting
noise hides the actual change in review, and "fix the formatting" becomes a review comment
someone has to write over and over.

The project already states a 120-column soft limit and uses four-space indentation. Java
has no single blessed formatter the way .NET has CSharpier, so the choice is between an
opinionated formatter with no configuration and a fully configurable engine whose settings
become their own thing to maintain.

## Decision

The build runs [Spotless](https://github.com/diffplug/spotless) with
[palantir-java-format](https://github.com/palantir/palantir-java-format) as the engine for
Java sources. `spotless:check` is bound to the `verify` phase, so unformatted code fails
the build; `./mvnw spotless:apply` reformats the repository.

Spotless additionally removes unused imports, enforces a fixed import order, and formats
the markdown under `docs/`.

palantir-java-format is deliberately unconfigurable: four-space indent, 120 columns, no
options to argue about. It also formats fluent chains and lambdas better than
google-java-format, which matters in a codebase that leans on AssertJ and streams.

Javadoc formatting stays off. The javadoc here contains hand-laid-out `<pre>{@code ...}`
examples, and reflowing them costs more than it gains.

## Alternatives considered

- **google-java-format** — the most widely used option, and the one this project's own
  standards floated first. Rejected: it mandates two-space indentation and a 100-column
  limit, which contradicts the style already written down and already in the code, so
  adopting it would mean reformatting every file *and* rewriting the standard to match a
  tool.
- **An Eclipse formatter XML config** — genuinely project-specific rules, and IntelliJ
  imports the same file so the IDE and the build agree exactly. Rejected: every knob it
  exposes is an invitation to discuss it, which is the discussion a formatter exists to
  end. Revisit if palantir's output turns out to be wrong for us in a specific,
  demonstrable way.
- **Formatting on save in the IDE only** — no build changes. Rejected: it enforces nothing
  for a contributor whose IDE is set up differently, and nothing at all for code generated
  by a tool.

## Consequences

- Formatting stops being reviewable, and therefore stops being reviewed. This is the
  point.
- The first `spotless:apply` touches nearly every file. It is committed on its own, so it
  can be skipped with `git blame --ignore-rev` and does not obscure any real change.
- A build can now fail for a reason that has nothing to do with correctness. The fix is
  always one command, and IntelliJ's palantir-java-format plugin keeps it from happening.
- Formatter and IDE can disagree until each contributor installs that plugin. The build is
  the authority when they do.
- `.mvn/jvm.config` carries `--add-exports` flags because the formatter reaches into javac
  internals. It applies to every Maven invocation in this repository, which is a small
  amount of action at a distance to be aware of.
- Markdown in `docs/` is reflowed to 90 columns. Prose diffs get noisier when a paragraph
  is edited mid-sentence; that is accepted in exchange for the docs staying uniform.

