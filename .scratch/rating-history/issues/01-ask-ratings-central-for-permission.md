---
status: resolved
blocked_by: []
---

# 01: Ask Ratings Central for permission to use the history export

**What to build:** nothing in code. Ratings Central's robots.txt disallows the history export the Rating history reads (ADR-0010), so release builds keep the section off until its maintainers agree.

**Blocked by:** None (can start immediately)

- [x] Send the draft email from the spec's Further Notes to Ratings Central's maintainers
- [x] Note their answer here, with any request rate or credit wording they ask for
- [x] On yes: turn the Rating history flag on for `beta` and `prod` and apply their conditions
- [x] On no (not needed): open a ticket to replace the section with a link to the Player's graph page, and mark ADR-0010 superseded

## Comments

- 2026-11-07: The maintainer reports the app may now use the real data in `prod`. No request rate or credit wording was passed on; the app already credits Ratings Central with a link in the info sheet. The flag in `AppConfig` is now on for every environment (`deb` still reads its fake histories), so the Rating history ships in `beta` and `prod` with the next release. If Ratings Central named conditions, record them here.
