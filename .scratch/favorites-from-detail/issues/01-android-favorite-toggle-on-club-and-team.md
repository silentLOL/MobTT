---
status: resolved
blocked_by: []
---

# 01: Android: favorite toggle on Club and Team screens

**What to build:** on Android, the Club and Team screens show a star. Tapping it adds or removes the shown Club or Team as a Favorite, and Home reflects the change. It uses the existing Favorites UseCases; persistence, deduplication and independence from My Player and My Club already exist and do not change.

- [x] The Club and Team screens show a star that reflects whether the item is a Favorite
- [x] Tapping the star adds or removes the Favorite, and Home shows the change
- [x] The ViewModels expose the Favorite state in their single UI state and handle the toggle through `onAction`; screens stay stateless
- [x] ViewModel tests cover toggling on and off against fake repositories and assert only observable outcomes
- [x] German and English strings, with accessibility labels that announce the state and the action
- [x] Previews cover favourited, not favourited and a long-name state

**Manual check outstanding:** 200% font scale and TalkBack on a device.

**Follow-up:** Team Favorites were dropped by the shared domain, so a favourited Team did not show on Android Home when this ticket closed. Ticket 03 fixed that in the domain. Android Home shows the row but has no tap-through to the Team screen.
