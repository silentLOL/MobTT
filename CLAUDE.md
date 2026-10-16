# Agent Context: KMP Shared Domain Library

Verification commands (Gradle, `xcodebuild`): [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md). Run the iOS build after touching `iosApp/` or `shared-umbrella/`.

Style rules (naming, folder layout, previews, accessibility, comments; §5, §9 Component Conventions, §10, §12, §16) live in [`CODING_STANDARDS.md`](CODING_STANDARDS.md) under the same section numbers. ktlint and `scripts/check-conventions.sh` enforce the mechanical ones; run `./gradlew check` before committing.

## 1. Project Philosophy
This project is a Kotlin Multiplatform (KMP) application using a Shared Domain Library pattern. We share the "Business Rules" and "Infrastructure," but we keep the "User Experience" and "State Logic" 100% native.

**Sharing Boundary:** Domain Layer (UseCases), Data Layer (Repositories/DB), and Core Resources.
**Native Sovereignty:** ViewModels, Navigation, and UI are implemented separately on each platform to ensure a zero-compromise native feel.

> **Architecture Decision Records:** This file describes *how the system works today*. The *why* behind significant decisions, and the alternatives weighed, lives in [`docs/adr/`](docs/adr/README.md). Consult it before changing an established pattern, and add a new ADR (start from [`docs/adr/template.md`](docs/adr/template.md)) when you make a significant architectural decision.

## 2. Architectural Layers

### Core Layer (Shared KMP)
**Purpose:** Low-level infrastructure and external integrations.
**Modules:**
* `:core:database` (SQLDelight)
* `:core:network` (Ktor)
* `:core:preferences` (Jetpack DataStore KMP)
* `:core:platform` (Platform-specific shared code)
* `:core:di` (Koin orchestration, `initKoin`). Depends on every `:domain:*` and `:core:*` module it registers, the one place where `core` may import `domain`.
* Add further `:core:*` modules (e.g. `:core:health-api`, expect/actual bridges) as needed.

### Android Layer (Pure Android)
**Purpose:** Purely Android-specific modules.
**Modules:**
* `:android:core:localization` (strings/fonts via Android Resources)
* `:android:core:design-system` (design tokens and Compose components; pure, no domain dependency)
* `:android:core:ui` (domain-aware Android UI glue shared by features)
* `:android:feature:navigation` (adaptive navigation structure)
* `:android:feature:{name}` — each split into `:api` & `:impl` (see §13 Navigation Architecture)

### Domain Layer (Shared KMP)
**Purpose:** The "Shared Brain." Business logic, models, and UseCases.
**Structure:** Granular modules (e.g., `:domain:activity`, `:domain:user`).
**Rule:** UseCases must return `Flow<T>` or `suspend` functions.

### Presentation & UI (Native)
**Android:** Native ViewModels and Screens live in the `android:feature/` modules. `androidApp/` is a pure Android application module that acts as a thin navigation shell, managing global configuration and the main `NavHost`. It leverages AGP 9's "Built-in Kotlin" support.
**iOS:** Swift ViewModels and SwiftUI views live in `iosApp/` (Xcode). Dependencies are resolved via a `KoinHelper` exported from `shared-umbrella`.

## 3. Build Variants & Flavors
Both platforms share a four-environment flavor setup: `deb`, `dev`, `beta`, `prod`. Each produces a distinct app name (`[Debug] BatMate`, `[Dev] BatMate`, `[Beta] BatMate`, `BatMate`). Document the per-platform mechanism (manifest placeholders vs. `.xcconfig`) in `docs/BUILD_VARIANTS.md`.

## 4. Folder Structure
```plaintext
├── android/              # Android-only modules (core & features)
├── build-logic/          # Convention Plugins for shared config
├── core/                 # Shared Infrastructure (KMP)
├── domain/               # Shared Business Logic (KMP)
├── shared-umbrella/      # Framework export module for iOS
├── androidApp/       # Native Android Application (Pure Android module)
└── iosApp/           # Native iOS Xcode project
```

