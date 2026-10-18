# 09: iOS: choose My Player and Favorites on Home

**What to build:** on iOS the user opens Settings, picks My Player or My Club, and sees Home reflect it, with their Favorites as quick access. Until the XTTV API exists this runs against the same fake directory, so the slice is demoable now.

**Blocked by:** 05, 06, 07

**Status:** done

- [x] The Settings sheet offers My Player and My Club, and the Club and Association are shown derived, not editable
- [x] Home shows the chosen Player or Club, and Favorites as quick access with add and remove
- [x] Swift ViewModels consume the shared UseCases through `KoinHelper` as async sequences
- [x] Every view has a `#Preview` with representative sample data, not only the empty state
- [x] The fake directory is bound only in a non-production configuration and is marked for replacement by the real repository
- [x] Accessibility labels, traits and Dynamic Type follow the rules in CLAUDE.md

**Manual check outstanding:** 200% Dynamic Type and VoiceOver on a device.
