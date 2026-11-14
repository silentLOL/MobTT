---
status: resolved
blocked_by: []
---

# 02: Check the Player screen on devices

**What to check:** the Player screen was verified on the Android emulator with live XTTV data only. The driver cannot tap the iOS simulator, so the rest needs a person.

- [x] iOS: open a Player from Home with a real Club and see the Teams section; tap the Club and a Team and use back
- [x] iOS: a Player with no Team in any lineup shows the Club row and no Teams heading
- [x] Both platforms at 200% font scale: long Club and Team names wrap and nothing is clipped
- [x] TalkBack and VoiceOver: the name reads as a heading, each row as one button, the star announces its state

## Comments

- 2026-10-31: the Android check with live data is recorded in the spec's Further Notes.
- 2026-10-31: Checked and verified on devices by the project owner.
