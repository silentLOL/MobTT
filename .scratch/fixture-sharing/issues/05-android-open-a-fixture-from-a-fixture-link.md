---
status: ready-for-agent
blocked_by: [3]
---

# 05: Android: open a Fixture from a Fixture link

**What to build:** tapping a Fixture link on an Android phone with MobTT opens that Fixture, with Home underneath, whether the app was closed or running, and even before onboarding is done.

**Blocked by:** 03

- [ ] The app's activity declares a verified App Link intent filter (`autoVerify`) for `https://<links host>/f/`, with the host from a build placeholder
- [ ] An incoming Fixture link, on a cold start or a new intent, leaves the back stack as Home with that Fixture's destination on top; Back returns to Home
- [ ] A link opens the Fixture even when My Player and My Club aren't set; Back then leads to where the app would normally start
- [ ] A URL that is not a Fixture link changes nothing
- [ ] `fixture_link_opened` is logged with `start` `cold` or `running` (catalog row first)
- [ ] Shell tests cover the above against `BackStackGlobalNavigator`; a manual check with `adb shell am start -a android.intent.action.VIEW -d https://<links host>/f/<id>` opens the Fixture
- [ ] `./gradlew check` passes