## 5. Technical Standards & Tooling
* **Dependency Injection**: Koin is the primary DI framework. All modules (KMP or Pure Android) must define a Koin `module`. Central orchestration happens in `:core:di`.
* **iOS Bridge:** SKIE is mandatory. It converts Kotlin Flow and Sealed Classes into native Swift AsyncSequence and Enums.
* **Preferences:** Jetpack DataStore KMP is used for asynchronous key-value storage.
* **Design Tokens:** Managed in `:android:core:design-system`.
* **Android Adapter:** Compose Theme in `android:core:design-system`.
* **iOS Adapter:** Swift extensions in `iosApp` wrapping the design tokens.
* **Localization:** Native ViewModels/Views pull from platform-specific resources (`strings.xml` in `:android:core:localization`, `.xcstrings` on iOS). Shared logic only manages the keys or IDs if necessary.

## 6. Developer Workflows

### Adding a New Feature
1. Create a granular module in `domain/` (using `bat.kmp.library` plugin).
2. Define Domain Models, Repository Interface, and UseCases (using `suspend`/`Flow`).
3. Create a granular module in `android/feature/` (using a pure Android plugin).
4. Define a Koin `Module` in the `feature-module/.../di` package.
5. Implement the Android ViewModel & Screen in the `feature-module/.../presentation` package.
6. Register the new Koin modules (both domain and feature) in `:core:di`'s `initKoin` function (a single `sharedModules` list; platform-specific modules are passed through `initKoin`'s `appDeclaration`).
7. Register the new feature's screen in the app module's main `NavHost`.
8. Implement the Swift ViewModel & Screen in `iosApp`.

### Updating Design Tokens
1. Update the tokens in `:android:core:design-system` (e.g. `BatColors.kt`).
2. Update the Material mapping (`BatMaterialMapping.kt`: color scheme, typography, shapes).
3. Update the colour sets in the iOS design-system package (`Media.xcassets/Colors/{Palette,Semantic}`) and the Swift accessors.
4. Keep token names identical on both platforms.

## 7. Forbidden Patterns
* **No Shared ViewModels:** Presentation logic must stay in its native language.
* **No UI in Domain:** The `domain/` and `core/` folders must never import UI frameworks.
* **No Direct Module Imports in iOS:** Xcode must only ever link against the SharedFramework produced by the `shared-umbrella` module.

## 8. Build Logic & Convention Plugins
The project uses a `build-logic` module to centralize Gradle configuration.

### Plugin Selection Rules (CRITICAL)
To maintain architectural integrity, you **must** use the correct plugin based on the module's location:

1.  **Shared KMP Modules (`core/`, `domain/`, `shared-umbrella/`)**:
    -   **Plugin:** `id("bat.kmp.library")`
    -   **Purpose:** Enables multiplatform targets (Android + iOS).
    -   **Structure:** Uses `src/commonMain`, `src/androidMain`, `src/iosMain`.

2.  **Pure Android Modules (`android/`, `androidApp/`)**:
    -   **Plugin:** `id("bat.android.library")`
    -   **Purpose:** Strictly Android-only implementation (Design System, Android Features).
    -   **Constraint:** **NEVER** apply the KMP plugin here. These modules must remain pure Android to avoid unnecessary iOS build overhead and incorrect source set generation.
    -   **Structure:** Uses standard Android `src/main`.

### Usage
Apply the chosen plugin at the top of your `build.gradle.kts`:
```kotlin
plugins {
    // Choose ONE based on the rules above:
    id("bat.android.library") // For modules in the /android folder
    // id("bat.kmp.library")   // For modules in /core or /domain
}
```
- **Implementation Details**: The convention plugins are implemented in `build-logic/src/main/kotlin`. Configuration for Android in KMP modules is nested inside the `kotlin { androidLibrary { ... } }` block.
    - **AGP 9 & Resources Note:** By default, the `com.android.kotlin.multiplatform.library` plugin does *not* package Android resources to optimize builds. If shared modules ship JetBrains Compose Resources, set `android.experimental.kmp.enableAndroidResources=true` in the `KmpLibraryPlugin`.

## 9. Design System Architecture
Strict separation between Android and iOS implementations, following **"Native Sovereignty"** and **"Platform-Specific Optimization"**. There is no shared Kotlin code for design primitives; each platform defines its own tokens. Token names are shared across platforms.

