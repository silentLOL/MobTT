---
status: ready-for-agent
blocked_by: []
---

# 01: Android: Settings as a full screen

**What to build:** Settings opens as a pushed full screen on the current tab's back stack, like a Team or Fixture screen, with the shell's back chevron. The bottom sheet, its X and the "Done" button are gone, and the gear is hidden while Settings is on top. Back steps out of the My Player and My Club pickers first, then out of Settings. See the spec's Implementation Decisions for the navigation contract.

**Blocked by:** None (can start immediately)

- [ ] Settings and its destination live in the settings `:api` module with a `GlobalNavigator` extension as the only entry point; the shell's "settings open" state, its open and close calls and the hosted `SettingsSheet` are removed
- [ ] Opening Settings from Home and from the top bar pushes it onto the current tab's stack; Back returns to that screen on that tab
- [ ] The shell's back chevron shows on Settings with its existing "Back" label; the gear is hidden while Settings is on top
- [ ] The header X, the "Done" action and the Dismiss event are removed, with their strings
- [ ] In a picker (My Player, My Club), system Back and the chevron step back inside the picker first; a second Back leaves Settings
- [ ] A new ADR from `docs/adr/template.md` supersedes the "Settings as a sheet" part of ADR-0005; ADR-0005's status links to it and `docs/adr/README.md` lists it
- [ ] CLAUDE.md prose that still calls Settings a sheet (§13 and the shell's description) links to the ADR instead
- [ ] `SettingsViewModelTest` is updated: the removed actions and event are gone, consent, appearance and picker cases still pass
- [ ] Previews are updated for the full-screen layout (empty, loaded, long name, picker, association step)
- [ ] Opened from each tab in a `deb` build: Back returns to that tab; a picker's Back stays in Settings
- [ ] `./gradlew check` passes
