# Development checks

Run the multi-target Gradle commands in the background; a cold run exceeds two minutes.

## Static checks

```sh
./gradlew check            # ktlint on every module plus scripts/check-conventions.sh
./gradlew ktlintFormat    # auto-fix formatting and import order
```

`check` also renders every `@BatScreenPreview` variant with Roborazzi and Robolectric (`:android:core:design-system:recordRoborazziDebug`). A preview that crashes fails the build. The PNGs land in `android/core/design-system/build/outputs/roborazzi/`; open them to check layout, contrast and 200% font scale. They are not compared against committed goldens.

`scripts/check-conventions.sh` enforces the mechanical CLAUDE.md rules: a `@BatScreenPreview` per Composable file, no dp literals in design-system components, icons only through `BatIcon`, braced `if`/`else`. Formatting settings live in `.editorconfig`.

The pre-commit hook in `.githooks/` runs the same checks. Any Gradle compile or `check` run sets `core.hooksPath` to it through the `installGitHooks` task, so a fresh clone or worktree needs no manual step.

## Shared and Android

```sh
./gradlew :domain:app:testAndroidHostTest :domain:app:iosSimulatorArm64Test \
  :androidApp:assembleDebDebug :shared-umbrella:linkDebugFrameworkIosSimulatorArm64
```

Swap `:domain:app` for the module under test.

## iOS app

```sh
./scripts/ios-build.sh
```

Compiles the Swift code against the `Shared` framework (the Xcode build phase runs Gradle itself) and prints only errors and the build result. It uses the first available iPhone simulator; set `IOS_SIMULATOR_ID` to pick another (`xcrun simctl list devices available`).

Set `IOS_CONFIGURATION` (`Debug`, `Dev`, `Beta`, `Release`) to build another environment; see [`BUILD_VARIANTS.md`](BUILD_VARIANTS.md).
