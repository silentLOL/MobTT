---
status: ready-for-human
blocked_by: [5, 9, 10]
---

# 11: iOS: open a Club and see its Teams

**What to build:** on iOS the user opens a Club from My Club, My Player's Club or a Favorite Club, and sees its Teams with League, standing and next Fixture, or a free week. It reuses the shared domain from 10 and runs against the same fake directory, so the slice is demoable now.

- [x] The Club screen lists the Club's Teams, each with League, standing and next Fixture or free week
- [x] Swift ViewModels consume the shared UseCase through `KoinHelper` as async sequences, and the sealed free-week outcome arrives as a Swift enum through SKIE, exercising the conversion the review ledger lists as untested
- [x] The screen is reachable from Home and from a Favorite Club
- [x] Every view has a `#Preview` with representative sample data covering loading, loaded, empty and error
- [x] The fake directory stays bound only in a non-production configuration
- [ ] Accessibility labels, traits, 44pt targets and Dynamic Type follow the rules in CLAUDE.md
- [x] The iOS build passes (`docs/DEVELOPMENT.md`)

## Comments
- 2026-10-30: Not inspected: iOS previews need Xcode and VoiceOver/Dynamic Type need a device or simulator. Left unchecked.
- 2026-10-30: Hero star layout fixed on iOS in c59e3f6 (Club): star overlaid top-end, name at full width. Build and tests pass, but not yet seen in Xcode previews. Still unchecked: VoiceOver and Dynamic Type on a device or simulator.
- 2026-10-31: Ticket text predates the XTTV API: Club, Team, Fixture and standing data now come from live XTTV (xttv-api), the fake directory is bound only in `deb` previews, and the Player profile exists (player-detail-teams). All build work is done; the only open box is the device pass (VoiceOver and Dynamic Type on iOS). Status set to ready-for-human.
