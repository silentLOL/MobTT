---
status: ready-for-agent
blocked_by: [3, 5]
---

# 06: iOS: open a Fixture from a Fixture link

**What to build:** tapping a Fixture link on an iPhone with MobTT opens that Fixture on the Home tab, whether the app was closed or running, and even before onboarding is done.

**Blocked by:** 03, 05

- [ ] The app declares the Associated Domain `applinks:<links host>` for every flavor
- [ ] The shell handles an incoming Fixture link by selecting the Home tab and pushing the Fixture onto its stack
- [ ] A link opens the Fixture even when My Player and My Club aren't set
- [ ] A URL that is not a Fixture link changes nothing
- [ ] `fixture_link_opened` is logged with `start` `cold` or `running`
- [ ] Shell tests cover the above; the iOS build passes
