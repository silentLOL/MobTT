---
status: resolved
blocked_by: [1]
---

# 03: iOS Firebase foundation

**What to build:** The Swift analytics and crash-reporting protocols, the Firebase implementation (SPM, without AdSupport) and a no-op, with per-configuration plist handling.

- [x] Firebase is added through Swift Package Manager without the AdSupport variant; no ATT prompt.
- [x] One `GoogleService-Info.plist` per configuration (Debug, Dev, Beta, Release) is selected by the build; templates exist and real files are git-ignored.
- [x] Auto-collection is off in Info.plist; collection follows Usage Consent at launch and on change.
- [x] In the Debug configuration, Analytics is a no-op and disabled in the SDK, while Crashlytics stays enabled (after consent) and reports to the `deb` Firebase app.
- [x] Builds without the plists succeed, with the SDK disabled.
- [x] The Crashlytics dSYM upload runs for Dev, Beta and Release.
- [x] The iOS build passes.
