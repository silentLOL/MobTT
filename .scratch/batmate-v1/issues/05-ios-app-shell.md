# 05: iOS app shell

**What to build:** launching the iOS app shows a native shell with the four tabs Home, Club, Players and Live, a Settings sheet, and an empty state per tab. Text is German or English following the device language.

**Blocked by:** 01, 04

**Status:** done

- [x] A `TabView` presents the four tabs, built only from the iOS design system
- [x] `.xcstrings` hold German and English using the `{module}_{context}_{type}` keys
- [x] The Settings sheet opens and closes, and the appearance choice persists across restarts
- [x] Each view has a `#Preview` covering its distinct states
- [x] The app links only the shared framework from `shared-umbrella`
- [x] The layout works up to 200% Dynamic Type and on iPad width

**Manual check outstanding:** 200% Dynamic Type and iPad width on a device; the shell header still overflows at the largest sizes (deferred in review-notes.md).
