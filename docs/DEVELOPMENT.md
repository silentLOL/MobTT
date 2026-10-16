# Development checks

Run the multi-target Gradle commands in the background; a cold run exceeds two minutes.

## Static checks

```sh
./gradlew check            # ktlint on every module plus scripts/check-conventions.sh
./gradlew ktlintFormat    # auto-fix formatting and import order
```

`scripts/check-conventions.sh` enforces the mechanical CLAUDE.md rules: a `@BatScreenPreview` per Composable file, no dp literals in design-system components, icons only through `BatIcon`, braced `if`/`else`. Formatting settings live in `.editorconfig`.

Install the pre-commit hook once per clone: `git config core.hooksPath .githooks`.

## Shared and Android

```sh
./gradlew :domain:app:testAndroidHostTest :domain:app:iosSimulatorArm64Test \
  :androidApp:assembleDebug :shared-umbrella:linkDebugFrameworkIosSimulatorArm64
```

Swap `:domain:app` for the module under test.

## iOS app

Compiles the Swift code against the `Shared` framework. The build phase runs Gradle itself.

```sh
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination 'id=<simulator-udid>' -derivedDataPath build/xcode \
  CODE_SIGNING_ALLOWED=NO build 2>&1 | grep -E "error:|\*\* BUILD"
```

Find a simulator id with `xcrun simctl list devices available`. A `platform=iOS Simulator,name=...` destination fails here because Xcode resolves it to My Mac.
