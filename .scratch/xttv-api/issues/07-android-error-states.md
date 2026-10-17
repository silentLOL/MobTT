# 07: Android error states for unavailable data

**What to build:** On Android, when XTTV cannot be reached the user sees a clear, retryable error on Home, Club and Team detail, and can tell it apart from "this Club has no Teams".

**Blocked by:** 05 Fixtures from XTTV, 06 League standings from XTTV.

**Status:** ready-for-agent

- [ ] Home, Club and Team detail each show an error state with a retry action when a repository throws `DataUnavailable`.
- [ ] Retry reloads the data; success clears the error.
- [ ] An empty result (no Teams, no Fixtures, no standings) shows the existing empty state, not the error.
- [ ] When a stale cached value is served after a failed refresh, the screen shows it without an error.
- [ ] The error text is localized and accessible (announced, retry reachable with a screen reader and at 200% text).
- [ ] ViewModel tests cover the `DataUnavailable` case; previews exist for the error state.
- [ ] `./gradlew check` passes.
