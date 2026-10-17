# 03: Clubs from XTTV

**What to build:** Club search and lookup use real XTTV data. A user can search for their Club by name, open it and set it as My Club, and the Club's Association (and so its XTTV host) is derived automatically.

**Blocked by:** 02 Associations from XTTV.

**Status:** ready-for-agent

- [ ] `ClubRepository.getClub` and `searchClubs` are backed by the Club list and Club details from XTTV.
- [ ] A blank query lists everything; otherwise results match on Club name, as today.
- [ ] A Club resolves its Association, and requests for it go to that Association's host.
- [ ] A Club XTTV does not know returns `null`; a failed request throws `DataUnavailable`.
- [ ] Results are cached with the Club TTL; a failed refresh serves the stale entry.
- [ ] Tests run through `ClubRepository` on `MockEngine` with recorded XML.
