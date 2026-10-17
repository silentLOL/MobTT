# 13: Team detail: full standing and lineup

**What to build:** tapping a Team opens its detail, showing the full League table with the Team highlighted and the Team's lineup of Players.

**Blocked by:** 10, 11

**Status:** ready-for-human

Built against the fake directory at the owner's explicit request, although the XTTV docs are still missing. `TeamDetail`, `LineupPlayer` and `TeamRepository.getLineup` are **provisional**: the lineup content and the full League table shape are not in the documented API (spec "Blocked on API"). Revisit them when the XTTV docs or sample responses arrive; the real repository may reshape them. Lineup rows are not tappable because the Player profile does not exist yet.

- [x] Team detail is reachable from a Team on the Club screen on both platforms
- [x] The full League table shows each Team's standing, with this Team visually and accessibly marked (not by colour alone)
- [x] The lineup lists the Team's Players (rows open the Player profile once it exists; it does not yet)
- [x] UseCase-seam tests against fake repositories cover standing order and an empty lineup
- [x] Previews cover loaded, empty lineup and error on both platforms
- [ ] Accessibility rules and 200% text scaling hold on both platforms (previews include 200%; not yet inspected visually)
