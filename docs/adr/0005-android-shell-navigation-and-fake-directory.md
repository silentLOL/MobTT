# ADR-0005: Android shell navigation contracts and a debug-only fake directory

**Status:** Accepted
**Date:** 2026-10-16
**Deciders:** Tech Lead

## Context
Tickets 03 and 08 build the Android shell and the first real slice on it: choosing
My Player or My Club and managing Favorites. Two things are not settled by
[ADR-0002](0002-shared-domain-library-with-native-presentation.md) or CLAUDE.md §13:

1. Feature `:impl` modules may only depend on other features' `:api` modules, yet
   every feature needs `GlobalNavigator`, and Settings (a bottom sheet) is opened from
   Home and from the shell's top bar.
2. The XTTV API is undocumented, so Player, Club and Association repositories have no
   real implementation, yet the slice must be demoable.

## Decision
We will put `GlobalAppDestination` and `GlobalNavigator` (plus the back-stack
implementation, including "settings open" state) in `:android:feature:navigation`,
which every `:api` module depends on. Settings is not a destination: the shell owns
its visibility through the navigator and hosts `SettingsSheet` from `:feature:settings:impl`.
The shell is the one place allowed to depend on every `:impl`.

We will add search to the domain (`PlayerRepository.searchPlayers`,
`ClubRepository.searchClubs`, and use cases) and provide the fake directory as
`:core:fake-directory`, a KMP module so iOS can reuse it. Android binds it only from
`androidApp/src/debug`; `src/release` binds nothing and carries a `TODO` until the
XTTV-backed repositories exist.

## Options Considered

### Option A: Navigator contracts in one module, fake directory as a KMP core module — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low |
| Cost             | One small module each |
| Scalability      | New features only add an `:api` and `:impl` pair |
| Team familiarity | High |

**Pros:** keeps `:impl` isolation intact; iOS ticket reuses the same fake data.
**Cons:** the navigator knows about the Settings sheet, which is not a navigation destination.

### Option B: Settings as a navigation destination
**Pros:** no special case in the navigator. **Cons:** a sheet over the current tab would
need nav3 scene strategies for little gain, and Settings would leave the tab's back stack.

### Option C: Fake directory in `androidApp` only
**Pros:** no extra module. **Cons:** iOS would duplicate the data and drift from it.

## Trade-off Analysis
The sheet special case is small and contained in one interface. The fake directory
costs a module but is the only way to keep both platforms demoable against identical data.

## Consequences
- A release build cannot resolve the directory repositories, so Home and Settings fail
  until the real repositories land. That ticket must add the binding in `src/release`
  and then the fake can be restricted to tests and previews.
- Team Favorites are not shown: there is no Team repository to resolve their names.
- Appearance is stored by `:feature:settings:impl` in the shared DataStore but is not
  shared domain: iOS keeps its own.
