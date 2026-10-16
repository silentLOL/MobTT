# ADR-0003: Android design system as a pure, domain-free module with semantic tokens

**Status:** Accepted
**Date:** 2026-10-16
**Deciders:** Tech Lead

## Context
The HTML prototype (`ttv_mobile_app.html`) expresses its look through Tailwind
utility classes and a handful of config colours. Per [ADR-0002](0002-shared-domain-library-with-native-presentation.md)
there is no shared UI, so Android and iOS each need their own implementation of
the same visual language, with identical token names (`CLAUDE.md` §9). The
prototype also uses colour pairs that miss WCAG 2.1 AA (e.g. slate-400 text on
white, white on emerald-500), which `CLAUDE.md` §12 forbids.

## Decision
We will extract the prototype into `:android:core:design-system`, a pure Android
module built with the new `bat.android.library` convention plugin
(`build-logic/`). It exposes **semantic** tokens (`BatDesign.colors.textSecondary`,
not "slate-600") backed by a private palette, a `BatTheme` that maps them to
Material 3, and stateless components that take all text as parameters. It has no
dependency on `domain/` and owns no string resources.

## Options Considered

### Option A: Semantic tokens over a palette, own components on Material 3 — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Medium |
| Cost             | One-off extraction; tokens mirrored once on iOS |
| Scalability      | High: re-theming touches one file per platform |
| Team familiarity | High |

**Pros:** dark mode and contrast fixes live in one mapping; iOS can mirror token
names 1:1. **Cons:** more indirection than using Material roles directly.

### Option B: Use Material 3 roles only
**Pros:** least code. **Cons:** prototype-specific concepts (hero gradient, live
status, win/loss) have no Material role and would leak as ad-hoc colours into features.

### Option C: Port Tailwind class names literally
**Pros:** closest to the prototype. **Cons:** names describe appearance, not
purpose, so they cannot carry the contrast corrections or a dark-mode mapping.

## Trade-off Analysis
Option A pays a small indirection cost to keep feature code free of raw colours
and to make accessibility fixes a single-token change. B and C push that cost
into every feature.

## Consequences
- **Easier:** theming, dark mode, accessibility audits; features compose
  `Bat*` components and previews (`@BatScreenPreview`).
- **Deviations from the prototype (contrast):** tertiary text is slate-500, not
  slate-400; win/success fills use emerald-700; input borders use a stronger
  `borderInput` token; muted hero text is brand-50.
- **Harder:** every token change must be repeated on iOS (`CLAUDE.md` §6).
- **Revisit when:** Inter is bundled (currently the system font), or when
  `:android:core:localization` lands and fonts move there as `CLAUDE.md` §2 states.
