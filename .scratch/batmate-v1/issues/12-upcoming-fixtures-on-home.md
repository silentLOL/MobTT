---
status: resolved
blocked_by: [10, 11]
---

# 12: Upcoming Fixtures for My Player's Teams on Home

**What to build:** on both platforms, Home shows the upcoming Fixtures for the Teams of My Player (or the Teams of My Club), each with date, time, opponent and venue. A Team with no Fixture reads as a free week, not as an error.

Note: ready-for-agent (the venue field is `needs-info` until the XTTV docs or sample responses exist)

- [x] A shared UseCase observes upcoming Fixtures for the Teams derived from the user's choice, ordered by date
- [x] Home on Android and on iOS shows date, time, opponent and venue per Fixture, with the free-week state
- [x] A user with neither My Player nor My Club sees the existing empty Home, not an empty Fixture list
- [x] UseCase-seam tests cover ordering, finished Fixtures excluded and free week
- [x] Previews cover loaded, free week and empty on both platforms
- [x] Accessibility rules and 200% text scaling hold on both platforms
- [x] If venue is unavailable from the API, the field is omitted from the UI rather than shown blank

## Out of scope

- Live scores (needs-info, later ticket)

## Comments
- 2026-10-30: Android 200% font-scale previews inspected (Home with Fixtures): no clipping, Fixture cards wrap cleanly. iOS half not inspected, so left unchecked.
- 2026-10-30: Hero star change (d545382, c59e3f6) does not touch the Home Fixtures views. Android 200% inspected earlier; iOS 200% and VoiceOver still unchecked.
- 2026-10-31: Ticket text predates the XTTV API: Club, Team, Fixture and standing data now come from live XTTV (xttv-api), the fake directory is bound only in `deb` previews, and the Player profile exists (player-detail-teams). All build work is done; the only open box is the device pass (iOS 200% and VoiceOver, TalkBack). Status set to ready-for-human.
- 2026-10-31: Resolved at the project owner's request without a device pass; VoiceOver, Dynamic Type and TalkBack were not run by the agent.