### 1. The Android Implementation (`android/core/design-system`)
**Purpose:** Official brand tokens and themes for Jetpack Compose. A pure Android module (`src/main`).
*   **Primitives:** `Color`, `TextStyle`, and `Dp` tokens defined directly in the module (e.g., `BatColors`, `BatTypography`).
*   **BatTheme:** A native Composable theme that:
    1.  Maps custom semantic colors, typography and shapes to Material 3 (`BatMaterialMapping.kt`).
    2.  Provides `CompositionLocal`s for direct access to custom brand tokens.
*   **Accessor Object:** `BatDesign.colors`, `.typography`, `.spacing`, `.shapes`, `.sizing` within Composables. One consistent API surface across all Android features.
*   **Shadows:** `Modifier.batShadow(BatShadow.X, shape)`.
*   **Components:** Native UI components (e.g., `BatPrimaryButton`) live in `ui/components` and consume the `BatDesign` tokens.

### 2. The iOS Implementation (`iosApp/.../Common/DesignSystem/`)
**Purpose:** Native SwiftUI tokens and themes.
*   **Native Primitives:** Colors, typography, and spacing defined in Swift/SwiftUI (e.g., `BatTypography.swift`, `BatColor.swift`). Colours live in `Resources/Media.xcassets/Colors/{Palette,Semantic}`; dark appearances are defined on the `Semantic` sets.
*   **Shadows:** `.batShadow(.x)`.
*   **Native Feel:** All UI components and themes are implemented natively in SwiftUI.

### Naming Conventions
*   **Global Prefix:** All design system objects and themes must be prefixed with `Bat`.
*   **Clean Naming:** Primitives use a clean naming convention (e.g., `BatColors`, `BatSpacing`), with no platform suffix.

## 11. Android Specific: Unidirectional Data Flow (UDF) Architecture
A strict UDF pattern using Kotlin Coroutines, StateFlows, and Jetpack Compose.

* **State flows down**: The UI state is built in the ViewModel and exposed as a single stream. The Composable Screen consumes this state and passes required data down to its child components.
* **Events flow up**: User interactions within the UI components invoke lambdas that bubble up to the screen level and are forwarded to a single unified event handler in the ViewModel.

### 1. The ViewModel (State & Business Logic)
The ViewModel (e.g., `OverviewViewModel`) is the central orchestrator of business logic and state construction.

* **State Aggregation**: Instead of holding multiple mutable variables, it reacts to domain streams (Repositories/UseCases) and UI selections. It typically uses operators like `combine` to merge multiple flows into a single `StateFlow<OverviewUiState>`.
* **Action Handling**: It exposes a single `fun onAction(action: OverviewAction)` method. The UI sends intent signals by passing specific Action sealed classes (e.g., `OverviewAction.AddDeviceClicked`). The ViewModel then executes business logic, analytics tracking, state mutation, or triggers navigation.
* **One-off Events**: For single execution triggers (navigation, toasts), it uses a Kotlin `Channel<OverviewEvent>` exposed to the UI as a `Flow` (events).

### 2. The Screen (UI & Composition)
The Composable Screen (e.g., `OverviewScreen`) is the presentation layer. It remains completely stateless concerning business logic.

* **State Consumption**: `uiState by viewModel.uiState.collectAsStateWithLifecycle()`.
* **Declarative Rendering**: The screen reacts to the current `uiState` and draws child components accordingly.
* **Event Handling**: It listens to the ViewModel's one-off event channel using an `ObserveAsEvents` side-effect wrapper. When an event is caught, it triggers the corresponding navigation lambda.
* **`Context`-dependent Side Effects**: One-off events that require a `Context` (e.g. `startActivity`, `CustomTabsIntent`) are handled in the Screen, never the ViewModel. ViewModels must not hold a `Context` reference. Keep the `ObserveAsEvents` branch itself a one-liner by extracting the platform call into a private `Context` extension function (e.g. `Context.launchMaps(...)`) in the same file.
* **Navigation Abstraction**: The screen does not know about the application's router. Navigation destinations are abstracted away as function parameters.
* **`BatAsyncButton` Exception** *(only if you build such a component)*: A ViewModel method invoked directly as an async button's `onClick` (a `suspend () -> Unit`) may remain a public `suspend fun` outside the `onAction` dispatcher. The button owns its own transient loading/disabled state internally, so there is nothing for `onAction` to route. Record this in an ADR.

