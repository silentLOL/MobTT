---
status: resolved
blocked_by: []
---

# 01: Release signing and reproducible prod bundle

**What to build:** Release signing config for `prod` from untracked settings, R8 on, versioning by Gradle property, documented in `docs/RELEASE_ANDROID.md`.

- [x] Signing from `local.properties` or `RELEASE_*`, unsigned fallback without them
- [x] R8 and resource shrinking on; `prod` APK run on an emulator
- [x] `bundleProdRelease` builds; 16 KB alignment and ABIs checked
- [x] `docs/RELEASE_ANDROID.md`
