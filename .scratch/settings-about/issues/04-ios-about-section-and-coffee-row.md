---
status: ready-for-agent
blocked_by: [2, 3]
---

# 04: iOS: About section with "Buy me a coffee"

**What to build:** the same About section and "Buy me a coffee" row on iOS, reusing the `AppConfig` field and the catalog row from 03. The row opens the tip page with the system URL opener and is hidden until the support URL is set. Leaving the iOS URL unset hides the row on iOS alone, which is the answer to an App Review rejection.

**Blocked by:** 02 (iOS Settings as a full screen), 03 (the `AppConfig` field and the catalog row)

- [ ] The iOS build passes the support URL from untracked local config (`Secrets.xcconfig`) into `AppConfig`, independently of Android; unset hides the row
- [ ] The About section sits last on Settings and holds the XTTV credit; the coffee row reads to VoiceOver as a button (CODING_STANDARDS §12)
- [ ] The tap is a Settings action; the ViewModel logs `support_link_opened` and the view opens the URL with `openURL`
- [ ] Swift ViewModel test: the action logs the event and hands over the URL; with no URL the state has no coffee row
- [ ] Previews cover Settings with and without the coffee row
- [ ] New `.xcstrings` keys are added in English and German
- [ ] New strings pushed to Loco
- [ ] Tapped in the simulator with a URL set: the browser opens on it
- [ ] The iOS build passes (docs/DEVELOPMENT.md)
