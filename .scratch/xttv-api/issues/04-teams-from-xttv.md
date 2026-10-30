---
status: resolved
blocked_by: [3]
---

# 04: Teams from XTTV

**What to build:** The Club screen's Teams list and Team lookup show real Teams. A Club with no Teams shows an empty list, never an error.

- [x] `TeamRepository.getTeams` and `getTeam` are backed by XTTV's league-team and team-detail data, mapping each Team to its Club and League.
- [x] A Club with no Teams returns an empty list; an unknown Team returns `null`; a failed request throws `DataUnavailable`.
- [x] Results are cached with the Team TTL; a failed refresh serves the stale entry.
- [x] `getLineup` still returns the fake data and its PROVISIONAL marker stays until ticket 09.
- [x] Tests run through `TeamRepository` on `MockEngine` with recorded XML.
