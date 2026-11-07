---
name: run-mobtt
description: Run, start, build, install, launch, screenshot or drive the MobTT app on the Android emulator and the iOS simulator (macOS). Use to see a change working in the real app, tap through screens on Android, or take screenshots on either platform.
---

# Run MobTT

MobTT is a native Android app (`androidApp/`) and a native iOS app (`iosApp/`) over a shared KMP library. Everything here targets the **`deb`** flavour (debug build), which reads from the fake directory, so no network or XTTV access is needed. Paths are relative to the repo root.

The driver is `.claude/skills/run-mobtt/driver.sh`. It wraps `adb`, `uiautomator` and `simctl`. Run `.claude/skills/run-mobtt/driver.sh help` for the command list.

## Prerequisites (macOS)

Android SDK at `~/Library/Android/sdk` with an AVD (default `Pixel_9a`, override with `AVD=...`), Xcode with an iPhone simulator, `python3`. AVDs live in `~/.android/avd`.

## Android (the agent path: fully drivable)

```sh
D=.claude/skills/run-mobtt/driver.sh
$D android-boot            # starts the emulator if none is running, waits for boot
$D android-install         # ./gradlew :androidApp:installDebDebug
$D android-launch          # force-stop and start at.stefanirndorfer.mobtt.deb
$D android-ui              # text | content-desc | bounds of everything on screen
$D android-tap "Open Settings"     # taps the first element whose text or description contains this
$D android-swipe-up        # scroll down one screen
$D android-ss /path/to/shot.png    # screenshot; then Read the PNG
```

`android-tap` matches by substring on text or content description, so use the full label for anything ambiguous (`"Add UTTC Oberwart to Favorites"`, not `"Club"`). Prefer it over raw coordinates; run `android-ui` first to see what is there.

Verified flow for Favorites: `android-tap "Open Settings"` → `android-tap "Choose" 2` (My Club is the second Choose; typing in the search field needs `adb shell input tap` on it, then `adb shell input text TTC`) → pick a result → `android-tap "UTTC Oberwart"` → `android-tap "Done"`, then `android-ui` to check you are on Home, → `android-tap "Club"` (the tab) → `android-tap "Add UTTC Oberwart to Favorites"`. Steps up to "Done" and the star toggle were each run through the driver; the one run of `android-tap "Club" 2` after "Done" left the app on the Android launcher and was not diagnosed, so check with `android-ui` between steps. The label flips to "Remove … from Favorites". Back on Home, scroll with `android-swipe-up` until the Favorites section shows; a favourited Team row is tappable and opens its Team screen.

## iOS (build, launch, screenshot only)

```sh
D=.claude/skills/run-mobtt/driver.sh
$D ios-build               # scripts/ios-build.sh on the first iPhone simulator
$D ios-launch              # boot, install Debug MobTT.app, launch at.stefanirndorfer.mobtt.deb
$D ios-ss /path/to/shot.png
```

Pin a simulator with `IOS_SIMULATOR_ID=<udid>` (`xcrun simctl list devices available`). **iOS cannot be tapped from here**: `simctl` has no tap command and no `idb`/`axe`/`cliclick` is installed, so UI behaviour on iOS is covered by `./scripts/ios-test.sh` and the `#Preview` blocks, not by driving the app.

## Gotchas

- **The Gradle build fails with `The given file '…/KMP/BatMate/…' is located outside the root directory`** after the project folder was renamed. Cached build output holds the old path. Deleting `runtime_library_classes_dir` alone did not fix it. `$D android-install-clean` cleans every Android module that has that folder and installs with `--no-build-cache`; that did.
- **Stale iOS products.** `build/xcode/Build/Products/` also holds `Dev-`, `Beta-` and `Release-iphonesimulator/BatMate.app` from before the rename. The driver uses `Debug-iphonesimulator/MobTT.app`; do not `find … -name '*.app' | head -1`, which picks a stale one.
- **zsh does not word-split an unquoted `$var`.** Passing a list of Gradle tasks in a variable needs `${=var}` (or an array), otherwise Gradle gets one long task name.
- `open -a Simulator` fails here ("Unable to find application named 'Simulator'"). Ignore it: `simctl boot`, `install`, `launch` and `io … screenshot` work without a window.
- `emulator` is not on the PATH; the driver calls it from `~/Library/Android/sdk/emulator/`. It was started with the sandbox override on and not tried sandboxed.
- Android screenshots are 1080x2424; when an image is shown scaled, multiply its coordinates by the factor the viewer states. Prefer `android-tap` text matching over coordinates.
- Several iPhone simulators may already be booted. The driver targets the first *available* iPhone, which is not necessarily one that is booted; set `IOS_SIMULATOR_ID` to pin it.
- Favorites and My Club persist across restarts (DataStore). `adb shell pm clear at.stefanirndorfer.mobtt.deb` resets the Android app to the empty Home.

## Tests

```sh
./gradlew check          # ktlint, convention script, unit tests, preview rendering (background it; see docs/DEVELOPMENT.md)
./scripts/ios-test.sh    # iosAppTests on a simulator
```
