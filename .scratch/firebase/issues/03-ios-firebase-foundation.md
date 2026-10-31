---
status: ready-for-agent
blocked_by: [1]
---

# 03: iOS Firebase foundation

**What to build:** The Swift analytics and crash-reporting protocols, the Firebase implementation (SPM, without AdSupport) and a no-op, with per-configuration plist handling.

- [ ] Firebase is added through Swift Package Manager without the AdSupport variant; no ATT prompt.
- [ ] One `GoogleService-Info.plist` per configuration (Debug, Dev, Beta, Release) is selected by the build; templates exist and real files are git-ignored.
- [ ] Auto-collection is off in Info.plist; collection follows Usage Consent at launch and on change.
- [ ] In the Debug configuration, Analytics is a no-op and disabled in the SDK, while Crashlytics stays enabled (after consent) and reports to the `deb` Firebase app.
- [ ] Builds without the plists succeed, with the SDK disabled.
- [ ] The Crashlytics dSYM upload runs for Dev, Beta and Release.
- [ ] The iOS build passes.
