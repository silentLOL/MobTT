# Build variants

Both platforms build in four environments. Each installs with its own name and identifier, so all four sit side by side on one device.

| Environment | App name | Android `applicationId` | iOS bundle id |
|---|---|---|---|
| `deb` | `[Debug] Bat Mate` | `at.stefanirndorfer.batmate.deb` | `…batmate.BatMate.deb` |
| `dev` | `[Dev] Bat Mate` | `at.stefanirndorfer.batmate.dev` | `…batmate.BatMate.dev` |
| `beta` | `[Beta] Bat Mate` | `at.stefanirndorfer.batmate.beta` | `…batmate.BatMate.beta` |
| `prod` | `Bat Mate` | `at.stefanirndorfer.batmate` | `…batmate.BatMate` |

The code and module name stays `BatMate`; only the user-facing name has a space.

## Android

`androidApp/build.gradle.kts` declares one flavor dimension, `environment`, with the flavors `deb`, `dev`, `beta` and `prod`. Each sets `applicationIdSuffix` and the `appName` manifest placeholder, which `AndroidManifest.xml` uses as `android:label`.

Flavors multiply with the `debug` and `release` build types. Tasks are named `assemble<Flavor><BuildType>`, for example `:androidApp:assembleDevDebug`. The fake directory is compiled into the `debug` build type only (ADR-0005) and bound only in the `deb` environment, so `debRelease` is disabled; `dev`, `beta` and `prod` read XTTV (ADR-0007).

## iOS

The Xcode project has four configurations, each backed by an `.xcconfig` in `iosApp/Configuration/` that includes `Config.xcconfig`:

| Environment | Configuration | `.xcconfig` | Kotlin framework |
|---|---|---|---|
| `deb` | `Debug` | `Deb.xcconfig` | debug |
| `dev` | `Dev` | `Dev.xcconfig` | debug |
| `beta` | `Beta` | `Beta.xcconfig` | release |
| `prod` | `Release` | `Prod.xcconfig` | release |

Each file sets `APP_NAME` (written to `CFBundleDisplayName`), `BUNDLE_ID_SUFFIX` (appended to the bundle id) and `KOTLIN_FRAMEWORK_BUILD_TYPE`. The last is needed because Kotlin derives the framework build type from the configuration name, which only works for `Debug` and `Release`.

`Debug` and `Release` keep their Xcode names so the default Run and Archive actions still work.

## Environment in shared code

Shared code reads the environment only through `AppConfig` (`:core:network`), injected by Koin. Each app builds it at launch and passes it to `initKoin` (rationale in [ADR-0006](adr/0006-app-config-from-platform-build-environment.md)):

- Android: the flavor's `buildConfigField` `ENVIRONMENT` (`deb`, `dev`, `beta`, `prod`). The app version (`versionName`) goes in as `AppConfig.appVersion`, which becomes the `User-Agent` XTTV sees.
- iOS: `ENVIRONMENT` in the `.xcconfig`, exposed as the `BatEnvironment` Info.plist key and read in `iOSApp`. The app version is `CFBundleShortVersionString`.

An unknown key stops the app at launch.
