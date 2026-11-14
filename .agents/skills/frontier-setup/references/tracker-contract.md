# Tracker contract

Version 1.

Frontier is a Mac app that shows a Repository's work, read from markdown files. This contract is the shape those files take: where they live, what each one holds, what Frontier derives from them, and the operations that change them. Frontier only reads. Agents and people write: new files in the formats below, and every later change through the operations at the end. Written this way, a Repository reads in Frontier with nothing flagged beyond the flags under What Frontier derives, which point at work someone should look at.

The words used throughout:

- **Repository**: a git repository. Each checked-out working directory of it is a **Worktree**: the main Worktree, which owns `.git`, and any linked ones.
- **Feature**: one unit of work, kept in a directory of its own.
- **Ticket**: one piece of a Feature's work, kept in a file of its own.

## Feature root and `.frontier.yml`

Features live under the **Feature root**: `.scratch/` at the top of the Repository, unless `.frontier.yml` names another.

`.frontier.yml` sits at the top of the Repository:

```yaml
contract: 1
features: docs/work
```

- **`contract`**: the contract version the Repository was set up against, a positive integer. Missing reads as the current version.
- **`features`**: optional. The Feature root, as a path relative to the top of the Repository and inside it. `.` is the top itself. A Feature root that doesn't exist yet reads as no Features.
- **The format** is one `key: value` per line, with the value bare, unquoted. Blank lines and `#` comments are allowed. Keys Frontier doesn't know are ignored.
- **With no `.frontier.yml`**, both keys take their defaults.
- **Frontier reads the file from the main Worktree**, and its Feature root applies to every Worktree.

Frontier still loads a Repository whose file it can't use, and flags the Repository:

- a line that isn't `key: value`, or a `contract` that isn't a positive integer, makes the whole file malformed, and both keys take their defaults;
- a `features` that is empty, absolute, starts with `~`, or climbs out of the Repository through `..` is ignored, and the default Feature root is read;
- a `contract` newer than Frontier knows is read anyway, with a prompt to update Frontier.

## The files of a Feature

A Feature is one directory, `<feature-root>/<slug>/`, and its slug is its name. Frontier reads these files in it, and no others:

| Path | Holds |
|---|---|
| `intent.md` | the **Intent**: the question the Feature answers, what it inherits and what it leaves open |
| `spec.md` | the **Spec**: what to build and why, decided as a whole |
| `map.md` | the **Map**: the work charted step by step, each step a Ticket |
| `issues/NN-<slug>.md` | one **Ticket** per file |

- A directory holding none of these is not a Feature.
- A Feature has a Spec or a Map, never both. Frontier flags a Feature that has both.
- Its Tickets come from that Spec or Map. Frontier flags a Feature with Tickets and neither, whether or not it has an Intent.
- An Intent is where a Feature starts. It stays beside the Spec or Map once one is written, as the record the Spec or Map came from.
- Every file is UTF-8 text. Frontier flags a Ticket file it can't read.

## Front matter

Structured properties live in front matter and nowhere else:

- **The fence**: the file's first line is `---`, and a later `---` line closes it. Each fence line holds nothing else, trailing spaces included. A fence that never closes is no fence.
- **Properties**: one `key: value` per line between the fences, with the value bare: `status: resolved`, unquoted and with no trailing comment.
- **Lists** are written on one line: `blocked_by: [1, 2]`.
- **Other keys** are ignored by Frontier and kept in place.
- **The body**, everything after the closing fence, is free markdown.

## Feature file format

`spec.md` and `map.md` open with a front matter fence, written when the file is created. A new Feature's fence is empty:

```markdown
---
---

# <Feature title>
```

The fence holds one optional key, `status`:

- `resolved`: the Feature is complete. Written by Complete a Feature.
- `wont-do`: the Feature is abandoned. Written by Abandon a Feature.
- Absent: the Feature's Tickets decide its state. Any other value reads as absent.

`intent.md` carries no status and needs no fence. Frontier shows its text and reads nothing else from it, so a `status` written there is ignored.

## Ticket file format

```markdown
---
status: ready-for-agent
blocked_by: [1, 2]
---

# 03: <Ticket title>

## <any heading>

Free markdown. Acceptance criteria are checkboxes:

- [ ] a criterion

## Comments

- 2026-10-09: a dated note
```

