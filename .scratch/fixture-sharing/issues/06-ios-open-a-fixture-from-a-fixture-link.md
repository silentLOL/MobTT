---
status: resolved
blocked_by: [3, 5]
---

# 06: iOS: open a Fixture from a Fixture link

**What to build:** tapping a Fixture link on an iPhone with MobTT opens that Fixture on the Home tab, whether the app was closed or running, and even before onboarding is done.

**Blocked by:** 03, 05

- [x] The app declares the Associated Domain `applinks:<links host>` for every flavor
- [x] The shell handles an incoming Fixture link by selecting the Home tab and pushing the Fixture onto its stack
- [x] A link opens the Fixture even when My Player and My Club aren't set
- [x] A URL that is not a Fixture link changes nothing
- [x] `fixture_link_opened` is logged with `start` `cold` or `running`
- [x] Shell tests cover the above; the iOS build passes

## Comments

- 2026-11-07: `FixtureLinkViewModel` (iOS) reads the link through `KoinHelper.fixtureIdOfLink` and logs `fixture_link_opened`; `ShellScreen` handles `onOpenURL` by selecting Home and setting its path to the Fixture, after clearing the other tabs and closing Settings. SwiftUI gives no "launched by this link" signal, so a link within 3 seconds of the shell first appearing counts as `cold`. The Associated Domain lives in `Configuration/MobTT.entitlements` with the host from `LINKS_HOST` in `Config.xcconfig`. Not checked on a device: the scripts build without signing, and the domain does not exist yet (07). A signed device build needs the Associated Domains capability on the App ID, which automatic signing adds.
