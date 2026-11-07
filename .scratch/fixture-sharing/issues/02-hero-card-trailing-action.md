---
status: ready-for-agent
blocked_by: []
---

# 02: Hero card trailing action

**What to build:** the design system's hero card can show one optional action at the end of its pill row, on Android (`BatHeroCard`, `BatHeroPillRow`) and iOS (`BatHeroCardView`). No screen uses it yet. This is a prefactor for the Share button (spec: Implementation Decisions, Share button).

**Blocked by:** None (can start immediately)

- [ ] Android: the hero card's pill row takes an optional trailing slot, aligned to the end, with the pill keeping its place on the start side
- [ ] iOS: `BatHeroCardView` takes the same optional trailing content
- [ ] Without the slot, every existing hero card looks exactly as before
- [ ] Previews show the card with an icon action in light, dark and large text on both platforms; Roborazzi screenshots record them on Android
- [ ] `./gradlew check` and the iOS build pass