### 3. The Navigation Layer
The Navigation layer (e.g., `DiscoverNavigation.kt`) ties the feature into the app's routing graph using `androidx.navigation3`.

* **Destinations**: Strongly-typed objects or classes implementing the `GlobalAppDestination` interface.
* **Entry Scopes**: An `EntryProviderScope` maps a destination key to the actual Composable Screen.
* **Wiring Callbacks**: Provides the concrete implementation for the navigation lambdas exposed by the Screen, translating them into `navigator.navigate(...)` calls.
* **Returning a Result**: `androidx.navigation3` has no built-in "navigate for result" API. When a pushed destination needs to hand a value back, use a small feature-scoped singleton bus backed by a `MutableSharedFlow` (Koin `single`): the pushed destination's ViewModel publishes into it before calling back via its own NavEvent/`goBack()`, and the calling ViewModel collects it from `init`.

## 13. Navigation Architecture (Android)
A decentralized, type-safe navigation system for module isolation and build performance.

### 1. The API Module (:feature:x:api)
**The Contract:** Defines where you can go and what data is required.
- **Destinations:** `@Serializable` data classes/objects implementing `GlobalAppDestination`.
- **Public API:** Exposes `GlobalNavigator.navigateToX()` extension functions as the only entry point for other features. These can internally use an optional navigation builder to configure state preservation (saveState, restoreState, popUpTo).

### 2. The Implementation Module (:feature:x:impl)
**The Routing:** Dictates what happens when a destination is requested.
- **Local Events:** Defines `XNavEvent` (e.g., Back, ToUserDetail) as local UI intents.
- **Graph Builder:** Contains `xGraph(navigator: GlobalNavigator)` as an `EntryProviderScope<NavKey>` extension in a dedicated `navigation` package to register the feature.
- **Decoupling:** Maps local `XNavEvent`s to global `GlobalNavigator` actions within a private handler. UI Screens stay ignorant of the global navigation graph.
- **Strict Isolation:** Implementation modules must only depend on the `:api` modules of other features. NEVER depend on another feature's `:impl` module.

## 14. Networking, Authentication & Configuration
- **BuildKonfig**: Environment-specific variables are defined in `:core:network/build.gradle.kts` within the `buildkonfig` block. Never store secrets or URLs in source code.
- **Environmental Configuration**: Never hardcode URLs, Client IDs, or environment-specific toggles in feature code. Consume them via `AppConfig` (injected through Koin, `:core:network`), which is powered by `BuildKonfig`.
- **Unified Localhost**: For local development, all platforms must target `localhost`. Do NOT implement code-level bridges (like `10.0.2.2`). Android developers use `adb reverse`.
- **Token Management**: The Ktor `Auth` plugin injects `Bearer` tokens into outbound requests and handles `401 Unauthorized` by transparently fetching new tokens. A `TokenProvider` interface breaks the circular dependency between the network and domain modules.
- **Secure Storage**: Auth tokens are stored via a common `SecureStorage` interface.
  - **iOS**: Keychain (`kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`).
  - **Android**: `EncryptedSharedPreferences` (`androidx.security:security-crypto`), backed by the Android Keystore.
- **Stateless Repositories**: Authentication session state (like the PKCE `code_verifier`) is managed by the caller (ViewModel) via an `AuthSession` model, keeping the domain repository stateless.
- **Logging**: Use `co.touchlab.kermit.Logger` for all core domain and network operations. Never log sensitive data (tokens, full secrets).

## 15. Global Compiler Opt-ins
Certain experimental APIs are enabled at the compiler level in the Gradle convention plugins (build-logic). Do not manually add `@OptIn` for:
- `androidx.compose.material3.ExperimentalMaterial3Api`

## Agent skills

### Issue tracker
Issues and specs live as markdown files under `.scratch/<feature>/`. See `docs/agents/issue-tracker.md`.

### Triage labels
Default five-role vocabulary (`needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`), recorded as a `Status:` line in each issue file. See `docs/agents/triage-labels.md`.

### Domain docs
Single-context: one `GLOSSARY.md` at the repo root plus `docs/adr/`. See `docs/agents/domain.md`.
