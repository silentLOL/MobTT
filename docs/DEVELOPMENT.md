# Development checks

Run the multi-target Gradle commands in the background; a cold run exceeds two minutes.

## Static checks

```sh
./gradlew check            # ktlint on every module plus scripts/check-conventions.sh
./gradlew ktlintFormat    # auto-fix formatting and import order
```

`scripts/check-conventions.sh` enforces the mechanical CLAUDE.md rules: a `@BatScreenPreview` per Composable file, no dp literals in design-system components, icons only through `BatIcon`, braced `if`/`else`. Formatting settings live in `.editorconfig`.

The pre-commit hook in `.githooks/` runs the same checks. Any Gradle compile or `check` run sets `core.hooksPath` to it through the `installGitHooks` task, so a fresh clone or worktree needs no manual step.

## Shared and Android

```sh
./gradlew :domain:app:testAndroidHostTest :domain:app:iosSimulatorArm64Test \
  :androidApp:assembleDebug :shared-umbrella:linkDebugFrameworkIosSimulatorArm64
```

Swap `:domain:app` for the module under test.

## iOS app

```sh
./scripts/ios-build.sh
```

Compiles the Swift code against the `Shared` framework (the Xcode build phase runs Gradle itself) and prints only errors and the build result. It uses the first available iPhone simulator; set `IOS_SIMULATOR_ID` to pick another (`xcrun simctl list devices available`).
