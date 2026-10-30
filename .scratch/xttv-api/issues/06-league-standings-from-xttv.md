---
status: resolved
blocked_by: [4]
---

# 06: League standings from XTTV

**What to build:** The League table on Team detail and the Club screen shows real standings, ordered by position.

- [x] `LeagueRepository.getLeague` and `getStandings` are backed by XTTV's league and team-standings data.
- [x] Standings are ordered by position and carry played and points.
- [x] A Team with no League yields no standings, not an error; a failed request throws `DataUnavailable`.
- [x] Results are cached with the standings TTL; a failed refresh serves the stale entry.
- [x] Tests run through `LeagueRepository` on `MockEngine` with recorded XML.
