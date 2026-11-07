# Rewrite rules

How Setup rewrites the Repository's existing Features and Tickets onto the Tracker contract. The rewrite reads every directory directly under the Feature root, and in each one only the files the contract names: `intent.md`, `spec.md`, `map.md`, and the Ticket files `issues/NN-<slug>.md`. Every other file stays as it is, apart from the files an answered judgement case renames or adds.

## Principles

- **Fixed or judgement.** A rewrite that only changes how a fact is written is **fixed**, and Setup applies it without a question. A rewrite that decides what state the work is really in is a **judgement**, and belongs to the user.
- **Rules that change what Frontier reads apply in every Feature**, resolved and abandoned ones included.
- **Prose tidying applies only to Features in play, and strips only the clause.** Prose tidying is the `Triage:`, `Label:` and `Map:` rows: text Frontier ignores. A Feature is **in play** when it is neither Complete nor Abandoned once this run's status rows and answers are applied: neither its `spec.md` nor its `map.md` has `status: resolved` or `wont-do`. A Feature with neither file is in play too.
- **Only front matter values and the matched prose lines change.** Body prose stays, legacy strings quoted in it included.

## The rules

The status rules apply to `status` on `spec.md` and `map.md` as well as on Tickets.

| Case | Rule | Kind |
|---|---|---|
| `done`, `complete`, `closed` | → `resolved` | fixed |
| `wontfix`, `wont-fix` | → `wont-do` | fixed |
| `needs-info` | → `needs-human` | fixed |
| Missing Ticket status | → `needs-triage` | fixed |
| `blocked` | propose `ready-for-agent`; the blocked state is derived | judgement |
| `in-progress`, `wip` | propose `ready-for-agent`; `claimed` would claim live work | judgement |
| Any other unknown status | propose `needs-triage` | judgement |
| Prose `Status:`, `**Status:**`, `Blocked by:` or `Type:` line | moved into front matter and deleted from the body | fixed |
| `Blocked by:` of `—` or "None", or zero-padded numbers | `[]`, or plain integers | fixed |
| `Blocked by:` naming a Ticket by exact title | resolved to its number | fixed |
| `Blocked by:` title with no exact match | propose the closest Ticket | judgement |
| `Dependencies:` line naming a Ticket in the same Feature that `blocked_by` lacks | propose adding it to `blocked_by` | judgement |
| `Dependencies:` line that only repeats `blocked_by` | left alone | fixed |
| Blocker outside the Feature, in a prose `Blocked by:` or a `Dependencies:` line, on a Ticket neither `resolved` nor `wont-do` | propose `needs-human`, with the prose kept as a dated line under `## Comments` | judgement |
| Blocker outside the Feature on a `resolved` or `wont-do` Ticket | no status change; the outside name is dropped when a prose `Blocked by:` moves into front matter | fixed |
| Ticket numbered `00` | left as is | fixed |
| Filename Frontier rejects (`1-foo.md`, `001-foo.md`) | renamed to two digits when the number is unambiguous | fixed |
| Unnumbered Ticket file | propose numbers in Blocked-by order | judgement |
| Two Tickets with the same number | propose the next free number for the later one, and ask which one each `blocked_by` meant | judgement |
| Ticket H1 written `NN — Title` | `NN: Title` | fixed |
| All boxes ticked, status not `resolved` | propose `resolved` only when a commit in the checked-out history names the Ticket; otherwise propose no change and say why | judgement |
| Spec in play, implemented, with a remainder in prose | ask. Default: a new `needs-triage` Ticket when the remainder is within the Spec's scope; a new Intent-only Feature, with this Spec `resolved`, when it reaches beyond. Nothing is written until answered | judgement |
| Resolved Spec with a remainder in prose | left alone | fixed |
| `spec.md` or `map.md` with no front matter fence | add the empty fence | fixed |
| Tickets but no `spec.md`, `map.md` or `intent.md` | propose a `spec.md` with the empty fence and one line pointing at wherever the spec lives; ask if it can't be found | judgement |
| Intent and Tickets, but no Spec or Map | left alone and listed | fixed |
| Both `spec.md` and `map.md` | ask which one the Tickets came from, and rename the other to a name Frontier ignores, such as `notes.md` | judgement |
| Directory with no Tickets and no Feature file | left alone and listed | fixed |
| `intent.md` with no fence, or with a prose `Status:` line | left alone | fixed |
| Loose files at the Feature root, or extra files in a Feature | left alone | fixed |
| `Triage:` clause in a Spec's body (Features in play only) | strip the clause, keep the rest of the paragraph | fixed |
| `Label:` or `Map:` line (Features in play only) | deleted | fixed |

