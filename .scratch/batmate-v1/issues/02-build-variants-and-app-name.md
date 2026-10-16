# 02: Build variants and app name

**What to build:** both platforms build in four environments, `deb`, `dev`, `beta` and `prod`, and each installs with its own user-facing name: `[Debug] Bat Mate`, `[Dev] Bat Mate`, `[Beta] Bat Mate` and `Bat Mate`. The code and module name stays `BatMate`.

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

- [ ] Android has the four flavors, with the app name supplied through manifest placeholders
- [ ] iOS has the four configurations, with the app name supplied through `.xcconfig`
- [ ] Each variant can be installed next to the others on one device
- [ ] `docs/BUILD_VARIANTS.md` documents the mechanism per platform
- [ ] The environment is exposed to the shared code only through `AppConfig`, not hardcoded in features
