# 02: Build variants and app name

**What to build:** both platforms build in four environments, `deb`, `dev`, `beta` and `prod`, and each installs with its own user-facing name: `[Debug] MobTT`, `[Dev] MobTT`, `[Beta] MobTT` and `MobTT`. The package prefix stays `at.stefanirndorfer.batmate`.

**Blocked by:** None (can start immediately)

**Status:** done

- [x] Android has the four flavors, with the app name supplied through manifest placeholders
- [x] iOS has the four configurations, with the app name supplied through `.xcconfig`
- [x] Each variant can be installed next to the others on one device
- [x] `docs/BUILD_VARIANTS.md` documents the mechanism per platform
- [x] The environment is exposed to the shared code only through `AppConfig`, not hardcoded in features

**Manual check outstanding:** install the four variants side by side on one device.
