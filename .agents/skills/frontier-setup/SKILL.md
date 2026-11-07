---
name: frontier-setup
description: "Bring this Repository onto Frontier's Tracker contract: write .frontier.yml, install the Adapters for its Skill families or for none, and rewrite existing Specs and Tickets to fit. Run it once to set up, again whenever a family's own setup has rewritten its docs, and again when the Repository takes up or drops a family."
disable-model-invocation: true
metadata:
  author: David Kroeg
  version: "1.3"
  contract: "1"
---

# Frontier Setup

Setup brings this Repository onto the Tracker contract, [references/tracker-contract.md](references/tracker-contract.md): the files Frontier reads and the operations that change them. It writes `.frontier.yml` and installs one Rules Adapter, plus a Reader Adapter when the Repository also uses `obra/superpowers`. Developers on one team may work with different Skill families, and each keeps working with their own.

- **A Rules Adapter** is instructions in the project rules and docs that send agents' writes to the contract and leave any skills untouched. For `mattpocock/skills` it also translates the family's verbs onto the contract's operations; with No Skill family on the Rules side there are no verbs to translate.
- **A Reader Adapter** has Frontier read a family's own files where the family writes them. For `obra/superpowers`, Setup names the family in `.frontier.yml`, adds its lines to the project rules, and marks Complete the design docs the user confirms are done. It moves none of the family's files.

Three rules hold for the whole run:

- **The working tree as it is.** Setup works on whatever branch is checked out, clean or not, and runs read-only git only: `git status`, `git log`, `git ls-files`, `git check-ignore`, `git worktree list`. It writes files, and the git state stays as the user left it.
- **Compare before writing.** Each file Setup would write is compared with what is there, and a file that already fits is left untouched. A re-run where everything fits writes nothing.
- **Questions before writes.** Steps 2 and 4 ask every question before the first write, so stopping at a question leaves nothing behind.

## 1. Before starting

Tell the user, as a notice rather than a question: a family skill patched by hand keeps writing the old convention, and Setup can't tell which ones were patched, so reinstall each one with `npx skills add <source> --skill <name> -y`. Then carry on.

## 2. Find the Skill families

A Skill family is a published set of skills that writes Specs and Tickets its own way. A Repository has two sides to settle:

- **The Rules side**, the family whose Specs and Tickets follow the contract: `mattpocock/skills` or No Skill family.
- **The Reader side**: `obra/superpowers`, or none.

Look at the `source` of each entry in `skills-lock.json`, at the folder names under `.agents/skills/` and `.claude/skills/`, and, for `obra/superpowers`, at `docs/superpowers/`, the headers of the plans in it, the plugin keys in `~/.claude/plugins/installed_plugins.json`, and the `family` key of `.frontier.yml`:

| Family | Recognised by |
|---|---|
| `mattpocock/skills` | the source `mattpocock/skills`, or folders such as `setup-matt-pocock-skills`, `to-tickets`, `to-spec`, `wayfinder`, `triage`, `implement` |
| `obra/superpowers` | the source `obra/superpowers`, or folders such as `brainstorming`, `writing-plans`, `executing-plans`, `subagent-driven-development`; a `docs/superpowers/` folder; a plan whose header names `superpowers:` sub-skills, such as "REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development"; a `superpowers@…` key in `~/.claude/plugins/installed_plugins.json`, such as `superpowers@claude-plugins-official` or `superpowers@superpowers-marketplace`; or `family: superpowers` already in `.frontier.yml` |
| `addyosmani/agent-skills` | the source `addyosmani/agent-skills` |

Any other source is not a Skill family, `frontier-setup` included: `tuist/agent-skills`, `avdlee/*`, `humanlayer/skills`, `firebase/skills`, `bitrise-io/agent-skills` and the like.

When `mattpocock/skills` is the only family the table recognises, with no sign of `obra/superpowers`, the Rules side is `mattpocock/skills`, there is no Reader side, and no question is asked. Evidence is not an answer: a sign of `obra/superpowers` may be one developer's own install or old work, and finding no family doesn't mean there is none, since a family installed at user level, or as a Claude Code plugin, leaves nothing in the Repository. So in every other case, print the **family question** as a message of its own, saying what you found for each side and where:

> Which Skill family writes this Repository's Specs and Tickets to the Tracker contract: `mattpocock/skills` or No Skill family? And is `obra/superpowers` used as well?

Offer both picks, propose neither, and wait for the answer before step 3. With no answer, stop here with nothing written.

The Rules side decides which Rules Adapter the run installs, and every run that writes installs one:

- **`mattpocock/skills`**: read [references/adapter-mattpocock.md](references/adapter-mattpocock.md), the Rules Adapter for steps 3 to 5.
- **No Skill family**: Setup installs the Rules Adapter with no verbs to translate: the rules lines, and a tracker doc that names the contract's operations. Steps 3 and 5 say how.
- **`addyosmani/agent-skills`**: tell the user that the Adapter for that family comes in a later version of Setup, and stop with nothing written: no `.frontier.yml`, no Adapter and no rewrite.

With `obra/superpowers` on the Reader side, also read [references/adapter-superpowers.md](references/adapter-superpowers.md), the Reader Adapter, whose sections run in steps 3 to 5 beside the Rules Adapter's.

This step is done when both sides are decided: found as `mattpocock/skills` alone, with no sign of `obra/superpowers`, or named by the user's answers to the family question.

