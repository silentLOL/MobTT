---
status: resolved
blocked_by: [2]
---

# 03: Rating history section from fake data in `deb`

**What to build:** in a `deb` build, the Player screen has a collapsed Rating history section below the Teams. Opening it loads the Player's Rating history and draws it: a line through the Rating after each Rating update, a shaded ±1 Deviation band, and a dot per Rating update. The data comes from a fake repository; real Ratings Central data follows in 04. See the spec's "Domain", "Release toggle" and "Chart".

**Blocked by:** 02

- [x] `:domain:rating` holds `RatingUpdate`, `RatingHistory` and `RatingHistoryRepository`, in the glossary's words
- [x] `GetRatingHistoryUseCase` takes the Player's ID and the Leagues of their Teams and tells apart: no Ratings Central entry, empty history, history, failure; reachable from iOS through `KoinHelper` as a `DataResult`
- [x] `AppConfig` gains the Rating history flag on both platforms: on for `deb` and `dev`, off for `beta` and `prod`; with it off the section is never shown and nothing is requested
- [x] `deb` binds a fake repository from the fake directory with a long history, a single Rating update, an empty history and a Player without an entry
- [x] The section loads on first expand, keeps its data when collapsed, retries only once opened, and waits for the Teams section before asking
- [x] States: hidden without an entry, "No Rating history yet", a single point with its value, Unavailable with a retry
- [x] `BatRatingChart` in both design systems takes plain points (date, value, low, high) and draws band, line and dots; Swift Charts on iOS, a Compose `Canvas` on Android, no new chart library; dates on the x-axis, the y-axis fitted to the band with padding; design-system colours in light and dark
- [x] The chart plots the Rating after each Rating update, so the unrated starting value never appears
- [x] Every new text is a key in `:android:core:localization` and the iOS string catalog, in English, German and Austrian German
- [x] `rating_history_opened` is added as a catalog row first (docs/ANALYTICS.md), then tracked on both platforms when the section is expanded, without Player identifiers
- [x] Use case tests with fake repositories for every outcome; Android and iOS ViewModel tests: loads on first expand only, waits for the Teams, hidden without an entry, never requested with the flag off, the analytics event
- [x] Chart previews: light, dark, one point, few points, many points
- [x] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: Two changes from the spec. The lookup is split into `GetRatingsCentralIdUseCase` (run once the Teams load, through XTTV only) and `GetRatingHistoryUseCase` (run when the section opens), so the section is hidden up front for a Player without a Ratings Central entry while Ratings Central is still asked only on demand. Reading `player_rc_id` onto `RankedPlayer` moved here from ticket 04, since the lookup needs it. The release flag is derived from the environment in `AppConfig` (on for `deb` and `dev`), so turning it on is one shared change instead of one per platform. Release builds bind a fallback repository that is always unavailable; the flag keeps them from asking.
