---
status: resolved
blocked_by: [3]
---

# 05: Android: open a Fixture from a Fixture link

**What to build:** tapping a Fixture link on an Android phone with MobTT opens that Fixture, with Home underneath, whether the app was closed or running, and even before onboarding is done.

**Blocked by:** 03

- [x] The app's activity declares a verified App Link intent filter (`autoVerify`) for `https://<links host>/f/`, with the host from a build placeholder
- [x] An incoming Fixture link, on a cold start or a new intent, leaves the back stack as Home with that Fixture's destination on top; Back returns to Home
- [x] A link opens the Fixture even when My Player and My Club aren't set; Back then leads to where the app would normally start
- [x] A URL that is not a Fixture link changes nothing
- [x] `fixture_link_opened` is logged with `start` `cold` or `running` (catalog row first)
- [x] Shell tests cover the above against `BackStackGlobalNavigator`; a manual check with `adb shell am start -a android.intent.action.VIEW -d https://<links host>/f/<id>` opens the Fixture
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: `GlobalNavigator.navigateFromStart` resets the back stack to Home plus the destination; `FixtureLinkViewModel` (club impl) reads the link and logs `fixture_link_opened`. `MainActivity` is `singleTask` and passes links to `MobTTApp`; a recreated activity does not replay its link. The host is the `linksHost` manifest placeholder. Checked on the emulator (`deb`): a cold-start link opened the Fixture (an unknown id showed the error state), a link while running replaced it with Hagenberg 3 – Kefermarkt 4, Share opened the sheet with the full text, and Back led to Home's choose prompt.
