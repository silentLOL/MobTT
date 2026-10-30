# Build variants

Both platforms build in four environments. Each installs with its own name and identifier, so all four sit side by side on one device.

| Environment | App name | Android `applicationId` | iOS bundle id |
|---|---|---|---|
| `deb` | `[Debug] MobTT` | `at.stefanirndorfer.batmate.deb` | `…batmate.MobTT.deb` |
| `dev` | `[Dev] MobTT` | `at.stefanirndorfer.batmate.dev` | `…batmate.MobTT.dev` |
| `beta` | `[Beta] MobTT` | `at.stefanirndorfer.batmate.beta` | `…batmate.MobTT.beta` |
| `prod` | `MobTT` | `at.stefanirndorfer.batmate` | `…batmate.MobTT` |

The app is named `MobTT`; the package, application ID and bundle ID keep the original `at.stefanirndorfer.batmate` prefix.

## Android

`androidApp/build.gradle.kts` declares one flavor dimension, `environment`, with the flavors `deb`, `dev`, `beta` and `prod`. Each sets `applicationIdSuffix` and the `appName` manifest placeholder, which `AndroidManifest.xml` uses as `android:label`.

Flavors multiply with the `debug` and `release` build types. Tasks are named `assemble<Flavor><BuildType>`, for example `:androidApp:assembleDevDebug`. Every environment reads XTTV (ADR-0007). The fake directory is compiled into the `debug` build type only (ADR-0005) and still bound by the apps in debug builds, but XTTV is bound after it and wins; the fake only fills what XTTV does not serve yet. `debRelease` stays disabled. Every environment except `prod` logs requests (the `apikey` is redacted).

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

## XTTV API key

The key is a secret and never enters git. It reaches shared code as `AppConfig.xttvApiKey` (empty when unset, redacted in `toString`):

- Android: `xttv.apiKey=…` in the untracked `local.properties`, or the `XTTV_API_KEY` environment variable (CI). `androidApp/build.gradle.kts` turns it into `BuildConfig.XTTV_API_KEY`.
- iOS: `XTTV_API_KEY=…` in `iosApp/Configuration/Secrets.xcconfig` (untracked; copy `Secrets.xcconfig.example`), included by `Config.xcconfig` and exposed as the `BatXttvApiKey` Info.plist key. CI must write that file.
