# Adapter: obra/superpowers

How Setup installs the Reader Adapter for `obra/superpowers`. Frontier reads this family's own files where the family writes them, so Setup moves nothing and rewrites nothing. The family writes:

- **Design docs** at `docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md`. Each is a Feature, and its text is the Feature's Spec.
- **Plans** at `docs/superpowers/plans/YYYY-MM-DD-<feature-name>.md`. Each `### Task N:` in a plan is a Ticket.

## Check the family's setup

The check always passes. The Feature root is `.scratch`, where any contract Features are read alongside the family's.

## Ask

Two questions join the ones `SKILL.md` puts to the user before anything is written, each with its proposed folder: the **`specs` folder**, where design docs are written, and the **`tickets` folder**, where plans are written. A preference in the project rules can move either folder: upstream's `brainstorming` and `writing-plans` let the user's preferences override their default locations, and `using-superpowers` puts instructions in `CLAUDE.md` or `AGENTS.md` above any skill. Propose each from the first of these that gives one:

1. **A location preference** in `CLAUDE.md` or `AGENTS.md`: a line that says where specs or design docs are saved, or where plans are saved. Quote the line beside the proposal.
2. **The folders that exist**: the folder holding files named `YYYY-MM-DD-<topic>-design.md`, or the folder holding plans with `### Task N:` headings.
3. **Upstream's defaults**: `docs/superpowers/specs` and `docs/superpowers/plans`.

Write each answer as a path relative to the top of the Repository, with no leading `./` and no trailing `/`. A path outside the Repository isn't a valid answer: ask again.

## Write `.frontier.yml`

Beside `contract`, the file holds `family: superpowers`, then `specs: <folder>` and `tickets: <folder>`, each only when its answer isn't upstream's default. A `specs` or `tickets` key whose answer is now the default is removed.

## Write the project rules

The `## Tracker contract` section is [../assets/superpowers-rules-lines.md](../assets/superpowers-rules-lines.md) verbatim: the done-mark line alone, in place of the shared rules lines, since this family's Tickets are never claimed or resolved by hand. The family writes no block of its own into the project rules, so `SKILL.md`'s file and place for the rules lines apply.

## Tested upstream version

This Adapter was tested end to end against `obra/superpowers` v7.0.1 (`2ce8aa1`) on 2026-10-28: a design taken from `brainstorming` to a local merge by `finishing-a-development-branch`, with the done-mark line unrevised. When upstream releases, check that the new version still has the design doc and plan paths, the `### Task N:` heading, the ledger's first line, and `finishing-a-development-branch`'s menu that this Adapter and the done-mark line rely on.
