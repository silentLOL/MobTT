# Issue tracker: Local Markdown

Specs, Maps and Tickets for this Repository live as markdown files under the Feature root: `.scratch/`, unless `.frontier.yml` names another directory. Their format, and every operation that changes them, is the Tracker contract in [`.agents/skills/frontier-setup/references/tracker-contract.md`](../../.agents/skills/frontier-setup/references/tracker-contract.md).

This doc names the contract's operations, the four the project rules point at among them, and translates each verb the skills use onto one.

## Operations

Each name links to its definition in the contract.

- **Ticket file format**: the [Ticket file format](../../.agents/skills/frontier-setup/references/tracker-contract.md#ticket-file-format). A Spec or Map follows the [Feature file format](../../.agents/skills/frontier-setup/references/tracker-contract.md#feature-file-format), and every `status` is a string from the [Status vocabulary](../../.agents/skills/frontier-setup/references/tracker-contract.md#status-vocabulary).
- **Claim**: [Claim](../../.agents/skills/frontier-setup/references/tracker-contract.md#claim).
- **Resolve**: [Resolve](../../.agents/skills/frontier-setup/references/tracker-contract.md#resolve).
- **Complete the map**: [Complete a Feature](../../.agents/skills/frontier-setup/references/tracker-contract.md#complete-a-feature), on the Feature's `map.md`.
- **Close**: [Close](../../.agents/skills/frontier-setup/references/tracker-contract.md#close).
- **Apply a triage role**: [Apply a triage role](../../.agents/skills/frontier-setup/references/tracker-contract.md#apply-a-triage-role), with the string from `triage-labels.md`.
- **Comment**: [Comment](../../.agents/skills/frontier-setup/references/tracker-contract.md#comment).
- **Ask a human**: [Ask a human](../../.agents/skills/frontier-setup/references/tracker-contract.md#ask-a-human), and once the human has acted, [Hand back](../../.agents/skills/frontier-setup/references/tracker-contract.md#hand-back).
- **Abandon a Feature**: [Abandon a Feature](../../.agents/skills/frontier-setup/references/tracker-contract.md#abandon-a-feature).

## When a skill says "publish to the issue tracker"

Write a new file in the Feature's directory, `<feature-root>/<feature-slug>/`, creating it if needed:

- **A Spec** goes to `spec.md`, and **a Map** to `map.md`. Each opens with the empty front matter fence of the Feature file format.
- **A Ticket**, and a sub-issue of a Spec or Map, goes to `issues/NN-<slug>.md` in that Feature, in the Ticket file format. Number new Tickets from `01` in the order they can be worked.
- **A template's `Status:`, `Blocked by:` or `Type:` line** becomes the `status`, `blocked_by` or `type` front matter key. "None" is `[]`, and a blocker named by its title is written as its number.
- **A triage label the skill applies** is Apply a triage role on a Ticket. A Spec or Map takes no triage role: its fence holds only what Complete a Feature or Abandon a Feature writes.

## When a skill says "fetch the relevant ticket"

Read the file at the referenced path. The user will normally pass the path, or the Feature and the Ticket's number.

## Triage

- **Query the issue tracker**: read the Tickets under the Feature root and group them by `status`.
- **A state role** (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`): Apply a triage role. Closing an issue as `wontfix` is Close.
- **A category role** (`bug`, `enhancement`): a `category` front matter key on the Ticket, such as `category: bug`. Frontier ignores the key and keeps it.
- **Comment on an issue**: Comment.
- **PRs as a request surface: no.** Requests arrive as Tickets only, so `/triage` never triages a pull request.

## Working a Ticket

- **The way the issue tracker closes work** is Resolve, in the work's own commit. No pull request closes a Ticket.
- **A question for a human mid-work** is Ask a human, and the human's answer is followed by Hand back.

## Wayfinding operations

Used by `/wayfinder`. The **map** is a Feature's `map.md`, and each **child** is a Ticket of that Feature.

- **Map**: `<feature-root>/<feature-slug>/map.md`, holding the Notes / Decisions-so-far / Fog body and opening with the empty front matter fence of the Feature file format. The `wayfinder:map` label is the file's name.
- **Child ticket**: a Ticket file in the map's `issues/`, numbered from `01`, with the question in the body. Its `wayfinder:<type>` label is the `type` front matter key: `research`, `prototype`, `grilling` or `task`. A new child is written with `status: open`.
- **Blocking**: the child's `blocked_by` front matter list. A child is unblocked when it isn't Blocked under the contract's [Ticket states](../../.agents/skills/frontier-setup/references/tracker-contract.md#ticket-states).
- **Frontier**: the children with `status: open` that are unblocked; the lowest number wins.
- **Claim**: Claim. `status: claimed` is the claim, in place of an assignee.
- **Resolve**: append the answer under an `## Answer` heading, Resolve the child, then append a context pointer (gist + link) to the map's Decisions so far.
- **Close it**, for a child ruled out of scope: Close, which releases the children it blocks, then add its line to the map's Out of scope.
- **Complete the map**: once every child is `resolved` or `wont-do` and nothing is under "Not yet specified", Complete a Feature on `map.md`.

## This repository

Notes on how this Repository works beyond the contract. Setup keeps this section as it is.

A bare number ("ticket 03") means the feature whose tickets the latest commits touched. Ask when that is unclear: every feature numbers from `01`.
