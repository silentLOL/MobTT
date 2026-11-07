---
status: resolved
blocked_by: []
---

# 02: Hero card trailing action

**What to build:** the design system's hero card can show one optional action at the end of its pill row, on Android (`BatHeroCard`, `BatHeroPillRow`) and iOS (`BatHeroCardView`). No screen uses it yet. This is a prefactor for the Share button (spec: Implementation Decisions, Share button).

**Blocked by:** None (can start immediately)

- [x] Android: the hero card's pill row takes an optional trailing slot, aligned to the end, with the pill keeping its place on the start side
- [x] iOS: `BatHeroCardView` takes the same optional trailing content
- [x] Without the slot, every existing hero card looks exactly as before
- [x] Previews show the card with an icon action in light, dark and large text on both platforms; Roborazzi screenshots record them on Android
- [x] `./gradlew check` and the iOS build pass

## Comments

- 2026-11-07: The slot lives on the pill row (`BatHeroPillRow` / `BatHeroPillRowView`), not the card, so the action lines up with the pill. Screens whose hero content is merged for accessibility (Team, Club) keep overlaying their Favorite star. Added `BatIcon.Share` on both platforms. The action uses `BatIconButton`'s usual round surface, like the Favorite star, not a bare `onHero` icon.
