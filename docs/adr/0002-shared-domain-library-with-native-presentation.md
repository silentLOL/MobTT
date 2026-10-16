# ADR-0002: Shared Domain Library with 100% native presentation

**Status:** Accepted
**Date:** 2026-10-16
**Deciders:** Tech Lead

## Context
BatMate targets Android and iOS and must feel first-class on both. We had to
decide how much of the app to share via Kotlin Multiplatform. The forces:

- **Correctness & maintenance:** business rules (UseCases, data access,
  networking) should exist once, not be re-implemented and drift per platform.
- **Native feel:** UX cannot feel like a cross-platform compromise; accessibility
  and platform idioms matter.
- **Team topology:** distinct Android (Compose) and iOS (SwiftUI) engineers who
  each want to work in their native idiom and toolchain.

## Decision
We will use a **Shared Domain Library** boundary. KMP owns the Domain layer
(models, repository interfaces, UseCases returning `Flow<T>`/`suspend`) and the
Data/Core infrastructure. **Presentation and state (ViewModels, navigation, and
UI) are implemented natively and separately on each platform.** Shared
ViewModels and shared UI are forbidden (`CLAUDE.md` §7).

## Options Considered

### Option A: Shared Domain Library, native presentation — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Medium: KMP toolchain + one interop bridge |
| Cost             | Business logic written once; UI twice |
| Scalability      | High: layers are granular modules |
| Team familiarity | High: each team stays in its native stack |

**Pros:** Single source of truth for business rules; zero-compromise native UX;
teams stay productive in Compose / SwiftUI. **Cons:** UI and state logic written
twice; requires a Kotlin↔Swift bridge (SKIE + `shared-umbrella`).

### Option B: Fully shared UI (Compose Multiplatform)
**Pros:** Write the UI once too; smallest total code. **Cons:** iOS UX is a
follower of Compose's iOS support; harder to hit native accessibility and
platform-idiom expectations; iOS engineers pushed out of their native stack.

### Option C: Fully native, no sharing (two independent apps)
**Pros:** Maximum native freedom; no KMP toolchain. **Cons:** Business rules
duplicated and guaranteed to drift; double the logic bugs.

## Trade-off Analysis
Option B minimises code but spends its savings exactly where the product cannot
afford compromise: the native-feeling UI. Option C protects the UI but
duplicates the risky part (business rules). Option A puts the "write once"
boundary around the logic that benefits most from a single implementation while
leaving the experience layer fully native.

## Consequences
- **Easier:** business-rule changes land once; each platform's UI is idiomatic
  and independently testable.
- **Harder:** every feature needs presentation built twice; a shared-to-native
  bridge must be maintained.
- **Revisit when:** Compose Multiplatform's iOS maturity and accessibility story
  change materially, or if UI duplication cost begins to dominate delivery time.
