---
status: resolved
blocked_by: [2]
---

# 03: Clubs from XTTV

**What to build:** Club search and lookup use real XTTV data. A user can search for their Club by name, open it and set it as My Club, and the Club's Association (and so its XTTV host) is derived automatically.

- [x] `ClubRepository.getClub` and `searchClubs` are backed by the Club list and Club details from XTTV.
- [x] A blank query lists everything; otherwise results match on Club name, as today.
- [x] A Club resolves its Association, and requests for it go to that Association's host.
- [x] A Club XTTV does not know returns `null`; a failed request throws `DataUnavailable`.
- [x] Results are cached with the Club TTL; a failed refresh serves the stale entry.
- [x] Tests run through `ClubRepository` on `MockEngine` with recorded XML.
