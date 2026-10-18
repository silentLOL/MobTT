# 04: iOS design system

**What to build:** the iOS app has the same visual language as Android. Colours, typography, spacing, shadows and background follow the same token names and the contrast-corrected values from ADR-0003, and SwiftUI components mirror the Android ones.

**Blocked by:** None (can start immediately)

**Status:** done

- [x] Palette and semantic colour sets exist in the asset catalog, with dark appearances on the semantic sets
- [x] `BatColor`, `BatTypography`, `BatSpacing` and shape and sizing accessors use the same names as Android
- [x] `.batShadow(.x)` and `.batBackground(.default)` exist, and no screen needs hardcoded gradients or colours for its root layout
- [x] Components mirror the Android set: top bar, logo mark, card, hero card, buttons, badge, live pill, section header, text field, stat view, form result, toast, initials avatar and tab bar item
- [x] Every view ships with a `#Preview` using representative sample data
- [x] Full-screen views are suffixed `Screen` and reusable views `View`, and no `AnyView` is used for overridable children
- [x] Accessibility labels and traits are set and Dynamic Type works up to 200%

**Manual check outstanding:** 200% Dynamic Type and VoiceOver on a device; the tab bar shows icons only at accessibility sizes (CODING_STANDARDS §12).
