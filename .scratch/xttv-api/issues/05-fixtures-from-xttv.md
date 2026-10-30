---
status: resolved
blocked_by: [4]
---

# 05: Fixtures from XTTV

**What to build:** Home's upcoming Fixtures and the Club screen's next Fixture are real. Finished Fixtures show their score (Matches won), scheduled ones show when and, where XTTV says so, where.

- [x] `FixtureRepository.getFixtures` returns every Fixture a Team plays in, home or away, built from XTTV's club and league "game" data; "Game" appears only in the data layer.
- [x] Finished Fixtures carry their Matches-won score; scheduled ones do not.
- [x] The venue is `null` when XTTV does not give one.
- [x] Times are parsed correctly for Austrian local time, including the daylight-saving switch.
- [x] Results are cached with the Fixture TTL; a failed refresh serves the stale entry; a failed request with nothing cached throws `DataUnavailable`.
- [x] Tests run through `FixtureRepository` on `MockEngine` with recorded XML.
