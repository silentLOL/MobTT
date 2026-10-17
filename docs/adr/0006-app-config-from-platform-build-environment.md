# ADR-0006: `AppConfig` is supplied by each platform's build environment, not BuildKonfig

**Status:** Accepted
**Date:** 2026-10-17
**Deciders:** Stefan Irndorfer

## Context
Shared code must know the active environment (`deb`, `dev`, `beta`, `prod`) without features hardcoding it. CLAUDE.md §14 planned BuildKonfig in `:core:network` to generate `AppConfig`. BuildKonfig picks one flavor per Gradle invocation through a property, so it cannot follow the four Android flavors of one `androidApp` build, and iOS would need a second selection path.

## Decision
We will define `Environment` and `AppConfig` in `:core:network` and bind the `AppConfig` through `initKoin(config)`. Android passes the flavor's `BuildConfig.ENVIRONMENT`, and iOS passes the `BatEnvironment` Info.plist key, which comes from `ENVIRONMENT` in the `.xcconfig`. Both go through `Environment.fromKey`, which fails fast on an unknown key.

## Options Considered

### Option A: Platform passes the environment key at startup — chosen
**Pros:** the existing flavor and `.xcconfig` mechanisms stay the single source; one build serves every flavor.
**Cons:** the key is spelled in Gradle and in the `.xcconfig` files.

### Option B: BuildKonfig in `:core:network`
**Pros:** matches the earlier plan; generated constants.
**Cons:** one flavor per Gradle invocation, so flavor builds need an extra property mapping on Android and iOS.

## Consequences
Base URLs per environment are added to `AppConfig` by the XTTV API ticket, keyed on `Environment`. CLAUDE.md §14 names `AppConfig` and no longer names BuildKonfig.
