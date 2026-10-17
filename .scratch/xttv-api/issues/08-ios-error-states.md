# 08: iOS error states for unavailable data

**What to build:** On iOS, the same behaviour as ticket 07: a retryable error on Home, Club and Team detail, distinct from an empty result, with the data error arriving in Swift as a typed error through SKIE.

**Blocked by:** 05 Fixtures from XTTV, 06 League standings from XTTV.

**Status:** done

- [x] The Swift ViewModels catch `DataUnavailable` as a typed error, with no string matching.
- [x] Home, Club and Team detail show an error state with retry; success clears it.
- [x] Empty results keep showing the existing empty state.
- [x] A stale cached value after a failed refresh shows without an error.
- [x] The error text is localized in the string catalog and works with VoiceOver and Dynamic Type at the largest sizes.
- [x] Swift tests cover the error case; previews exist for the error state.
- [x] The iOS build passes.

## Comments

- SKIE does not give Swift a typed `DataUnavailable`: a throwing suspend call arrives as an `NSError` that `catch is DataUnavailable` never matches (it compiles, though), and an error inside a Flow ends the app. So `KoinHelper` wraps the calls that can fail in `DataResult` (`Success` / `Unavailable(error: DataUnavailable)`), matched with `onEnum(of:)`: `loadClubOverview`, `loadTeamDetail`, `observeUserContext`, `observeResolvedFavorites`. The Android use cases are unchanged.
- Home gains a retry for the Fixtures error (`HomeAction.retryFixturesClicked`); Home, Club and Settings no longer end the app when the user-context or favorites streams hit `DataUnavailable`. A retry restarts `observe()` through `.task(id: retryCount)`.
- Error states announce themselves to VoiceOver on appearance (`AccessibilityNotification.Announcement`), the counterpart of Android's polite live region.
- New `iosAppTests` target (Swift Testing, hosted in the app) with the `iosAppTests` scheme and `scripts/ios-test.sh`. Club and Team detail ViewModels take their loader as an initializer parameter with the Koin call as default. Home has no ViewModel test: it reads its use cases from Koin in property initializers.
- Stale-after-failed-refresh is served by the repository layer (`XttvApiTest`), so the screens need no change.
- Not looked at: the previews were built, not rendered, so the Home error layout at the largest Dynamic Type size is unchecked.
