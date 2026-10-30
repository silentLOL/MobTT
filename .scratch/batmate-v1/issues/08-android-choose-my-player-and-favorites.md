---
status: resolved
blocked_by: [3, 6, 7]
---

# 08: Android: choose My Player and Favorites on Home

**What to build:** on Android the user opens Settings, picks My Player or My Club, and sees Home reflect it, with their Favorites as quick access. Until the XTTV API exists this runs against a fake directory of Players and Clubs, so the slice is demoable now.

- [x] The Settings sheet offers My Player and My Club, and the Club and Association are shown derived, not editable
- [x] Home shows the chosen Player or Club, and Favorites as quick access with add and remove
- [x] The ViewModel exposes one `StateFlow` of UI state and one `onAction` entry point, with one-off events through a channel
- [x] Screens take navigation as function parameters, and feature modules depend only on other features' `:api` modules
- [x] Koin modules are registered through `:core:di`
- [x] Previews cover empty, loaded and a long-name state, and the screens meet the accessibility rules in CLAUDE.md
- [x] The fake directory is bound only in a non-production configuration and is marked for replacement by the real repository

**Manual check outstanding:** 200% font scale and TalkBack on a device.
