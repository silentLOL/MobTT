# Coding Standards

Read during review. `CLAUDE.md` describes the architecture; this file holds the style rules a reviewer checks. Section numbers match the references in `scripts/check-conventions.sh`, the ADRs and the review skills.

Enforced by tooling, so a green `./gradlew check` covers them: formatting and import order (ktlint, `.editorconfig`), braced `if`/`else` (§5), a `@BatScreenPreview` per Composable file (§10), no dp literals in design-system components and icons only through `BatIcon` (§9). Everything else here is a judgement call for the reviewer.

## 5. Style Rules
* **Interface Separation**: Interfaces for Repositories and API Clients MUST live in separate files from their implementation (e.g., `MeRepository.kt` vs `MeRepositoryImpl.kt`). This ensures clean boundaries and easier testing/mocking.
* **Enum Naming Convention**: Enum values MUST always be defined in UPPER_CASE (e.g., `Status.ACTIVE`, `TaskType.TODO`).
* **Feature Folder Layout**: A screen's folder holds only the screen and its ViewModel (or the controller/model that drives it). Everything else goes into subfolders: `Views/` for views, `Models/` for presentation models and UI state, `Formatting/` for formatters, `PreviewContent/` for preview data (Kotlin packages lowercase: `views`, `models`, `formatting`, `preview`). Each screen gets its own folder, never a `Views/` folder, e.g. iOS `DetailFeature/Booking/`, Android `presentation/timeslot/`.
* **Screen/View Naming Convention**: Full-screen SwiftUI views / Jetpack Compose composables MUST be suffixed `Screen` (e.g. `OfferDetailScreen`). Smaller reusable components/views MUST be suffixed `View` (e.g. `PeriodInfoView`). Applies on both iOS and Android.
  * *Design-system exception:* `:android:core:design-system` components are named by role without the suffix (`BatCard`, `BatPrimaryButton`). `View` appears only where the bare name is taken by a token object (`BatIconView` beside `BatIcon`) or reads as a data type (`BatStatView`, `BatFormResultView`).
* **Braced Control Flow**: Every `if`/`else` body is a braced block on its own lines, including early returns, statements inside lambdas, and `if` used as an expression:
  ```kotlin
  if (isBooking) {
    return
  }
  val color = if (isSelected) {
    colors.primary
  } else {
    colors.text
  }
  ```
* **SwiftUI View Type Erasure:** Avoid `AnyView` for optional/overridable child views (e.g. a customizable fallback or label). Instead, make the component generic over the child view type and provide the default via `extension Foo where Content == EmptyView { init(...) }`.
* **String Key Naming Rule:** All string resource keys must follow the `{module}_{context}_{type}` convention.
  * `{module}`: The feature or scope (e.g., `login`, `discover`, `core`).
  * `{context}`: What the string describes (e.g., `welcome_message`, `password`).
  * `{type}`: Its UI function (e.g., `title`, `button`, `hint`, `error`, `cd` for accessibility).
  * *Examples:* `authentication_login_primary_button`, `discover_pool_title`, `core_back_button_cd`.

## 9. Component Conventions
*   **Backgrounds:** Every screen and every bottom sheet uses the flat app background via a single design-system modifier or scaffold parameter (Android: `Modifier.batContentBackground()`, applied by `BatScreenScaffold`; iOS: `.batBackground(.default)`). Do not use hardcoded gradients or colors for the root layout.
*   **Semantic Icons:** NEVER reference `R.drawable.*` directly for UI icons. All icons must be registered in and referenced via the semantic `BatIcon` object (e.g., `BatIcon.Logo`).

## 10. Multi-Preview Annotations (Android)
Custom multi-preview annotations live in `:android:core:design-system`.

- **`@BatScreenPreview`**: The preview annotation for **all** Composables, screens and components alike. It generates multiple previews covering Light mode, Dark mode, small devices, tablets, and narrow widths, so responsive layout behavior is checked everywhere.

**Mandatory:** every new or changed Composable view, screen or component, must ship with a `@BatScreenPreview` preview. A commit adding a Composable without one is incomplete. Cover the meaningfully distinct UI states (e.g. loading/loaded/error, selected/unselected), not just the default.

```kotlin
@BatScreenPreview
@Composable
private fun MyComponentPreview() {
    BatTheme {
        MyComponent()
    }
}
```

### SwiftUI Previews (iOS)
The same rule applies on iOS: every new or changed SwiftUI `View` must ship with an Xcode `#Preview` block. Wrap stateful views with representative sample data rather than leaving the preview on defaults/empty state only.

## 12. Accessibility Standards **(optional, adjust to your legal context)**
All UI must adhere to **EN 301 549** and **WCAG 2.1 Level AA**.

1.  **Screen Reader Compatibility**:
    *   **Android**: Use `semantics` modifiers for content descriptions, state announcements (selected/toggled), and headings.
    *   **iOS**: Use `.accessibilityLabel()`, `.accessibilityValue()`, and `.accessibilityTraits()`.
    *   Ensure logical reading order and meaningful labels for all interactive elements.
    *   **Label Reuse**: If a UI element has a visible text label, reuse that label's string resource for its accessibility label/`contentDescription` instead of creating a separate `cd_` key for identical content.
2.  **Dynamic Text Resizing**: Support system font scaling up to 200% without layout breakage.
    *   **Android**: Always use `sp` for font sizes. Avoid hardcoded heights on containers holding text.
    *   **iOS**: Use Dynamic Type (`@ScaledMetric` or native styles) and allow text wrapping.
3.  **Color Contrast**: 4.5:1 for text (3:1 for large text); 3:1 for UI components/graphics. Validate token pairs for both Light and Dark themes.
4.  **Touch Targets**: Minimum 44x44 dp/pt. Android's 48x48 dp is preferred.
5.  **Focus Management**: No focus traps. Users must be able to navigate into and out of all components (modals, bottom sheets) using assistive technologies.
6.  **Legal Compliance**: Include an Accessibility Statement and a feedback mechanism for reporting barriers.

## 16. Code Commentary Guidelines

1. **Prioritize Intent Over Comments**: Express intent through clear names. Only comment when syntax and naming can't convey the full picture.
2. **Explain "Why", Never "What"**:
   * **Do NOT** write comments that explain standard syntax, summarize control flow, or echo what the code does.
   * **DO** explain non-obvious business logic, domain constraints, trade-offs, or workarounds for third-party bugs/APIs.
   * Doc comments (`/** */`, `///`) carry only what the signature can't: what `null` means, a side effect, a backend quirk, an example of formatted output. A self-explaining type, property or function gets none.
3. **IDE & Organizational Markers Permitted**: `// MARK: -` in Swift and `#pragma mark` are allowed. In Kotlin, structure files with functions and classes instead. No ASCII banner lines or visual dividers.
4. **No Conversational Noise or AI Meta-Talk**: No narrative commentary, inner reasoning, or notes about your generation/editing process in code.
5. **Actionable Notes Only**: Use `// TODO:` or `// FIXME:` for temporary hacks or pending work. State *what* needs to be done, without narrative.
6. **Clean Edit Hygiene**: Delete outdated or contradictory comments when modifying surrounding code. No commented-out/dead code.
