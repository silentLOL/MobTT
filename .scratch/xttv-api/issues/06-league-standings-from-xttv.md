# 06: League standings from XTTV

**What to build:** The League table on Team detail and the Club screen shows real standings, ordered by position.

**Blocked by:** 04 Teams from XTTV.

**Status:** ready-for-agent

- [ ] `LeagueRepository.getLeague` and `getStandings` are backed by XTTV's league and team-standings data.
- [ ] Standings are ordered by position and carry played and points.
- [ ] A Team with no League yields no standings, not an error; a failed request throws `DataUnavailable`.
- [ ] Results are cached with the standings TTL; a failed refresh serves the stale entry.
- [ ] Tests run through `LeagueRepository` on `MockEngine` with recorded XML.
