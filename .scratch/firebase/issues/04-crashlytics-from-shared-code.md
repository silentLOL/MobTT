---
status: resolved
blocked_by: [2, 3]
---

# 04: Crashlytics from shared code

**What to build:** Reporting of errors that originate in shared Kotlin.

- [x] A Kermit `LogWriter` per platform is passed through `initKoin`'s `appDeclaration`: `Error` with a throwable is a non-fatal, lower severities are breadcrumbs.
- [x] Unhandled Kotlin exceptions on iOS appear with readable Kotlin stack traces.
- [x] Logged URLs and exception messages never include the XTTV API key or tokens, shown by a test.
- [x] Nothing is sent without Usage Consent.
- [x] `./gradlew check` and the iOS build pass.
