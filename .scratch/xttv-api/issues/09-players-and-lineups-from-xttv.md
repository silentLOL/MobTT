---
status: resolved
blocked_by: [3, 10]
---

# 09: Players and lineups from XTTV

**What to build:** Player search, Player lookup and the Team lineup come from XTTV, and `TeamDetail` is finalized. The data layer is shaped by what XTTV actually exposes, because the current shape is a guess.

- [x] `PlayerRepository.searchPlayers` and `getPlayer` are backed by XTTV, matching on Player name or Club name, as today.
- [x] `TeamRepository.getLineup` is backed by XTTV; a Team with no Players listed returns an empty lineup.
- [x] `TeamDetail` and `LineupPlayer` take their final shape and the PROVISIONAL markers are removed.
- [x] Favorites and My Player keep resolving against the new Player IDs.
- [x] A failed request throws `DataUnavailable`; results are cached.
- [x] Tests run through the repository interfaces on `MockEngine` with real recorded XML.
- [x] The glossary is updated if XTTV's Player terms differ from ours.

## Comments

- 2026-10-30: Blocked by: 03 Clubs from XTTV, and an XTTV answer on how Players and lineups are exposed (from the registration reply or ticket 10).
- 2026-10-31: Confirmed done by the project owner.
