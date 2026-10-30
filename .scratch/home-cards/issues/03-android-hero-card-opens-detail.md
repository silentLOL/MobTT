---
status: ready-for-agent
blocked_by: [1]
---

# 03: Android: the hero card opens its detail

**What to build:** on Android, the hero card on Home is tappable. The My Player hero opens that Player's detail; the My Club hero opens the existing Club screen, which must show a top-left back chevron when opened from Home.

- [ ] Tapping the hero for My Player opens Player detail; tapping the hero for My Club opens that Club
- [ ] The Club screen shows a top-left back chevron when opened from Home, and it returns to Home
- [ ] The choose prompt shown when nothing is chosen is unchanged and the unavailable state stays non-interactive
- [ ] Taps route through `onAction` and a one-off navigation event
- [ ] Home ViewModel tests cover the Player hero tap and the Club hero tap
- [ ] The hero keeps its prominent look; accessibility label (button role, name and kind), 48dp touch target, EN and DE strings; previews show the tappable hero

**Manual check outstanding:** TalkBack and 200% font scale on a device.
