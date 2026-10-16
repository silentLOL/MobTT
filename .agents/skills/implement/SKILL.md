---
name: implement
description: "Implement a piece of work based on a spec or set of tickets."
disable-model-invocation: true
---

Implement the work described by the user in the spec or tickets.

Before changing anything, record the base with `git rev-parse HEAD`.

If the user passes a ticket reference, fetch it from the issue tracker and state its title before starting. If the reference is ambiguous, ask.

Change source files with the Edit and Write tools; reserve shell scripts for generated or bulk mechanical edits.

Call the Skill tool with "tdd" where possible, at pre-agreed seams.

Run typechecking regularly, single test files regularly, and the full test suite once at the end. Commands live in `docs/DEVELOPMENT.md`; run the iOS build when the work touches `iosApp/` or `shared-umbrella/`.

Commit your work to the current branch, then call the Skill tool with "code-review" against the recorded base so the review sees exactly this work.
