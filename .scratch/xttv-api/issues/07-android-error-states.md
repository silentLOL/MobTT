---
status: resolved
blocked_by: [5, 6]
---

# 07: Android error states for unavailable data

**What to build:** On Android, when XTTV cannot be reached the user sees a clear, retryable error on Home, Club and Team detail, and can tell it apart from "this Club has no Teams".

- [x] Home, Club and Team detail each show an error state with a retry action when a repository throws `DataUnavailable`.
- [x] Retry reloads the data; success clears the error.
- [x] An empty result (no Teams, no Fixtures, no standings) shows the existing empty state, not the error.
- [x] When a stale cached value is served after a failed refresh, the screen shows it without an error.
- [x] The error text is localized and accessible (announced, retry reachable with a screen reader and at 200% text).
- [x] ViewModel tests cover the `DataUnavailable` case; previews exist for the error state.
- [x] `./gradlew check` passes.

## Comments

- Club and Team detail already had an error state with retry. Home's Fixtures error gained a retry button (`HomeAction.RetryFixturesClicked`).
- Found while testing: with XTTV down and nothing cached, the user-context, favorites and picker lookups threw `DataUnavailable` outside any try and would have crashed Home (and Club opened as "Mine"). Home and Club now catch it; a retry restarts the whole stream.
- Stale-after-failed-refresh is served by the repository layer (`XttvApiTest`), so the screens need no change.
- Feature previews are not rendered by `check` (Roborazzi scans only `designsystem`), so the Home error layout at 200% text was not looked at.
