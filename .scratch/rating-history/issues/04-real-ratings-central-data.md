---
status: ready-for-agent
blocked_by: [3]
---

# 04: Real Ratings Central data in `dev`

**What to build:** a `dev` build shows a real Player's Rating history from Ratings Central. The Player's Ratings Central ID is read from the Player ranking of one of their Leagues, and the history from Ratings Central's CSV export. See the spec's "Ratings Central ID" and "Ratings Central data source", and ADR-0010.

**Blocked by:** 03

- [ ] `RankedPlayer` carries a nullable Ratings Central ID mapped from `player_rc_id`; an empty value maps to null; the Player ranking on the League screen is unchanged
- [ ] The use case resolves the ID through the Player ranking, trying the next League when the Player has no row there
- [ ] `:core:ratings-central` reads the CSV export through the shared Ktor client with a `User-Agent` naming the app and version; the base URL comes from `AppConfig`
- [ ] Columns are read by header name and extra columns ignored; a non-CSV or headerless response is unavailable and logged with Kermit, without the Player's name
- [ ] Each history is held in memory for 24 hours, a failed refresh serves the held entry, and identical concurrent requests share one call
- [ ] Koin modules registered in `:core:di` for `dev`, `beta` and `prod`; `deb` keeps the fake
- [ ] Tests: the recorded standings sample covers a present and an empty ID; a recorded CSV of a real Player via `MockEngine`, plus an HTML response, a missing column, an extra column, the held entry and the stale fallback
- [ ] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: `RankedPlayer.ratingsCentralId` and its XTTV mapping (with tests) were done in ticket 03; the first two criteria here only need checking. To keep `deb` on the fake, bind the Ratings Central module in `:core:di` for every environment except `deb` (the fake directory is bound before `initKoin`'s modules, so anything bound there wins over it).
