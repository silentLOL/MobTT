# 08: Android: choose My Player and Favorites on Home

**What to build:** on Android the user opens Settings, picks My Player or My Club, and sees Home reflect it, with their Favorites as quick access. Until the XTTV API exists this runs against a fake directory of Players and Clubs, so the slice is demoable now.

**Blocked by:** 03, 06, 07

**Status:** ready-for-agent

- [ ] The Settings sheet offers My Player and My Club, and the Club and Association are shown derived, not editable
- [ ] Home shows the chosen Player or Club, and Favorites as quick access with add and remove
- [ ] The ViewModel exposes one `StateFlow` of UI state and one `onAction` entry point, with one-off events through a channel
- [ ] Screens take navigation as function parameters, and feature modules depend only on other features' `:api` modules
- [ ] Koin modules are registered through `:core:di`
- [ ] Previews cover empty, loaded and a long-name state, and the screens meet the accessibility rules in CLAUDE.md
- [ ] The fake directory is bound only in a non-production configuration and is marked for replacement by the real repository
