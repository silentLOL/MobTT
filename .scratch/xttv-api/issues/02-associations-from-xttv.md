# 02: Associations from XTTV

**What to build:** The first real data end to end. The Association list comes from XTTV (its "Federation" list), the host that serves each Association is chosen in one place, and every non-debug build on both platforms binds the XTTV-backed repositories while `deb` keeps the fake directory. This removes the release `TODO` and is the first slice that can be demoed against recorded data. See ADR-0007 and ADR-0008.

**Blocked by:** 01 XTTV client foundation.

**Status:** ready-for-agent

- [ ] `AssociationRepository` returns Associations built from recorded XTTV federation XML; XTTV's "Federation" term appears only in the data layer.
- [ ] A single host resolver maps an Association to its host, with `AppConfig` supplying the default host.
- [ ] `deb` builds on Android and iOS bind the fake directory; `dev`, `beta` and `prod` bind the XTTV-backed repositories; the release `TODO` is gone.
- [ ] A failed request surfaces as `DataUnavailable`.
- [ ] Repository tests run on `MockEngine` with recorded XML and assert on domain objects only.
- [ ] The iOS build passes after the wiring change.
