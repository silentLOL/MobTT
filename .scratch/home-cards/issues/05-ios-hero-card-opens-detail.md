---
status: ready-for-agent
blocked_by: [4]
---

# 05: iOS: the hero card opens its detail

**What to build:** on iOS, the hero card on Home is tappable. The My Player hero opens that Player's detail; the My Club hero opens the existing Club screen, which must show a top-left back chevron when opened from Home.

- [ ] Tapping the hero for My Player opens Player detail; tapping the hero for My Club opens that Club
- [ ] The Club screen shows a top-left back chevron when opened from Home, and it returns to Home
- [ ] The choose prompt shown when nothing is chosen is unchanged and the unavailable state stays non-interactive
- [ ] Swift tests at the Home ViewModel seam cover the Player hero tap and the Club hero tap
- [ ] The hero keeps its prominent look; VoiceOver label and button trait, 44pt touch target, String Catalog EN and DE; previews show the tappable hero

**Manual check outstanding:** VoiceOver and 200% Dynamic Type on a device.
