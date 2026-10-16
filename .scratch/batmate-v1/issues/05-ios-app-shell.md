# 05: iOS app shell

**What to build:** launching the iOS app shows a native shell with the four tabs Home, Club, Players and Live, a Settings sheet, and an empty state per tab. Text is German or English following the device language.

**Blocked by:** 01, 04

**Status:** ready-for-agent

- [ ] A `TabView` presents the four tabs, built only from the iOS design system
- [ ] `.xcstrings` hold German and English using the `{module}_{context}_{type}` keys
- [ ] The Settings sheet opens and closes, and the appearance choice persists across restarts
- [ ] Each view has a `#Preview` covering its distinct states
- [ ] The app links only the shared framework from `shared-umbrella`
- [ ] The layout works up to 200% Dynamic Type and on iPad width
