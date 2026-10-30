# 03: iOS: Home lists Team Favorites

**What to build:** on iOS, a Team the user has favourited shows in Home's Favorites list, and tapping it opens the Team detail. The shared domain also dropped Team Favorites, so resolving them there was part of this ticket; Android Home lists them as a result, without a tap-through. Today Home leaves Team Favorites out, so the star on the Team screen (ticket 02) adds a Favorite the user cannot see or remove from Home.

**Blocked by:** None (can start immediately). Ticket 02 is committed.

**Status:** done

- [x] The shared domain resolves a Team Favorite to its name and Club, and a Team that cannot be resolved is left out
- [x] A Team Favorite appears in Home's Favorites list with its name and Club as detail
- [x] Removing it from the Home row removes the Favorite, and the star on the Team screen follows
- [x] Tapping the row opens that Team's detail screen, through the existing Team route
- [x] The existing test asserting that Team Favorites are left out is replaced by tests that they are listed, opened and removed
- [x] The row keeps the accessibility labels and touch targets of the Club and Player rows
- [x] Previews cover a Team Favorite and a long-name Team Favorite

**Manual check outstanding:** 200% Dynamic Type and VoiceOver on a device.
