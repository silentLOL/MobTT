## Tracker contract

These rules point at the operations in `docs/agents/issue-tracker.md` and win over any template or step inside a skill:

- Ticket files follow the "Ticket file format", which wins over any ticket template inside a skill.
- Working a ticket starts with "Claim", the first write before any other work, and ends with "Resolve". The commit that finishes the ticket's work, after any review fixes, carries the Resolve: tick its boxes and set `status: resolved` before running `git commit`. A ticket is never left `claimed` once its work is committed.
- A map with no open tickets and nothing under "Not yet specified" is completed by "Complete the map".
