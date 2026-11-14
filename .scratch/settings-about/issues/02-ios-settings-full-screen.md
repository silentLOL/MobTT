---
status: resolved
blocked_by: []
---

# 02: iOS: Settings as a full screen

**What to build:** Settings is pushed onto the current tab's `NavigationStack` with the system back chevron, like Team detail. The sheet, its X and the "Done" button are gone, and the gear is hidden while Settings is on top. Back and the edge swipe step out of the My Player and My Club pickers first, then out of Settings. See the spec's Implementation Decisions.

**Blocked by:** None (can start immediately)

- [x] Settings is one shared pushed destination registered once for every tab's stack, not four copies
- [x] Every "open Settings" path (the top bar gear, the Club tab's event, the Home callbacks) pushes onto the current tab's stack; the Settings sheet and its detents are removed
- [x] Back returns to the screen and tab Settings was opened from; the edge swipe works
- [x] The gear is hidden while Settings is on top; the consent prompt stays suppressed while Settings is open
- [x] The header X, the "Done" action and the dismiss event are removed, with their strings
- [x] In a picker, the system back and the edge swipe step back inside the picker first; a second one leaves Settings
- [x] `SettingsViewModelTests` is updated: the removed actions and event are gone, consent, appearance and picker cases still pass
- [x] Previews are updated for the full-screen layout
- [x] Opened from each tab in the simulator: Back returns to that tab; a picker's back stays in Settings
- [x] The iOS build passes (docs/DEVELOPMENT.md)

## Comments

- 2026-11-07: Settings is a private `SettingsDestination` pushed onto the selected tab's path by `openSettings`; the top-bar gear and the four `ClubRoute` `onOpenSettings` closures all call it. Each tab's stack still needs its own `navigationDestination` line (four lines, one destination type, one `settingsDetail()` builder), as for every other destination in `ShellScreen`. The sheet, its detents, the X, "Done", `doneClicked`, the dismiss event and `settings_done_button` are gone. `core_close_button_cd` stays: other screens use it.
- 2026-11-07: The shell hides its gear and holds back the consent prompt while a `SettingsDestinationView` is on screen; each one registers its own id on appear and removes it on disappear, and a tab tap clears the set. In a picker the system back button is hidden (which also disables the system swipe), so the screen's own arrow, a left-edge swipe strip and the VoiceOver escape action step back instead.
- 2026-11-07: Build and the 190 Swift tests pass. Not done: the simulator could not be driven (access to it was never granted), so nothing here was run in the app, and the previews were not viewed. Ask a human: please check in the simulator that (1) Settings opens from each tab and Back returns to that tab, (2) the gear is hidden on Settings and the consent prompt waits until Settings closes, (3) inside My Player and My Club the back arrow, the left-edge swipe and Back each step out of the picker first (a second one leaves Settings), and (4) the `SettingsScreen` previews show no X or Done. Risk spots: the edge strip may lose to the scroll view, and the system back button may flicker when a picker closes. Then tick the open boxes and set `status: resolved`.
- 2026-11-07: Once this is committed the Loco asset `settings_done_button` is unused on both platforms. Delete it in Loco, and only then run `./scripts/loco.sh pull` (see ticket 01).
- 2026-11-07: Checked by hand in the simulator on iOS (and on Android) and confirmed.