## 3. Check the families' setup

Run the Rules Adapter's "Check the family's setup", and the Reader Adapter's when there is one. When either stops, Setup stops: tell the user what to do, and write nothing. Otherwise the Rules Adapter's check gives the Feature root.

With No Skill family on the Rules side, there is no Rules Adapter check to run, and the Feature root is `.scratch`.

## 4. Ask

Gather these questions, then put them all to the user in one message, each with its proposed answer:

- **Paragraphs for `## This repository`**, when `docs/agents/issue-tracker.md` exists and has no `## This repository` section. List each of its paragraphs that isn't about the format, and propose keeping each one in the new doc's `## This repository`, verbatim, or with a clause about the format cut, such as an instruction to write a prose `Status:` line. A paragraph is about the format when the contract or the new tracker doc now covers it: where Features and Tickets live, file names, front matter, statuses and labels, the operations, and a Skill family's verbs. Any other paragraph is proposed: which tracker is the team's tracker of record, what git tracks, where evidence is kept, how to write for a reader. With none to propose, there is no question.
- **The file for the rules lines**, only when neither `CLAUDE.md` nor `AGENTS.md` exists. Propose `CLAUDE.md`.
- **The judgement tables.** Read the Features and Tickets under the Feature root by [references/migration-rules.md](references/migration-rules.md), and give every judgement case it names a row in its Feature's judgement table, laid out as its "Judgement cases" says.
- **Each Adapter's own questions**, from its "Ask" section, when it has one.

Quote what each question is about, as it would be written, such as every paragraph proposed for `## This repository` with any cut marked, so the user can answer without opening a file.

This step is done when that message has been sent and every answer is back, or when there were no questions to send. With no answer, stop here with nothing written.

## 5. Write

Write in this order:

1. **`.frontier.yml`** at the top of the Repository: `contract:` set to `metadata.contract` from this file's frontmatter, any keys the Reader Adapter's "Write `.frontier.yml`" adds, and `features: <feature-root>` only when the Feature root isn't `.scratch`. With no Reader side, the `family`, `specs` and `tickets` keys are removed, and any done marks already written stay in their design docs. An existing file keeps its comments, its other keys and a valid `features`, which wins over the Feature root from step 3.
2. **The project rules**: the `## Tracker contract` section, written once. It is [assets/rules-lines.md](assets/rules-lines.md) verbatim, the shared rules lines. With `obra/superpowers` on the Reader side, one blank line and [assets/superpowers-rules-lines.md](assets/superpowers-rules-lines.md) verbatim follow them inside the same section; with no Reader side, the shared rules lines are the whole section.
   - **The file** is `CLAUDE.md`, else `AGENTS.md`, else the file chosen in step 4, unless the Rules Adapter's "Write the project rules" names another. When `AGENTS.md` is a symlink to `CLAUDE.md`, they are one file.
   - **The place** is where an existing `## Tracker contract` section in that file stands, else the end of the file after one blank line, unless the Rules Adapter places it.
   - **Every other copy** of the rules lines in `CLAUDE.md` or `AGENTS.md` is removed, heading and body: a `### Tracker contract` sub-section, or a second `## Tracker contract` section.

   For `mattpocock/skills`, the Rules Adapter's "Write the project rules" also rewrites stale lines in the family's block.
3. **The tracker docs**: `docs/agents/issue-tracker.md` becomes the tracker doc asset, [assets/mattpocock-issue-tracker.md](assets/mattpocock-issue-tracker.md) for `mattpocock/skills` and [assets/no-family-issue-tracker.md](assets/no-family-issue-tracker.md) with No Skill family on the Rules side. The asset's `## This repository` section comes last:
   - when the current doc has a `## This repository` section, keep that section and everything after it as they are, and replace everything above it with the asset's text above it;
   - otherwise, write the asset whole, with the paragraphs the user kept appended to `## This repository`.

   For `mattpocock/skills`, the Rules Adapter's "Write the triage labels doc" adds [assets/mattpocock-triage-labels.md](assets/mattpocock-triage-labels.md). With No Skill family on the Rules side, the tracker doc is the only one.
4. **The rewrite** of the existing Features and Tickets under the Feature root, by [references/migration-rules.md](references/migration-rules.md), the same with `mattpocock/skills` or No Skill family on the Rules side: every fixed row, and every judgement row as step 4's answers settled it.
5. **The done marks**, with `obra/superpowers` on the Reader side: the Reader Adapter's "Write the done marks".

## 6. Report

End the run with the report, in these three parts. The rewrite's share of the first two is laid out in migration-rules.md's "In the report", and the Reader Adapter's in its own "In the report".

- **Files changed**, by step 5's writes. Name each file written and what changed in it, or say that the write changed nothing.
- **Left outside the contract**, with why: what this run saw and didn't bring onto the contract, such as paragraphs the user declined for `## This repository`. Say "nothing" when there is nothing.
- **The `.gitignore` warning**, only when `git check-ignore --no-index -v .agents/skills/frontier-setup/SKILL.md` or `git check-ignore --no-index -v .claude/skills/frontier-setup` prints a rule (`--no-index` catches the rule even when the folder is already tracked): the skill folder is excluded from git, so teammates won't get the skill, or the contract the tracker doc links to, through git. Name the rule. Setup leaves `.gitignore` as it is; changing it is the user's choice.

Those three parts are the whole report. It describes files only: branches, commits and the app are the user's business, and stay out of it.
