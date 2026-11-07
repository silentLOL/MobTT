# Adapter: mattpocock/skills

How Setup installs the Adapter for `mattpocock/skills`. The family's own setup, `/setup-matt-pocock-skills`, writes three things this Adapter builds on:

- **The `## Agent skills` block** in `CLAUDE.md`, else `AGENTS.md`: the sub-sections `### Issue tracker`, `### Triage labels` (only when `triage` is installed) and `### Domain docs`, each a one-line summary ending in a pointer such as "See `docs/agents/issue-tracker.md`." A re-run of the family's setup regenerates the block in place and leaves the sections around it alone.
- **`docs/agents/issue-tracker.md`**, from one of its templates: local markdown, GitHub, GitLab, or free prose for any other tracker.
- **`docs/agents/triage-labels.md`** when `triage` is installed, and `docs/agents/domain.md`, which Setup never touches.

## Check the family's setup

Read `docs/agents/issue-tracker.md`. Setup stops here, with nothing written, in two cases:

- **The doc is missing**: `/setup-matt-pocock-skills` hasn't run. Tell the user to run it, choose local markdown as the issue tracker, then run `/frontier-setup` again.
- **The doc chose a tracker other than local markdown**: Frontier only tracks local files. Tell the user so, name the tracker the doc chose, and say that re-running `/setup-matt-pocock-skills` with local markdown, then `/frontier-setup`, would bring this Repository onto the contract.

The tracker the doc chose follows from where it says Specs and Tickets are written:

- **Local markdown**: they are markdown files in a directory of the Repository, such as upstream's "Issue tracker: Local Markdown" with `.scratch/<feature-slug>/`. This Adapter's own doc counts too, and so does a doc brought onto the contract by hand. A local-markdown doc may still name Jira or another tracker as the team's tracker of record, or name Ticket ids from one; the files decide.
- **GitHub**: issues live in GitHub Issues, written with `gh`.
- **GitLab**: issues live in GitLab Issues, written with `glab`.
- **Another tracker**: prose describing Jira, Linear or any other place, with no directory of files for Specs and Tickets.

**The Feature root** is the directory the doc names for Features: `.scratch` in upstream's template and in this Adapter's doc.

## Write the project rules

The project rules file is the one holding the `## Agent skills` block: `CLAUDE.md` when both hold one. With no block in either, `SKILL.md`'s file and place for the rules lines apply.

In the file holding the block:

1. **The `## Tracker contract` section** goes right after the `## Agent skills` block, moving there from wherever it stood. The block ends where the next `## ` heading begins, or at a marker such as an HTML comment that opens another tool's section. Leave one blank line on each side.
2. **Stale lines in the block** are rewritten to the contract's strings. The block keeps each of its pointers (`### Issue tracker`, `### Triage labels`, `### Domain docs`), with its "See `docs/agents/…`" sentence and every sentence that still holds.
   - **`### Triage labels`**: its summary is stale unless it says that `needs-info` is written as `needs-human` and `wontfix` as `wont-do`. Rewrite a stale summary to: "The five canonical roles, except `wontfix` is written as `wont-do` and `needs-info` as `needs-human`."
   - **`### Issue tracker`**: delete a sentence or clause that has state written anywhere but front matter, such as a prose `Status:` line or a ``Triage: `<label>` `` line on a Spec, or that names `needs-info`, `wontfix`, `done`, `complete` or `closed` as a status to write.

The other sections of the file are the user's, and stay as they are.

## Write the triage labels doc

**`docs/agents/triage-labels.md`** becomes this skill's `assets/mattpocock-triage-labels.md`, verbatim. Write it whether or not `triage` is installed, since the contract's status strings apply to every Ticket.
