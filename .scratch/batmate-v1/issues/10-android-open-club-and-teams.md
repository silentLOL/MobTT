# 10: Android: open a Club and see its Teams

**What to build:** on Android the user opens a Club from My Club, My Player's Club or a Favorite Club, and sees its Teams. Each Team shows its League, its standing in that League and its next Fixture, or a "free week" when it has none. Until the XTTV API exists this runs against the fake directory, so the slice is demoable now. It carries the shared League, Fixture and standing domain that the iOS ticket reuses.

**Blocked by:** 03, 06, 08

**Status:** ready-for-agent

- [x] Domain models exist for League, Fixture and a Team's standing, using the glossary terms; a Fixture's score counts Matches won, and existing `Team` users keep working
- [x] Repository interfaces for Teams, Leagues and Fixtures live in separate files from any implementation
- [x] A UseCase returns a Club's Teams with League, standing and next Fixture (`suspend`)
- [x] A Team with no upcoming Fixture yields an explicit free-week outcome, never `null` or an error
- [x] Fixture fields the XTTV API may not provide are recorded as assumptions, not invented (spec "Blocked on API")
- [x] UseCase-seam tests against fake repositories assert only on observable outcomes: the next Fixture is the earliest upcoming one, finished Fixtures are skipped, free week when none remain
- [x] The Club feature is split into `:api` and `:impl`, with a Koin module registered through `:core:di`
- [x] The Club screen follows the UDF pattern (`onAction`, single `StateFlow`) and is reachable from Home and from a Favorite Club
- [x] Every screen state (loading, loaded, empty, error) has a `@BatScreenPreview` with representative sample data
- [x] Fake repositories are bound only in a non-production configuration and marked for replacement by the real repository (ADR-0005)
- [ ] Accessibility labels, 48dp targets and 200% font scaling follow CODING_STANDARDS §12

## Out of scope

- Real XTTV repositories and bindings (API ticket)
- Team detail, lineup and full standing (13)
