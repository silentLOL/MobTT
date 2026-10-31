# Architecture Decision Records

This directory holds the MobTT architecture decision log. See
[ADR-0001](0001-record-architecture-decisions.md) for why we keep ADRs and how
the process works. New records start from [template.md](template.md).

`CLAUDE.md` / `README.md` describe **how the system works today**; ADRs record
**why we chose it**. When an ADR supersedes prose in those files, the prose
should link here instead of duplicating the rationale.

## Index

| ADR | Title | Status |
|-----|-------|--------|
| [0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [0002](0002-shared-domain-library-with-native-presentation.md) | Shared Domain Library with native presentation | Accepted |
| [0003](0003-android-design-system-module.md) | Android design system as a pure, domain-free module with semantic tokens | Accepted |
| [0006](0006-app-config-from-platform-build-environment.md) | `AppConfig` supplied by each platform's build environment | Accepted |
| [0005](0005-android-shell-navigation-and-fake-directory.md) | Android shell navigation contracts and a debug-only fake directory | Accepted |
| [0007](0007-xttv-backed-repositories-in-core-xttv.md) | XTTV-backed repositories live in `:core:xttv`; the fake directory stays for `deb` | Accepted |
| [0008](0008-xttv-hosts-per-association-and-id-strategy.md) | One XTTV host per Association, and plain string IDs | Proposed |
| [0009](0009-native-firebase-behind-interfaces-with-opt-in-consent.md) | Native Firebase behind interfaces, opt-in consent, no advertising IDs | Proposed |
