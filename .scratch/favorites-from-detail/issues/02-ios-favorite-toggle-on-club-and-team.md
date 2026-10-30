# 02: iOS: favorite toggle on Club and Team screens

**What to build:** on iOS, the Club and Team screens show a star. Tapping it adds or removes the shown Club or Team as a Favorite, and Home reflects the change. It uses the existing Favorites UseCases through the Koin helper; persistence and deduplication already exist and do not change.

**Blocked by:** None (can start immediately).

**Status:** done

- [x] The Club and Team screens show a star that reflects whether the item is a Favorite
- [x] Tapping the star adds or removes the Favorite, and Home shows the change for Clubs (Teams on Home: ticket 03)
- [x] The Swift ViewModels expose the Favorite state in their UI state and handle the toggle through their action entry point, with an injection seam for tests
- [x] ViewModel tests cover toggling on and off and assert only observable outcomes
- [x] German and English strings in the string catalog, with VoiceOver labels that announce the state and the action
- [x] Previews cover favourited, not favourited and a long-name state

**Manual check outstanding:** 200% Dynamic Type and VoiceOver on a device.

**Split out:** iOS Home leaves Team Favorites out of its list; ticket 03 covers listing them.
