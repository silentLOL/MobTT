# Adapter: obra/superpowers

How Setup installs the Reader Adapter for `obra/superpowers`, beside a Rules Adapter. Frontier reads this family's own files where the family writes them, so Setup moves none of them. The family writes:

- **Design docs** at `docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md`. Each is a Feature, and its text is the Feature's Spec.
- **Plans** at `docs/superpowers/plans/YYYY-MM-DD-<feature-name>.md`. Each `### Task N:` in a plan is a Ticket. A plan's `**Spec:**` line names its design doc.
- **Ledgers** at `.superpowers/sdd/<name>/progress.md`, in the Worktree running a plan, while the plan runs. The family deletes a ledger after the final review, so a plan whose work is done leaves none.

## Check the family's setup

The check always passes, and gives no Feature root of its own: Frontier reads the contract Features under the Rules side's Feature root alongside this family's.

## Ask

Two questions and a judgement table join the ones `SKILL.md` puts to the user before anything is written.

### The folders

Each folder question comes with its proposed folder: the **`specs` folder**, where design docs are written, and the **`tickets` folder**, where plans are written. A preference in the project rules can move either folder: upstream's `brainstorming` and `writing-plans` let the user's preferences override their default locations, and `using-superpowers` puts instructions in `CLAUDE.md` or `AGENTS.md` above any skill. Propose each from the first of these that gives one:

1. **A location preference** in `CLAUDE.md` or `AGENTS.md`: a line that says where specs or design docs are saved, or where plans are saved. Quote the line beside the proposal.
2. **The folders that exist**: the folder holding files named `YYYY-MM-DD-<topic>-design.md`, or the folder holding plans with `### Task N:` headings.
3. **Upstream's defaults**: `docs/superpowers/specs` and `docs/superpowers/plans`.

Write each answer as a path relative to the top of the Repository, with no leading `./` and no trailing `/`. A path outside the Repository isn't a valid answer: ask again.

### The done-mark table

A design doc written before the done-mark line reached the project rules carries no mark, even when its work is done, so Frontier shows its Feature as Active, with every Ticket Ready for agent. The table asks once which design docs are done.

**Rows.** One row, in filename order, for each design doc whose front matter has no `status`: a file named `YYYY-MM-DD-<topic>-design.md` directly in the proposed `specs` folder of the checked-out tree. So a re-run asks only about design docs still unmarked. A design doc found only in another Worktree is left to the done-mark line, which marks it when its branch is finished, and the report leaves it out: Frontier already reads it there. Each row names three things:

- **The design doc**, by its path.
- **Its plans**: every plan directly in the proposed `tickets` folder of the checked-out tree whose `**Spec:**` line names the design doc, or "none". A plan's `**Spec:**` line is its first one above its first `### Task` heading, outside fenced code. Its path is written bare, in backticks, in angle brackets, or as a markdown link's target, and names the design doc when it reaches it read from the top of the Repository or from the plan's folder: the target in `[docs/superpowers/specs/x-design.md](../specs/x-design.md)` is read from the plan's folder.
- **Its ledgers**: every ledger running one of those plans, by its path and the Worktree holding it, or "none". Find the Worktrees with `git worktree list`, and in each one list `.superpowers/sdd/` on the filesystem, with `ls` or `find`: the folder is git-ignored, so `git ls-files` never shows it. A ledger is `.superpowers/sdd/<name>/progress.md` whose first line is exactly `# SDD ledger — plan: <path>`, with an em dash. It runs the plan its `<path>` reaches: a relative path is read from the top of the Worktree holding the ledger, and an absolute one is cut at the deepest Worktree that contains it, then read from the top of that Worktree. A plan path from a linked Worktree names the plan at the same path in the checked-out tree.

**The proposal:**

- **done** when the design doc has a plan and no ledger runs any of its plans;
- **leave open** when a ledger runs one of its plans, or when it has no plan.

**Layout.** One table for the Repository, laid out and asked as [migration-rules.md](migration-rules.md)'s "Judgement cases" lays out a judgement table, with the columns **Design doc**, **Plan**, **Ledger** and **Proposed**, `done` or `leave open`. A row's question is whether the design doc's work is done, such as "4. Is the work of `2026-10-13-route-planner-design.md` done? Proposed: done, so `status: resolved`." When an answer moves the `specs` or `tickets` folder, build the table again from the answered folders and ask it again, before anything is written.

## Write `.frontier.yml`

Beside `contract`, the file holds `family: superpowers`, then `specs: <folder>` and `tickets: <folder>`, each only when its answer isn't upstream's default. A `specs` or `tickets` key whose answer is now the default is removed.

## Write the project rules

[../assets/superpowers-rules-lines.md](../assets/superpowers-rules-lines.md) holds this family's two bullets, which go in `SKILL.md`'s file and place for the rules lines:

- **The done-mark line** marks a design doc done when its branch is finished, since this family's Tickets are never claimed or resolved by hand.
- **The guard line** keeps the shared rules lines, and any `## Agent skills` section, to Specs and Tickets under the Feature root. Both families' agents read the same project rules, so without it a rule meant for contract Tickets could move this family's design docs and plans, or turn a plan's tasks into Ticket files.

## Write the done marks

The last of `SKILL.md`'s writes, in the checked-out tree. For each row answered done, set `status: resolved` in the design doc's front matter fence, adding the fence at the top when the doc has none, as the rewrite adds a missing fence ([migration-rules.md](migration-rules.md)'s "Front matter"). It is the mark the done-mark line writes, "Complete a Feature" in the contract. A row answered leave open writes nothing.

## In the report

**Files changed** names each design doc marked done, with `status: resolved` added. **Left outside the contract** holds only what Frontier won't read, so it names no design doc, not even one left open.

## Tested upstream version

This Adapter was tested end to end against `obra/superpowers` v7.0.1 (`2ce8aa1`) on 2026-10-28: a design taken from `brainstorming` to a local merge by `finishing-a-development-branch`, with the done-mark line unrevised. When upstream releases, check that the new version still has the design doc and plan paths, the `### Task N:` heading, the ledger's first line, and `finishing-a-development-branch`'s menu that this Adapter and the done-mark line rely on.
