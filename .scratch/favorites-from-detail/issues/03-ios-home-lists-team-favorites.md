# 03: iOS: Home lists Team Favorites

**What to build:** on iOS, a Team the user has favourited shows in Home's Favorites list, the way it already does on Android, and tapping it opens the Team detail. Today Home leaves Team Favorites out, so the star on the Team screen (ticket 02) adds a Favorite the user cannot see or remove from Home.

**Blocked by:** None (can start immediately). Ticket 02 is committed.

**Status:** ready-for-agent

- [ ] A Team Favorite appears in Home's Favorites list with its name and Club as detail
- [ ] Removing it from the Home row removes the Favorite, and the star on the Team screen follows
- [ ] Tapping the row opens that Team's detail screen, through the existing Team route
- [ ] The existing test asserting that Team Favorites are left out is replaced by tests that they are listed, opened and removed
- [ ] The row keeps the accessibility labels and touch targets of the Club and Player rows
- [ ] Previews cover a Team Favorite and a long-name Team Favorite

**Manual check outstanding:** 200% Dynamic Type and VoiceOver on a device.
