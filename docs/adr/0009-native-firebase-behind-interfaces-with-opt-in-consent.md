# ADR-0009: Native Firebase behind interfaces, opt-in consent, no advertising IDs

**Status:** Proposed
**Date:** 2026-10-31
**Deciders:** Stefan Irndorfer

## Context
We want Crashlytics crash reporting and Firebase Analytics events for user interactions. Interactions happen in native ViewModels (ADR-0002), so shared code has little to track. MobTT's users are in Austria, so GDPR applies, and the app has no ads. The app has four environments (`deb`, `dev`, `beta`, `prod`) with distinct application IDs and bundle IDs (ADR-0006).

## Decision
We will use the native Firebase SDKs on each platform behind our own interfaces (`AnalyticsTracker`, `CrashReporter`), in `:android:core:analytics` and `iosApp/.../Common/Analytics/`. Nothing is collected until the user allows it (Usage Consent), and no advertising identifiers are collected. One Firebase project holds a separate app per environment and platform. Crashlytics collects in every environment including `deb`; Analytics events are sent from `dev`, `beta` and `prod` only. Shared Kotlin code reports to Crashlytics only through a Kermit `LogWriter` that each platform supplies through `initKoin`. The event catalog lives in `docs/ANALYTICS.md` and is the single source of truth for both platforms.

## Options Considered

### Option A: Native SDKs behind interfaces — chosen
| Dimension | Assessment |
|---|---|
| Complexity | Med: two integrations |
| Cost | Two small wrappers |
| Scalability | Interfaces allow swapping the vendor |
| Team familiarity | High: official SDKs and docs |

**Pros:** fits Native Sovereignty; no third-party KMP wrapper; Xcode still links only `SharedFramework`; ViewModels testable with a recording fake.
**Cons:** event names must be kept identical by hand, enforced by a convention check.

### Option B: A KMP Firebase wrapper in `:core:analytics`
**Pros:** one API in shared code.
**Cons:** adds a third-party dependency with its own lag behind Firebase releases and iOS linking friction. Shared code has no interactions to track.

### Option C: Collect by default with an opt-out
**Pros:** more data, simpler UX.
**Cons:** weaker GDPR position; most users never find the toggle.

## Trade-off Analysis
Duplicated wrappers cost little, while a wrapper dependency on the build and release path is a standing risk. Opt-in gives less data but a clear legal footing.

## Consequences
- Easier: testing ViewModels, swapping the analytics vendor, keeping the SDK out of feature modules.
- Harder: the first-run and existing-install flows gain a consent step, and the consent state needs three values (unanswered, allowed, declined).
- Firebase config files are untracked with `.example` templates (as with the XTTV key). Builds without them work, with the SDK disabled.
- Revisit if per-environment Firebase projects are needed, or if a vendor other than Firebase is wanted.
