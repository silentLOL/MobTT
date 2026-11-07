---
name: frontier-setup
description: "Bring this Repository onto Frontier's Tracker contract: write .frontier.yml, install the Adapter for its Skill family or for none, and rewrite existing Specs and Tickets to fit. Run it once to set up, and again whenever the family's own setup has rewritten its docs."
disable-model-invocation: true
metadata:
  author: David Kroeg
  version: "1.1"
  contract: "1"
---

# Frontier Setup

Setup brings this Repository onto the Tracker contract, [references/tracker-contract.md](references/tracker-contract.md): the files Frontier reads and the operations that change them. It writes `.frontier.yml` and installs an Adapter. `mattpocock/skills`, and a Repository with no Skill family, get a Rules Adapter: instructions in the project rules and docs that send agents' writes to the contract and leave any skills untouched. For a Skill family the Rules Adapter also translates the family's verbs onto the contract's operations. `obra/superpowers` gets a Reader Adapter instead: Frontier reads the family's own files where it writes them, so Setup names the family in `.frontier.yml`, adds one project rules line that marks a Feature done, and moves or rewrites nothing.

Three rules hold for the whole run:

- **The working tree as it is.** Setup works on whatever branch is checked out, clean or not, and runs read-only git only: `git status`, `git log`, `git ls-files`, `git check-ignore`. It writes files, and the git state stays as the user left it.
- **Compare before writing.** Each file Setup would write is compared with what is there, and a file that already fits is left untouched. A re-run where everything fits writes nothing.
- **Questions before writes.** Steps 2 and 4 ask every question before the first write, so stopping at a question leaves nothing behind.

## 1. Before starting

Tell the user, as a notice rather than a question: a family skill patched by hand keeps writing the old convention, and Setup can't tell which ones were patched, so reinstall each one with `npx skills add <source> --skill <name> -y`. Then carry on.

## 2. Find the Skill family

A Skill family is a published set of skills that writes Specs and Tickets its own way. Look at the `source` of each entry in `skills-lock.json`, at the folder names under `.agents/skills/` and `.claude/skills/`, and, for `obra/superpowers`, at `docs/superpowers/`, the headers of the plans in it, and the folder names under `~/.claude/plugins/`:

| Family | Recognised by |
|---|---|
| `mattpocock/skills` | the source `mattpocock/skills`, or folders such as `setup-matt-pocock-skills`, `to-tickets`, `to-spec`, `wayfinder`, `triage`, `implement` |
| `obra/superpowers` | the source `obra/superpowers`, or folders such as `brainstorming`, `writing-plans`, `executing-plans`, `subagent-driven-development`; a `docs/superpowers/` folder; a plan whose header names `superpowers:` sub-skills, such as "REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development"; or a `superpowers@…` plugin under `~/.claude/plugins/`, such as `superpowers@claude-plugins-official` or `superpowers@superpowers-marketplace` |
| `addyosmani/agent-skills` | the source `addyosmani/agent-skills` |

Any other source is not a Skill family, `frontier-setup` included: `tuist/agent-skills`, `avdlee/*`, `humanlayer/skills`, `firebase/skills`, `bitrise-io/agent-skills` and the like.

When `mattpocock/skills` is the only family found, it is the family, and no question is asked. Finding no family doesn't settle it: a family installed at user level, or as a Claude Code plugin, leaves nothing in the Repository. So in every other case (two families, none, or only a family other than `mattpocock/skills`), print the **family question** as a message of its own, saying what you found and where:

> Which Skill family writes this Repository's Specs and Tickets: `mattpocock/skills`, `obra/superpowers`, or No Skill family?

What you found is evidence; the answer is the user's. Offer the three choices, propose none, and wait for the answer before step 3. With no answer, stop here with nothing written.

The family decides the rest of the run:

- **`mattpocock/skills`**: read [references/adapter-mattpocock.md](references/adapter-mattpocock.md), the Rules Adapter for steps 3 to 5.
- **`obra/superpowers`**: read [references/adapter-superpowers.md](references/adapter-superpowers.md), the Reader Adapter for steps 3 to 5.
- **No Skill family**: Setup installs the Rules Adapter with no verbs to translate: the rules lines, and a tracker doc that names the contract's operations. Steps 3 and 5 say how.
- **`addyosmani/agent-skills`**: tell the user that the Adapter for that family comes in a later version of Setup, and stop with nothing written: no `.frontier.yml`, no Adapter and no rewrite.

This step is done when the family is decided: found as the only family, or named by the user's answer to the family question.

## 3. Check the family's setup

Run the Adapter's "Check the family's setup". When it stops, Setup stops: tell the user what to do, and write nothing. Otherwise it gives the Feature root.

With no Skill family there is no family setup to check, and the Feature root is `.scratch`.

