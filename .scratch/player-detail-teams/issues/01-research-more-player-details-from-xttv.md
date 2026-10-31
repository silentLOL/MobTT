---
status: needs-triage
blocked_by: []
---

# 01: Research further Player details XTTV can give us

**Question:** the Player screen shows only the Club, its Association and the Teams whose lineup lists the Player. What else does XTTV offer per Player?

- [ ] Record a real `getSingleStandings` row and list every field it carries (ranking, wins and losses, points, quota), with the XML kept as a test sample
- [ ] Say which of those fields are per League and which per Player, since a Player can appear in several Leagues
- [ ] Ask XTTV whether `getPlayerDetails`, currently closed to API access, can be opened to us, and note the answer
- [ ] Propose which fields earn a place on the Player screen, without building them

## Comments

- 2026-10-31: split out of the Player screen change (Teams, tappable Club, no hero card), which ships only what the API provably gives us.
