---
status: ready-for-human
blocked_by: []
---

# 01: Ask Ratings Central for permission to use the history export

**What to build:** nothing in code. Ratings Central's robots.txt disallows the history export the Rating history reads (ADR-0010), so release builds keep the section off until its maintainers agree.

**Blocked by:** None (can start immediately)

- [ ] Send the draft email from the spec's Further Notes to Ratings Central's maintainers
- [ ] Note their answer here, with any request rate or credit wording they ask for
- [ ] On yes: open a ticket to turn the Rating history flag on for `beta` and `prod` and apply their conditions
- [ ] On no: open a ticket to replace the section with a link to the Player's graph page, and mark ADR-0010 superseded

## Comments
