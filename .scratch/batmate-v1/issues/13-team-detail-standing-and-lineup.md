# 13: Team detail: full standing and lineup

**What to build:** tapping a Team opens its detail, showing the full League table with the Team highlighted and the Team's lineup of Players.

**Blocked by:** 10, 11

**Status:** needs-info

Waiting on the XTTV docs or sample responses. The lineup content and the full League table shape are not in the documented API, and must not be assumed from the prototype's mock data (spec "Blocked on API"). Move to `ready-for-agent` once they are provided.

- [ ] Team detail is reachable from a Team on the Club screen on both platforms
- [ ] The full League table shows each Team's standing, with this Team visually and accessibly marked (not by colour alone)
- [ ] The lineup lists the Team's Players, each opening the Player profile when it exists
- [ ] UseCase-seam tests against fake repositories cover standing order and an empty lineup
- [ ] Previews cover loaded, empty lineup and error on both platforms
- [ ] Accessibility rules and 200% text scaling hold on both platforms
