---
status: resolved
blocked_by: [10, 11]
---

# 13: Team detail: full standing and lineup

**What to build:** tapping a Team opens its detail, showing the full League table with the Team highlighted and the Team's lineup of Players.

Built against the fake directory at the owner's explicit request, although the XTTV docs are still missing. `TeamDetail`, `LineupPlayer` and `TeamRepository.getLineup` are **provisional**: the lineup content and the full League table shape are not in the documented API (spec "Blocked on API"). Revisit them when the XTTV docs or sample responses arrive; the real repository may reshape them. Lineup rows are not tappable because the Player profile does not exist yet.

- [x] Team detail is reachable from a Team on the Club screen on both platforms
- [x] The full League table shows each Team's standing, with this Team visually and accessibly marked (not by colour alone)
- [x] The lineup lists the Team's Players (rows open the Player profile once it exists; it does not yet)
- [x] UseCase-seam tests against fake repositories cover standing order and an empty lineup
- [x] Previews cover loaded, empty lineup and error on both platforms
- [x] Accessibility rules and 200% text scaling hold on both platforms (previews include 200%; not yet inspected visually)

## Comments
- 2026-10-30: Android 200% font-scale previews inspected (Team detail, loaded): standing and lineup wrap cleanly; the "This Team" badge sits slightly indented under the row text, cosmetic only. iOS half not inspected, so left unchecked.
- 2026-10-30: Hero star layout fixed on Android (d545382) and iOS (c59e3f6) for Team detail: star overlaid top-end, name at full width. iOS not yet seen in Xcode previews. Still unchecked: iOS Dynamic Type and VoiceOver, TalkBack on a device.
- 2026-10-31: Ticket text predates the XTTV API: Club, Team, Fixture and standing data now come from live XTTV (xttv-api), the fake directory is bound only in `deb` previews, and the Player profile exists (player-detail-teams). All build work is done; the only open box is the device pass (iOS Dynamic Type and VoiceOver, TalkBack). Status set to ready-for-human.
- 2026-10-31: Resolved at the project owner's request without a device pass; VoiceOver, Dynamic Type and TalkBack were not run by the agent.
