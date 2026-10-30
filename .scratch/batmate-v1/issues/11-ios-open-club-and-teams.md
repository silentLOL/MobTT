---
status: ready-for-agent
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
