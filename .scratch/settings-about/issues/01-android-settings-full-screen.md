---
status: resolved
blocked_by: []
---

# 01: Android: Settings as a full screen

**What to build:** Settings opens as a pushed full screen on the current tab's back stack, like a Team or Fixture screen, with the shell's back chevron. The bottom sheet, its X and the "Done" button are gone, and the gear is hidden while Settings is on top. Back steps out of the My Player and My Club pickers first, then out of Settings. See the spec's Implementation Decisions for the navigation contract.

**Blocked by:** None (can start immediately)

- [x] Settings and its destination live in the settings `:api` module with a `GlobalNavigator` extension as the only entry point; the shell's "settings open" state, its open and close calls and the hosted `SettingsSheet` are removed
- [x] Opening Settings from Home and from the top bar pushes it onto the current tab's stack; Back returns to that screen on that tab
- [x] The shell's back chevron shows on Settings with its existing "Back" label; the gear is hidden while Settings is on top
- [x] The header X, the "Done" action and the Dismiss event are removed, with their strings
- [x] In a picker (My Player, My Club), system Back and the chevron step back inside the picker first; a second Back leaves Settings
- [x] A new ADR from `docs/adr/template.md` supersedes the "Settings as a sheet" part of ADR-0005; ADR-0005's status links to it and `docs/adr/README.md` lists it
- [x] CLAUDE.md prose that still calls Settings a sheet (§13 and the shell's description) links to the ADR instead
- [x] `SettingsViewModelTest` is updated: the removed actions and event are gone, consent, appearance and picker cases still pass
- [x] Previews are updated for the full-screen layout (empty, loaded, long name, picker, association step)
- [x] Opened from each tab in a `deb` build: Back returns to that tab; a picker's Back stays in Settings
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: Settings is `SettingsDestination` in the settings `:api` module, entered through `navigateToSettings()` and registered by `settingsGraph` in `:impl`; `SettingsRoute` replaces `SettingsSheet`. The navigator's `openSettings`, `closeSettings` and `isSettingsOpen`, the Dismiss event, the Done action and the X are gone. ADR-0012 supersedes the sheet part of ADR-0005 and is in the index.
- 2026-11-07: The shell's chevron now calls the back dispatcher (falling back to `navigator.goBack()` when there is none), so the picker's `BackHandler` sees it first. This changes the chevron for every screen, not just Settings; on the emulator the chevron still pops a Team screen opened from the Club tab. Fixture, League and Player screens were not tapped through.
- 2026-11-07: CLAUDE.md has no prose calling Settings a sheet, so there was nothing to relink; ADR-0005's status line points at ADR-0012.
- 2026-11-07: The existing `SettingsScreen` previews cover the new layout (title, no X or Done); the tests lost the Done case and the picker-back case already existed. Added no new ViewModel behaviour, so no new test.
- 2026-11-07: Checked in a `deb` build on the emulator: Settings shows a back chevron, no gear, no X or Done; system Back and the chevron both step out of the My Club picker, a second Back returns to Home; opened from the League tab and from the Club tab, Back returns to that tab (Home, League and Club were tried; the Players tab was not). `./gradlew check` passes.
- 2026-11-07: `settings_done_button` is removed from the Android strings, but Loco still holds it and its asset is shared with the iOS key of the same name, which iOS uses until ticket 02. Do not delete it in Loco before 02 lands, and do not run `./scripts/loco.sh pull` before then or it writes the Android string back. After 02, delete the asset in Loco.
- 2026-11-07: Rotating the phone with Settings open returned to Home: the navigator's back stack was held with `remember`, so the recreated activity started over (every pushed screen had this; Settings was a sheet before, so it never showed). `MobTTApp` now keeps the stack with `rememberNavBackStack` and hands it to `BackStackGlobalNavigator`. Checked on the emulator in real landscape: Settings stays, the chevron still leaves to Home. A Team or Fixture screen was not rotated.
