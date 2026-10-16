# ADR-0001: Record architecture decisions

**Status:** Accepted
**Date:** 2026-10-16
**Deciders:** Tech Lead

## Context
`CLAUDE.md` and `README.md` describe the *current* state well, but they flatten
history: a reader cannot tell which statements are deliberate decisions with
weighed alternatives, which are conventions, and which were reversals of earlier
choices. As the team grows across two platforms we need a durable record of *why*
the architecture is the way it is, not just *what* it is.

## Decision
We will capture significant architectural decisions as **Architecture Decision
Records (ADRs)** in `docs/adr/`, one Markdown file per decision, using the
lightweight Michael Nygard format captured in [template.md](template.md).

- Files are named `NNNN-kebab-case-title.md`, numbered sequentially.
- ADRs are immutable once **Accepted**. To change a decision we add a new ADR
  and mark the old one **Superseded by ADR-XXXX**.
- Decisions already in force are recorded retroactively with **Status: Accepted**
  and the date they were written down (not invented historical dates).
- `CLAUDE.md` / `README.md` remain the "how it works now" reference; ADRs are the
  "why we chose it" record. Where an ADR supersedes prose in those files, the
  prose links to the ADR rather than duplicating the rationale.
- ADRs have no "Action Items" section. A record is a snapshot, not a task
  tracker. Forward-looking intent goes in **Consequences → "Revisit when …"**;
  real tasks go in the issue tracker.

## Options Considered

### Option A: ADRs in-repo (Markdown) — chosen
**Pros:** Versioned with the code; reviewable in PRs; no external tool; works
offline and in the agent context. **Cons:** No built-in cross-linking/search UI.

### Option B: Confluence / wiki pages
**Pros:** Rich editing, org-wide discoverability. **Cons:** Drifts from the code,
not in PR review flow, extra access management, not visible to coding agents.

### Option C: Keep decisions inline in `CLAUDE.md`
**Pros:** Zero new structure. **Cons:** No history, no alternatives captured,
grows unbounded, conflates "what" with "why".

## Trade-off Analysis
In-repo Markdown keeps the decision log next to the artefact it governs and
inside the review process that changes it, which is worth more than the richer
editing of a wiki. It is also directly readable by AI coding agents.

## Consequences
- New significant decisions require a small writing step before or during the PR
  that introduces them.
- Reviewers gain a stable reference to cite ("this violates ADR-0002").
