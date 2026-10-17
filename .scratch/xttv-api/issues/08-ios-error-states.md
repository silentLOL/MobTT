# 08: iOS error states for unavailable data

**What to build:** On iOS, the same behaviour as ticket 07: a retryable error on Home, Club and Team detail, distinct from an empty result, with the data error arriving in Swift as a typed error through SKIE.

**Blocked by:** 05 Fixtures from XTTV, 06 League standings from XTTV.

**Status:** ready-for-agent

- [ ] The Swift ViewModels catch `DataUnavailable` as a typed error, with no string matching.
- [ ] Home, Club and Team detail show an error state with retry; success clears it.
- [ ] Empty results keep showing the existing empty state.
- [ ] A stale cached value after a failed refresh shows without an error.
- [ ] The error text is localized in the string catalog and works with VoiceOver and Dynamic Type at the largest sizes.
- [ ] Swift tests cover the error case; previews exist for the error state.
- [ ] The iOS build passes.