- **Identity**: a Ticket is a file directly under `issues/` named `NN-<slug>.md`, with exactly two digits, a hyphen and a slug. The number is the Ticket's identity. Frontier skips any other file under `issues/` silently, so a misnamed Ticket such as `1-foo.md` or `003-foo.md` is invisible.
- **Numbering**: a new Feature numbers its Tickets from `01`, in the order they can be worked. Any number from `00` to `99` is valid, so a Feature that already starts at `00` keeps it. Within a Feature, each number names one file: Frontier reads files that share a number, in one Worktree or across several, as copies of one Ticket, and shows only one of them.
- **`status`**: a string from the Status vocabulary. A missing `status` key reads as `needs-triage`; an empty value is an unknown status.
- **`blocked_by`**: the Tickets of the same Feature that block this one, as plain integers: `blocked_by: [1, 2]` names Tickets `01` and `02`, and `[0]` names `00`. Written `[]` when there are none. A Ticket waiting on something outside its Feature is `needs-human` instead, with what it waits on as a Comment.
- **`type`**: on Map Tickets only, one of `research`, `prototype`, `grilling` or `task`. Spec Tickets carry none.
- **Title**: the first H1 is how the Ticket reads in Frontier, a display hint only. Frontier strips a leading number followed by `:` or `.`, so `# 03: Add login` reads as "Add login". Without an H1, the Ticket reads as the slug from its filename.
- **Checkboxes**: every `- [ ]` and `- [x]` item counts toward the Ticket's progress, except under a `## Comments` or `## Answer` heading, up to the next `#` or `##` heading.
- **`## Comments`**: dated notes, appended at the bottom of the file.
- **Fenced code**: an H1, heading or checkbox inside a fenced code block is code, so a Ticket can quote this format without changing its own title or criteria.
- **No front matter**: the file is still a Ticket, read as `needs-triage` with no blockers, and flagged.

## Status vocabulary

The strings a Ticket's `status` takes, from least to most advanced:

| `status` | Meaning |
|---|---|
| `needs-triage` | Not yet evaluated. |
| `needs-human` | Waiting on a human to answer, decide or act, for example on a question raised mid-work or a report too vague to act on. |
| `open` | A Map Ticket nobody has claimed. Map Tickets start here; Spec Tickets start with a triage role. |
| `ready-for-human` | Specified, and the work is a human's. |
| `ready-for-agent` | Specified, and an agent can do it unattended. |
| `claimed` | An agent is working on it. |
| `resolved` | The work is done. |
| `wont-do` | The work will not be done. Terminal. |

The five **triage roles** are `needs-triage`, `needs-human`, `ready-for-agent`, `ready-for-human` and `wont-do`.

Any other string is an unknown status. Frontier flags it; the Ticket is Waiting when nothing blocks it, and it blocks its dependents.

## What Frontier derives

Frontier derives a state for each Ticket and each Feature from the written properties above. Files hold only those properties; the states live in Frontier.

### Ticket states

A Ticket takes the first state that fits:

1. **Wont do**: `status: wont-do`.
2. **Done**: `status: resolved`, and nothing else. Ticked checkboxes never make a Ticket Done.
3. **Claimed**: `status: claimed`, whatever its blockers.
4. **Blocked**: a Ticket in its `blocked_by` is neither `resolved` nor `wont-do`, or it sits in a cycle of `blocked_by` references, a Ticket naming itself included. So a `wont-do` blocker releases the Tickets it blocks, as a `resolved` one does.
5. **Frontier**: unblocked, and `open`, `ready-for-agent` or `ready-for-human`. The work that can start now.
6. **Waiting**: unblocked, and `needs-triage`, `needs-human` or an unknown status.

Frontier flags a Ticket that is neither `resolved` nor `wont-do` when:

- it has checkboxes and every one is ticked, since its Resolve is still missing;
- a Ticket in its `blocked_by` is `wont-do`, so someone checks the Ticket still makes sense without that work;
- it sits in a `blocked_by` cycle;
- it is `claimed` while a Ticket in its `blocked_by` is neither `resolved` nor `wont-do`.

Frontier also flags any Ticket whose `blocked_by` names a number that no Ticket file in the Feature carries. That number blocks nothing.

### Feature states

A Feature takes the first state that fits:

1. **Complete**: `status: resolved` on its Spec or Map, whatever its Tickets say.
2. **Abandoned**: `status: wont-do` on its Spec or Map.
3. **Unspecified**: an Intent and neither a Spec nor a Map, whatever its Tickets say. The Feature waits for its Spec or Map.
4. **Awaiting verification**: every Ticket is `resolved` or `wont-do`, and at least one is `resolved`. The result waits to be checked against the Spec or Map before Complete a Feature.
5. **Active**: anything else, including a Feature with no Tickets.

### Worktrees

Frontier reads the Feature root of every Worktree and merges what it finds. Of several copies of one Ticket, it shows the copy whose status comes furthest down the Status vocabulary, the main Worktree's on a tie, so a Claim made in a linked Worktree shows before it is merged. Of several copies of an Intent, a Spec or a Map, it shows the main Worktree's copy when there is one.

## Operations

Work on a Feature moves through these operations, each a write to the files above.

### Claim

Set the Ticket's `status: claimed`. This is the first write when work on a Ticket starts, before any other.

### Resolve

Tick the Ticket's checkboxes and set `status: resolved`, in the commit that finishes the Ticket's work.

### Close

Set the Ticket's `status: wont-do`, for work that will not be done or turns out to be out of scope.

### Apply a triage role

Write the role's string to the Ticket's `status`.

### Comment

Append a dated line under the Ticket's `## Comments`, adding the heading at the bottom of the file when it is missing.

### Ask a human

Set a `claimed` Ticket to `status: needs-human`, and write the question as a Comment.

### Hand back

Once the human has acted, set the `needs-human` Ticket to `status: ready-for-agent`, or `status: open` on a Map Ticket.

### Complete a Feature

Set `status: resolved` in the Feature's `spec.md` or `map.md`.

### Abandon a Feature

Set `status: wont-do` in the Feature's `spec.md` or `map.md`.
