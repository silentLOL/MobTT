---
status: ready-for-agent
blocked_by: [2]
---

# 03: Rating history section from fake data in `deb`

**What to build:** in a `deb` build, the Player screen has a collapsed Rating history section below the Teams. Opening it loads the Player's Rating history and draws it: a line through the Rating after each Rating update, a shaded ±1 Deviation band, and a dot per Rating update. The data comes from a fake repository; real Ratings Central data follows in 04. See the spec's "Domain", "Release toggle" and "Chart".

**Blocked by:** 02

- [ ] `:domain:rating` holds `RatingUpdate`, `RatingHistory` and `RatingHistoryRepository`, in the glossary's words
- [ ] `GetRatingHistoryUseCase` takes the Player's ID and the Leagues of their Teams and tells apart: no Ratings Central entry, empty history, history, failure; reachable from iOS through `KoinHelper` as a `DataResult`
- [ ] `AppConfig` gains the Rating history flag on both platforms: on for `deb` and `dev`, off for `beta` and `prod`; with it off the section is never shown and nothing is requested
- [ ] `deb` binds a fake repository from the fake directory with a long history, a single Rating update, an empty history and a Player without an entry
- [ ] The section loads on first expand, keeps its data when collapsed, retries only once opened, and waits for the Teams section before asking
- [ ] States: hidden without an entry, "No Rating history yet", a single point with its value, Unavailable with a retry
- [ ] `BatRatingChart` in both design systems takes plain points (date, value, low, high) and draws band, line and dots; Swift Charts on iOS, a Compose `Canvas` on Android, no new chart library; dates on the x-axis, the y-axis fitted to the band with padding; design-system colours in light and dark
- [ ] The chart plots the Rating after each Rating update, so the unrated starting value never appears
- [ ] Every new text is a key in `:android:core:localization` and the iOS string catalog, in English, German and Austrian German
- [ ] `rating_history_opened` is added as a catalog row first (docs/ANALYTICS.md), then tracked on both platforms when the section is expanded, without Player identifiers
- [ ] Use case tests with fake repositories for every outcome; Android and iOS ViewModel tests: loads on first expand only, waits for the Teams, hidden without an entry, never requested with the flag off, the analytics event
- [ ] Chart previews: light, dark, one point, few points, many points
- [ ] `./gradlew check` and the iOS build pass

## Comments