## Applying the rules

Work Feature by Feature. The sections below add what the rows leave open. The rewrite of a Feature is done when every Ticket, Spec and Map in it fits every fixed row, and every row of its judgement table is written as answered.

A judgement case decides only the write that settles it. Every fixed row still applies around it, in the same file: a prose `Status:` line on an all-ticked Ticket still moves, since that case asks only whether the status should change afterwards.

### Front matter

- **A missing fence** is added as the file's first lines, with one blank line between it and the file's first line of text.
- **A key** is added after the keys already in the fence, in the order `status`, `blocked_by`, `type`, written as the contract's Front matter section says.
- **A legacy status** in front matter is rewritten in place by the status rows.
- **A Spec or Map** takes only `resolved` and `wont-do`: any other status from the vocabulary, moved there or answered, says the Feature is in play, which the contract writes as no `status`.
- **A missing Ticket status** means neither the front matter nor a prose `Status:` line holds one.

### Prose field lines

A prose field line opens a body line with the field's name and a colon, bare or bold: `Status:`, `**Status:**` or `**Status**:`, and likewise `Blocked by:` and `Type:`. It sits outside front matter and fenced code. A list item, a heading, a quote and a mention inside a sentence are body prose. A field's value runs on over the lines that continue its sentence, up to a blank line, the next field line such as `Jira:`, or other markup such as an HTML comment.

`Status:` moves on Tickets, Specs and Maps; `Blocked by:` and `Type:` on Tickets. When the front matter already holds the field's key, the line is body prose and stays. Otherwise the field moves:

- **`Status:`**: the status is the value's first word, with any `**`, backticks or trailing punctuation removed, so `**done** — shipped` reads `done`. A status from the vocabulary, or a legacy one mapped by the status rows, goes to `status`, and any other word is a judgement case: an answer that settles a status moves the line with it. On a Spec or Map, the line moves out even when the status is written as none.
- **`Blocked by:`**: the list is the value's leading run of items, split on commas and "and". An item is a Ticket number, zero-padded or not, with any text after it; `—` or "None", with any text after it, for no blocker; or the exact title of a Ticket in the Feature, which is its H1 without the number. The list ends at the first item that is none of these. `blocked_by` gets the list's numbers as plain integers, `[]` when it has none. On a Ticket neither `resolved` nor `wont-do`, the line is a judgement case when the rest of its text names a Ticket of the Feature that the list lacks, by number or by a title with no exact match, or names something outside the Feature that the Ticket waits on. Its `blocked_by` and the line are then written as the answer settles.
- **`Type:`**: the value's first word goes to `type`.

The moved line is then deleted, unless its text says more than the value it moved. Then the line stays where it was, verbatim, with its label replaced by a bare `Note:`, so `` **Status:** done — commit `abc1234` `` becomes `` Note: done — commit `abc1234` ``. Two kinds of `Blocked by:` text say nothing more, and go with the line: text that restates the list, such as a listed Ticket's title or "can start immediately"; and, on a `resolved` or `wont-do` Ticket, the name of a blocker outside the Feature.

A deleted line takes one blank line with it when it stood alone, so paragraphs stay one blank line apart.

### Ticket files and titles

- **A rejected filename's number is unambiguous** when no other file in `issues/` already has it. Rename the file with a plain `mv <old> <new>`, run as a command of its own: a file rename that leaves git's index alone. The renamed file then gets the rules like any Ticket.
- **A rename an answer settles** is made the same way, before the fixed rows, so every file gets the rules of the name it ends with: a renamed Ticket gets the Ticket rules, and a `spec.md` renamed to `notes.md` gets none.
- **The H1** `# NN — Title` becomes `# NN: Title`, for an em dash, an en dash or a hyphen between the number and the title. Only the Ticket's first H1 outside fenced code is its title.
- **A Feature with an Intent and Tickets but no Spec or Map** gets no Feature file, but its Tickets get the Ticket rules.

### Prose tidying

In a Feature in play:

- **The `Triage:` clause** in `spec.md` is `Triage:`, its label, and the period that ends the clause with the space after it. Strip the clause and keep the rest of its line and paragraph: `` Triage: `ready-for-agent`. Grilled from… `` keeps "Grilled from…". A line left empty goes with it.
- **A `Label:` or `Map:` line** in a Ticket, Spec or Map is deleted, with the lines that continue it. A Map's H1, `# Map: …`, is a heading and stays.

