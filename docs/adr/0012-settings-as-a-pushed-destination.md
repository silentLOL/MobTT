# ADR-0012: Settings as a pushed destination on Android

**Status:** Accepted
**Date:** 2026-11-07
**Deciders:** Tech Lead

## Context
[ADR-0005](0005-android-shell-navigation-and-fake-directory.md) made Settings a bottom
sheet that the shell owns: `GlobalNavigator` carried `openSettings`/`closeSettings` and the
back-stack navigator kept an `isSettingsOpen` flag. It rejected Settings as a navigation
destination because "a sheet over the current tab would need nav3 scene strategies for little
gain, and Settings would leave the tab's back stack".

Settings now grows past what a sheet suits: an About section, and a second screen for open
source licenses that must itself be navigated to and back from. The iOS app is moving
Settings to a pushed screen at the same time, so the two platforms should match.

## Decision
We will make Settings a `@Serializable` `SettingsDestination` in `:android:feature:settings:api`,
entered through `GlobalNavigator.navigateToSettings()` and registered by `settingsGraph` in
`:android:feature:settings:impl`. It is pushed onto the current tab's back stack, so Back
returns to the screen it was opened from. The navigator's `openSettings`, `closeSettings` and
`isSettingsOpen` and the shell-hosted `SettingsSheet` are removed.

## Options Considered

### Option A: Pushed destination on the current stack — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low: same pattern as every other pushed screen |
| Cost             | Removes the navigator's special case |
| Scalability      | A licenses screen is one more destination |
| Team familiarity | High |

**Pros:** no special case in `GlobalNavigator`; the shell's back chevron and the system Back
work unchanged; Settings keeps the tab it was opened from selected.
**Cons:** Settings is part of the tab's back stack, so opening it from two tabs leaves two
independent entries; the shell must hide the gear while Settings is on top.

### Option B: Keep the sheet and add the About screens inside it
**Pros:** no navigation change. **Cons:** a nested stack inside a sheet duplicates navigation
that Navigation3 already provides, and the screen can no longer share the shell's back chevron.

### Option C: Settings as a top-level tab
**Pros:** always one tap away. **Cons:** a rarely used screen would take a tab slot, and
`navigateTopLevel` would replace the stack, losing the screen the user came from.

## Trade-off Analysis
ADR-0005's objection was that Settings would leave the tab's back stack. Pushing onto the
current stack answers it: Back is a normal pop. The remaining cost, two independent entries
when Settings is opened from different tabs, is the same as for a Team or Fixture screen.

The My Player and My Club pickers stay inside the Settings screen. Back, from the system or
from the shell's chevron, steps out of a picker before it leaves Settings. The chevron
therefore goes through the back dispatcher instead of calling the navigator directly, so a
screen can handle it first.

## Consequences
- `GlobalNavigator` no longer knows about Settings.
- The shell's chevron uses the back dispatcher; any future screen with internal back steps
  can register a `BackHandler` and the chevron respects it.
- The Settings sheet's X and "Done" button are gone; changes are saved as they are made.
- ADR-0005 stays accepted for the rest of its decision (navigator contracts in
  `:android:feature:navigation`, the fake directory); only its Settings-as-a-sheet part is
  superseded.
- Revisit if a second feature needs the shell's chevron to defer to a screen: then the back
  dispatcher route may want a small navigator-level contract instead of relying on `BackHandler`.
