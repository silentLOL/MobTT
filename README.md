# BatMate

A Kotlin Multiplatform app for Android and iOS built as a Shared Domain Library with fully native presentation. See [CLAUDE.md](./CLAUDE.md) for the architecture and [docs/adr](./docs/adr/README.md) for the reasoning behind it.

* [/core](./core) and [/domain](./domain) are shared KMP modules (`bat.kmp.library`).
* [/shared-umbrella](./shared-umbrella) produces the `Shared` framework, the only thing Xcode links. SKIE converts Flow and sealed classes to Swift.
* [/android](./android) holds Android-only modules; [/androidApp](./androidApp) is the app shell.
* [/iosApp](./iosApp) is the Xcode project (SwiftUI).

### Running the apps

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

- Android host tests: `./gradlew :domain:app:testAndroidHostTest`
- iOS simulator tests: `./gradlew :domain:app:iosSimulatorArm64Test`
