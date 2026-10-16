# 01: Shared KMP foundation

**What to build:** both apps start with a shared Kotlin core that a UseCase can run on. A `bat.kmp.library` convention plugin exists beside `bat.android.library`, `:core:di` exposes `initKoin` and both apps call it at launch. The template `shared` module is replaced by `shared-umbrella`, which is the only framework Xcode links, and SKIE is applied. A trivial UseCase proves a `kotlin-test` test runs on Android and iOS targets.

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

- [ ] Shared modules in `core/` and `domain/` apply `bat.kmp.library` and target Android and iOS
- [ ] `initKoin` registers modules from a single place and is called by the Android app and the iOS app
- [ ] iOS links only the framework produced by `shared-umbrella`, with SKIE converting Flow and sealed classes
- [ ] A UseCase test in `commonTest` passes on the Android host test and the iOS simulator test
- [ ] The template greeting code and `README` run instructions are updated or removed
- [ ] An ADR records the SKIE and `shared-umbrella` decision if it deviates from CLAUDE.md
