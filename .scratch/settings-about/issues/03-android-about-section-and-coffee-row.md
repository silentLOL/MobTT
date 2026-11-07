---
status: ready-for-agent
blocked_by: [1]
---

# 03: Android: About section with "Buy me a coffee"

**What to build:** a final About section on the Settings screen holding the existing XTTV credit and a "Buy me a coffee" row. The row opens the developer's tip page in the browser and is hidden until `AppConfig` has a support URL. Tapping it logs `support_link_opened`. See the spec's Implementation Decisions for the URL, the App Store risk and the analytics.

**Blocked by:** 01 (Settings as a full screen)

- [ ] `AppConfig` gains an optional support URL, null by default, documented like the Fixture link host; the row is not shown while it is null
- [ ] The About section sits last on Settings and holds the XTTV credit; the coffee row reads to TalkBack as a button, e.g. "Buy me a coffee" (CODING_STANDARDS §12)
- [ ] The tap is a Settings action; the ViewModel logs `support_link_opened` and emits a one-off event; the Screen opens the URL through a private `Context` extension
- [ ] `support_link_opened` has its catalog row in `docs/ANALYTICS.md` (no parameters; add it before any code)
- [ ] The Android build passes the support URL from untracked local config into `AppConfig`, and `docs/BUILD_VARIANTS.md` or `docs/DEVELOPMENT.md` says where to set it
- [ ] ViewModel test: the action logs the event and emits the URL; with a null URL the state has no coffee row
- [ ] Previews cover Settings with and without the coffee row
- [ ] New strings are added in English and German
- [ ] New strings pushed to Loco
- [ ] Tapped in a `deb` build with a URL set: the browser opens on it
- [ ] `./gradlew check` passes
