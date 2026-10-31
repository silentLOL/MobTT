---
status: ready-for-agent
blocked_by: [1]
---

# 02: Android Firebase foundation

**What to build:** `:android:core:analytics` with `AnalyticsTracker` and `CrashReporter` interfaces, the Firebase implementation, a no-op fake and a Koin module, and the Gradle setup for all four flavors.

- [ ] Google Services and Crashlytics plugins apply only when the flavor's `google-services.json` is present; `.example` templates exist and real files are git-ignored.
- [ ] Firebase auto-collection is off in the manifest; collection follows Usage Consent at launch and on change.
- [ ] `AD_ID` is removed and ad personalization is disabled.
- [ ] Automatic screen reporting is off.
- [ ] In the `deb` flavor, Analytics is a no-op and disabled in the SDK, while Crashlytics stays enabled (after consent) and reports to the `deb` Firebase app.
- [ ] A build without the config files still succeeds, with the SDK disabled.
- [ ] `docs/BUILD_VARIANTS.md` documents the setup; `./gradlew check` passes.
