---
status: ready-for-agent
blocked_by: [2, 3]
---

# 04: Crashlytics from shared code

**What to build:** Reporting of errors that originate in shared Kotlin.

- [ ] A Kermit `LogWriter` per platform is passed through `initKoin`'s `appDeclaration`: `Error` with a throwable is a non-fatal, lower severities are breadcrumbs.
- [ ] Unhandled Kotlin exceptions on iOS appear with readable Kotlin stack traces.
- [ ] Logged URLs and exception messages never include the XTTV API key or tokens, shown by a test.
- [ ] Nothing is sent without Usage Consent.
- [ ] `./gradlew check` and the iOS build pass.
