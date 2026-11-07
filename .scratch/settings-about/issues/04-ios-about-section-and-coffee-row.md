---
status: resolved
blocked_by: [2, 3]
---

# 04: iOS: About section with "Buy me a coffee"

**What to build:** the same About section and "Buy me a coffee" row on iOS, reusing the `AppConfig` field and the catalog row from 03. The row opens the tip page with the system URL opener and is hidden until the support URL is set. Leaving the iOS URL unset hides the row on iOS alone, which is the answer to an App Review rejection.

**Blocked by:** 02 (iOS Settings as a full screen), 03 (the `AppConfig` field and the catalog row)

- [x] The iOS build passes the support URL from untracked local config (`Secrets.xcconfig`) into `AppConfig`, independently of Android; unset hides the row
- [x] The About section sits last on Settings and holds the XTTV credit; the coffee row reads to VoiceOver as a button (CODING_STANDARDS §12)
- [x] The tap is a Settings action; the ViewModel logs `support_link_opened` and the view opens the URL with `openURL`
- [x] Swift ViewModel test: the action logs the event and hands over the URL; with no URL the state has no coffee row
- [x] Previews cover Settings with and without the coffee row
- [x] New `.xcstrings` keys are added in English and German
- [x] New strings pushed to Loco
- [x] Tapped in the simulator with a URL set: the browser opens on it
- [x] The iOS build passes (docs/DEVELOPMENT.md)

## Comments

- 2026-11-07: iOS gets its tip page on its own: `SUPPORT_URL` in the untracked `Secrets.xcconfig` feeds the `BatSupportUrl` Info.plist key, which `iOSApp` passes to `KoinHelper.start` (new `supportUrl` parameter) and on to `AppConfig.supportUrl`. Unset, empty or anything that is not `https://` plus something leaves the shared `SupportLink` null, so the row is hidden on iOS alone; the Android setting is separate. An xcconfig reads `//` as a comment, so the example file says to write `https:/$()/example.org/...`; written plainly the value truncates to `https:` and the row silently hides.
- 2026-11-07: Settings' About section is now the header, the coffee row (when a URL is set) and the XTTV credit. The row is a plain `Button` with the visible title as its label, a hint that it opens the tip page in the browser, and the link trait, since it leaves the app. `supportClicked` logs `support_link_opened` and the view opens the URL with `openURL`.
- 2026-11-07: The ViewModel hands the URL to the view through an event stream created per subscriber, not one shared stream. SwiftUI cancels a view's `.task` when another screen covers it, which ends an `AsyncStream` for good, so the coffee tap would have logged but never opened after Settings was covered (ticket 06 will push the licenses screen over it). A test now covers a cancelled consumer followed by a new one. Other ViewModels in the app use the single shared stream and may share the problem; they were not changed.
- 2026-11-07: Review found `SupportLink` accepted a bare `https://`; it now needs something after the scheme (tests on both platforms). It also found a doc comment I had stranded in `KoinHelper`, since moved back.
- 2026-11-07: Loco: the three strings were pushed, but iOS and Android share a Loco asset when the key matches, so my first iOS hint wording overwrote Android's. I aligned the iOS text with Android's and pushed again; Loco then reported nothing to update for iOS.
- 2026-11-07: Build, the 194 Swift tests, the Kotlin tests on both platforms and `./gradlew check` pass. Tapped by hand in the simulator with a URL set and checked on iOS and confirmed; VoiceOver itself was not run by me.
