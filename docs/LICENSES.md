# Open source licenses

Settings → About → "Open source licenses" lists every library, font and asset the app ships, with its license text. Most licenses (Apache 2.0, MIT, BSD, OFL) require the notice and license text to travel with the app, so the list is generated, not written by hand.

## Where the list comes from

- **Libraries (Android).** The AboutLibraries Gradle plugin in `androidApp/build.gradle.kts` collects every dependency of the app, KMP modules included, and writes the list as the `aboutlibraries` raw resource. The plugin fetches SPDX license texts while it builds.
- **Fonts and other assets.** Anything without a dependency manifest is listed by hand in `licenses/`: one `libraries/<name>.json` per entry and one `licenses/<id>.json` per license text, in AboutLibraries' format (see `licenses/libraries/inter.json`). Give the entry a `uniqueId` starting `font:` or `asset:`; the screen groups by that prefix. Inter's OFL text lives here.
- **The screen** (`:android:feature:settings:impl`) reads the list through `LicensesSource`, which the app module binds, and sorts it into libraries, fonts and assets by name.

## When a dependency or asset is added

1. A new library needs nothing: the next build includes it.
2. A new license (anything but Apache-2.0, MIT, BSD-3-Clause, OFL-1.1 and ASDKL) fails the build with "not allowed licenses". Read it, then add its id to `allowedLicenses` in `androidApp/build.gradle.kts` if the app may ship under it. A dependency with no license fails the same way.
3. A new font, icon set or other asset: add a `licenses/libraries/*.json` entry, and a `licenses/licenses/*.json` text if the license is new.

The checks run in the build (`./gradlew :androidApp:assembleDebDebug`, or any task that needs the list) and in `./gradlew check`: the plugin's strict mode for the generated list, and `scripts/check-conventions.sh` for the hand-written entries, which fails on one that lists no license.

## Known gaps

- The Android SDK license (ASDKL, used by Firebase and Play Services) ships no text. The screen shows its name and `https://developer.android.com/studio/terms.html` instead.
- A dependency's own `NOTICE` file, which Apache 2.0 asks to be carried along, is not collected. Check dependencies that ship one before release.
- The iOS app lists its own packages through a separate step (the iOS licenses ticket).
