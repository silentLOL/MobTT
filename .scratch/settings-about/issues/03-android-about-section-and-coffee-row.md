---
status: resolved
blocked_by: [1]
---

# 03: Android: About section with "Buy me a coffee"

**What to build:** a final About section on the Settings screen holding the existing XTTV credit and a "Buy me a coffee" row. The row opens the developer's tip page in the browser and is hidden until `AppConfig` has a support URL. Tapping it logs `support_link_opened`. See the spec's Implementation Decisions for the URL, the App Store risk and the analytics.

**Blocked by:** 01 (Settings as a full screen)

- [x] `AppConfig` gains an optional support URL, null by default, documented like the Fixture link host; the row is not shown while it is null
- [x] The About section sits last on Settings and holds the XTTV credit; the coffee row reads to TalkBack as a button, e.g. "Buy me a coffee" (CODING_STANDARDS §12)
- [x] The tap is a Settings action; the ViewModel logs `support_link_opened` and emits a one-off event; the Screen opens the URL through a private `Context` extension
- [x] `support_link_opened` has its catalog row in `docs/ANALYTICS.md` (no parameters; add it before any code)
- [x] The Android build passes the support URL from untracked local config into `AppConfig`, and `docs/BUILD_VARIANTS.md` or `docs/DEVELOPMENT.md` says where to set it
- [x] ViewModel test: the action logs the event and emits the URL; with a null URL the state has no coffee row
- [x] Previews cover Settings with and without the coffee row
- [x] New strings are added in English and German
- [x] New strings pushed to Loco
- [x] Tapped in a `deb` build with a URL set: the browser opens on it
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: `AppConfig.supportUrl` (constructor parameter, null by default) feeds a `SupportLink` value in `:domain:user`, bound in `:core:di` like `FixtureLinks`. `SupportLink` only keeps an `https://` address, so a typo or a stray `file:` or `intent:` value in build config hides the row instead of reaching a browser. Android reads `support.url` from `local.properties` or `SUPPORT_URL` from the environment into `BuildConfig.SUPPORT_URL` (quotes and backslashes escaped); `docs/BUILD_VARIANTS.md` says so. Only the `SUPPORT_URL` route was run; `local.properties` uses the same code as the XTTV key.
- 2026-11-07: Settings' ViewModel gets `SupportClicked`, which logs `support_link_opened` (catalog row added first) and emits `OpenSupportLink`; the Route opens it through a private `Context.openInBrowser` that logs the throwable and does nothing when no browser exists. `showSupportLink` in the state hides the row without a URL.
- 2026-11-07: Checked on the emulator: with a test URL the About section shows the coffee row above the XTTV credit, it is exposed as a button, and tapping it opened Chrome on the URL; with no URL the row is gone; at 200% font the section wraps cleanly. TalkBack itself was not run. The real tip page is not set anywhere: put the PayPal link in `support.url` (and in CI's `SUPPORT_URL` for release builds) before shipping.
- 2026-11-07: Strings pushed to Loco: 3 new Android assets and 3 de-AT texts. The same push also created 2 new iOS assets that were already in the repo's catalog and unpushed; they are not from this ticket.
- 2026-11-07: The About order is coffee, then XTTV, until ticket 05 adds the licenses row first.