## Judgement cases

Each judgement case becomes a row in its Feature's judgement table, with the proposal its rule gives.

### The judgement table

There is one table per Feature, holding only judgement rows:

| File | Field | Old | New | Rule |
|---|---|---|---|---|
| `issues/04-sync.md` | `status` | `in-progress` | `ready-for-agent` | `in-progress`, `wip` |

- **File**: the path inside the Feature, or the Feature's directory for a row about the whole Feature.
- **Field**: the front matter key, the file name, or the prose line the row would change.
- **Old** and **New**: the value as written, and the proposed one.
- **Rule**: the Case of the rules row it falls under.

Below the table goes each row's question with its proposed answer, numbered in row order across the whole message, such as "3. Is anyone working on `04` right now? Proposed: no, so `ready-for-agent`." The user can then answer "all as proposed, except 3".

An answer takes the proposal, gives another value, or leaves the case as it is. The rewrite writes the value the answer settles on, and nothing for a case left as it is.

### What the rows leave open

- **Statuses**: on a Map Ticket, which carries `type`, the proposal `ready-for-agent` is `open` instead, the status Map Tickets start with.
- **Order text** is a `Dependencies:` line, or the text after a prose `Blocked by:` list, naming a Ticket of the Feature. It makes a row only when the Ticket it sits on is neither `resolved` nor `wont-do`, since the order of a `resolved` or `wont-do` Ticket decides nothing, whichever Ticket the text names. A `Dependencies:` line stays where it is. Text that orders this Ticket first, such as "must land before 05", proposes this Ticket's number for the other Ticket's `blocked_by`, and text calling two Tickets independent names no blocker.
- **Outside blockers**: the text as written becomes a Comment dated today, by the contract's Comment operation, unless a Comment already holds it. A prose `Blocked by:` line then moves with its text in that Comment rather than a `Note:` line. A Ticket the status rows already leave `needs-human` already waits on a human, so its case gets no row, and the Comment and the line are written all the same.
- **Unnumbered Tickets** take the next free numbers in front of their names, `zeta.md` becoming `05-zeta.md`, in Blocked-by order: a Ticket comes after every Ticket it names as a blocker.
- **Two Tickets with one number**: the later one is the one the checked-out history added later (`git log --diff-filter=A -- <file>`), or the later filename when history can't tell. The number in its H1 changes with its filename. Each `blocked_by` in the Feature that names the shared number gets a row of its own, proposing the Ticket that keeps the number, unless the blocked Ticket's text names the other one.
- **All boxes ticked**: a `wont-do` Ticket gets no row, since Frontier doesn't flag it. A commit names the Ticket when its message holds the Ticket's filename, its Title, or its number beside its Feature's slug or an id the slug carries, such as `SPA2-749` in `spa2-749-stale-water-temperature`. Look with `git log -i --grep`. The question names the commit, or says that none names the Ticket.
- **A Spec in play with a remainder**: the Spec is implemented when every Ticket is `resolved` or `wont-do` with this round's proposals taken, or, with no Tickets, when its own text says the work shipped; an answer that changes this drops the row. A remainder is text in the Spec saying part of its work is still open, and an Out of Scope section holds none. The question quotes the remainder.
  - **The new Ticket** takes the next free number, with `blocked_by: []`, an H1 naming the remainder, and a body quoting it with a link to `../spec.md`.
  - **The new Feature**'s slug is proposed in the question. Its `intent.md` holds the remainder as its question and names this Spec as what it inherits.
- **Tickets with no Feature file**: the new `spec.md` holds the empty fence, an H1 with the Feature's title, and one line pointing at where its Spec lives: a file, another Feature, or an entry in the team's tracker of record. Look for it in what the Feature's files and slug name. When nothing names one, the question asks where it lives, and proposes leaving the Feature as it is.
- **Both a Spec and a Map**: the Tickets came from the Map when they carry `type` or `open`, which only Map Tickets have, and from the Spec otherwise. A pointer this run writes to the renamed file uses its new name.

## In the report

The rewrite adds to the report:

- **Files changed**: per Feature, each file written or renamed, and the rows applied to it, fixed or answered.
- **Left outside the contract**: each judgement row whose answer wrote nothing, with that answer, then every directory a row marks "left alone and listed": each Feature with an Intent and Tickets but no Spec or Map, and each directory with no Tickets and no Feature file, an empty one included.
