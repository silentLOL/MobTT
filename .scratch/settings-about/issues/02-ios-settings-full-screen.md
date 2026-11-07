---
status: ready-for-agent
blocked_by: []
---

# 02: iOS: Settings as a full screen

**What to build:** Settings is pushed onto the current tab's `NavigationStack` with the system back chevron, like Team detail. The sheet, its X and the "Done" button are gone, and the gear is hidden while Settings is on top. Back and the edge swipe step out of the My Player and My Club pickers first, then out of Settings. See the spec's Implementation Decisions.

**Blocked by:** None (can start immediately)

- [ ] Settings is one shared pushed destination registered once for every tab's stack, not four copies
- [ ] Every "open Settings" path (the top bar gear, the Club tab's event, the Home callbacks) pushes onto the current tab's stack; the Settings sheet and its detents are removed
- [ ] Back returns to the screen and tab Settings was opened from; the edge swipe works
- [ ] The gear is hidden while Settings is on top; the consent prompt stays suppressed while Settings is open
- [ ] The header X, the "Done" action and the dismiss event are removed, with their strings
- [ ] In a picker, the system back and the edge swipe step back inside the picker first; a second one leaves Settings
- [ ] `SettingsViewModelTests` is updated: the removed actions and event are gone, consent, appearance and picker cases still pass
- [ ] Previews are updated for the full-screen layout
- [ ] Opened from each tab in the simulator: Back returns to that tab; a picker's back stays in Settings
- [ ] The iOS build passes (docs/DEVELOPMENT.md)
