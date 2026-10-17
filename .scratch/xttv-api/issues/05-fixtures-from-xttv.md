# 05: Fixtures from XTTV

**What to build:** Home's upcoming Fixtures and the Club screen's next Fixture are real. Finished Fixtures show their score (Matches won), scheduled ones show when and, where XTTV says so, where.

**Blocked by:** 04 Teams from XTTV.

**Status:** ready-for-agent

- [ ] `FixtureRepository.getFixtures` returns every Fixture a Team plays in, home or away, built from XTTV's club and league "game" data; "Game" appears only in the data layer.
- [ ] Finished Fixtures carry their Matches-won score; scheduled ones do not.
- [ ] The venue is `null` when XTTV does not give one.
- [ ] Times are parsed correctly for Austrian local time, including the daylight-saving switch.
- [ ] Results are cached with the Fixture TTL; a failed refresh serves the stale entry; a failed request with nothing cached throws `DataUnavailable`.
- [ ] Tests run through `FixtureRepository` on `MockEngine` with recorded XML.
