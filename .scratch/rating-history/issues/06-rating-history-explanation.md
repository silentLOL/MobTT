---
status: resolved
blocked_by: [3]
---

# 06: Info button that explains the Rating history

**What to build:** an info button next to the Rating history title opens a sheet that explains in plain words what the Rating, the Deviation and the band mean, with a link to Ratings Central's own explanation. See the spec's "Info sheet".

**Blocked by:** 03

- [x] `BatInfoSheet` in both design systems: title, body and an optional link; `ModalBottomSheet` on Android, `.sheet` with a medium detent on iOS; closes with a swipe and a labelled close button; scrolls and grows with the text size
- [x] A `BatIcon.Info` button labelled "About the Rating history" next to the section title opens it
- [x] The body says: the line is Ratings Central's estimate of playing strength; ± is the Deviation, how certain it is; about two times in three the true strength lies within the band; a wider band means a bigger move at the next Rating update; the Rating changes once per Rating update
- [x] "Learn more at Ratings Central" opens its "How it works" page in the in-app browser (Custom Tab, Safari view)
- [x] Texts in English, German and Austrian German on both platforms
- [x] `rating_info_opened` is added as a catalog row first, then tracked on both platforms when the sheet opens
- [x] Android and iOS ViewModel tests for the analytics event
- [x] Previews in light, dark and large text
- [x] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: The "How it works" URL comes from `AppConfig` through a new `RatingHistoryExplanation`, exposed as `GetRatingHistoryUseCase.explanationUrl`, so no URL sits in feature code. Android opens it in a Custom Tab (new dependency `androidx.browser`); iOS presents `SafariView` from the info sheet itself, since SwiftUI cannot stack a second sheet on the screen. `BatCollapsibleSectionHeader` and `LazySectionView` gained an optional header action on both platforms.