## 4. Ask

Gather these questions, then put them all to the user in one message, each with its proposed answer:

- **Paragraphs for `## This repository`**, with a Rules Adapter, when `docs/agents/issue-tracker.md` exists and has no `## This repository` section. List each of its paragraphs that isn't about the format, and propose keeping each one in the new doc's `## This repository`, verbatim, or with a clause about the format cut, such as an instruction to write a prose `Status:` line. A paragraph is about the format when the contract or the new tracker doc now covers it: where Features and Tickets live, file names, front matter, statuses and labels, the operations, and a Skill family's verbs. Any other paragraph is proposed: which tracker is the team's tracker of record, what git tracks, where evidence is kept, how to write for a reader. With none to propose, there is no question.
- **The file for the rules lines**, only when neither `CLAUDE.md` nor `AGENTS.md` exists. Propose `CLAUDE.md`.
- **The judgement tables**, with a Rules Adapter. Read the Features and Tickets under the Feature root by [references/migration-rules.md](references/migration-rules.md), and give every judgement case it names a row in its Feature's judgement table, laid out as its "Judgement cases" says.
- **The Adapter's own questions**, from its "Ask" section, when it has one.

Quote what each question is about, as it would be written, such as every paragraph proposed for `## This repository` with any cut marked, so the user can answer without opening a file.

This step is done when that message has been sent and every answer is back, or when there were no questions to send. With no answer, stop here with nothing written.

## 5. Write

Write in this order:

1. **`.frontier.yml`** at the top of the Repository: `contract:` set to `metadata.contract` from this file's frontmatter, any keys the Adapter's "Write `.frontier.yml`" adds, and `features: <feature-root>` only when the Feature root isn't `.scratch`. An existing file keeps its comments, its other keys and a valid `features`, which wins over the Feature root from step 3.
2. **The project rules**: the `## Tracker contract` section, which is [assets/rules-lines.md](assets/rules-lines.md) verbatim, written once. For `obra/superpowers`, the Adapter's "Write the project rules" puts [assets/superpowers-rules-lines.md](assets/superpowers-rules-lines.md) in its place.
   - **The file** is `CLAUDE.md`, else `AGENTS.md`, else the file chosen in step 4, unless the Adapter's "Write the project rules" names another. When `AGENTS.md` is a symlink to `CLAUDE.md`, they are one file.
   - **The place** is where an existing `## Tracker contract` section in that file stands, else the end of the file after one blank line, unless the Adapter places it.
   - **Every other copy** of the rules lines in `CLAUDE.md` or `AGENTS.md` is removed, heading and body: a `### Tracker contract` sub-section, or a second `## Tracker contract` section.

   For `mattpocock/skills`, the Adapter's "Write the project rules" also rewrites stale lines in the family's block.
3. **The tracker docs**, with a Rules Adapter: `docs/agents/issue-tracker.md` becomes the tracker doc asset, [assets/mattpocock-issue-tracker.md](assets/mattpocock-issue-tracker.md) for `mattpocock/skills` and [assets/no-family-issue-tracker.md](assets/no-family-issue-tracker.md) with no Skill family. The asset's `## This repository` section comes last:
   - when the current doc has a `## This repository` section, keep that section and everything after it as they are, and replace everything above it with the asset's text above it;
   - otherwise, write the asset whole, with the paragraphs the user kept appended to `## This repository`.

   For `mattpocock/skills`, the Adapter's "Write the triage labels doc" adds [assets/mattpocock-triage-labels.md](assets/mattpocock-triage-labels.md). With no Skill family, the tracker doc is the only one.
4. **The rewrite** of the existing Features and Tickets under the Feature root, with a Rules Adapter, by [references/migration-rules.md](references/migration-rules.md), the same with a Skill family or without one: every fixed row, and every judgement row as step 4's answers settled it.

## 6. Report

End the run with the report, in these three parts. The rewrite's share of the first two is laid out in migration-rules.md's "In the report".

- **Files changed**, by step 5's writes. Name each file written and what changed in it, or say that the write changed nothing.
- **Left outside the contract**, with why: what this run saw and didn't bring onto the contract, such as paragraphs the user declined for `## This repository`. Say "nothing" when there is nothing.
- **The `.gitignore` warning**, only when `git check-ignore --no-index -v .agents/skills/frontier-setup/SKILL.md` or `git check-ignore --no-index -v .claude/skills/frontier-setup` prints a rule (`--no-index` catches the rule even when the folder is already tracked): the skill folder is excluded from git, so teammates won't get the skill, or the contract the tracker doc links to, through git. Name the rule. Setup leaves `.gitignore` as it is; changing it is the user's choice.

Those three parts are the whole report. It describes files only: branches, commits and the app are the user's business, and stay out of it.
