---
status: resolved
blocked_by: [2, 4, 5]
---

# 06: iOS: Open source licenses screen

**What to build:** the same "Open source licenses" row and pushed screen on iOS. The list shows the Swift packages (Firebase and its transitive packages), the Kotlin libraries that ship in the shared framework, and the fonts and assets, reusing the list and Inter's OFL text from 05. See the spec's Implementation Decisions.

**Blocked by:** 02 (iOS Settings as a full screen), 04 (the iOS About section), 05 (the Kotlin library list, the fonts and assets list and the catalog value)

- [x] Before building on it, check that LicensePlist works with this Xcode project and its SwiftPM packages; if it does not, record the finding as a Comment and ask a human (Ask a human) before choosing another approach
- [x] The generated Swift package list is merged with the Kotlin library list from 05 and the shared fonts and assets list, so the iOS screen is as complete as Android's; a check fails when a package has no license entry
- [x] The licenses destination is pushed onto the current tab's stack with the system back chevron; the About row reads to VoiceOver as a button
- [x] The screen groups, sorts, expands and handles a missing license text and an unreadable list exactly as 05 specifies; text scales with Dynamic Type and scrolls
- [x] The ViewModel sends `screen_view` for `licenses`
- [x] Swift tests for the mapping: the same cases as 05
- [x] Previews cover the list loaded, one entry expanded, empty, and large Dynamic Type and dark mode
- [x] New `.xcstrings` keys are added in English and German
- [x] New strings pushed to Loco
- [x] Opened in the simulator: the list shows, an entry expands, and Back returns to Settings
- [x] The iOS build passes (docs/DEVELOPMENT.md)

## Comments

- 2026-11-08: LicensePlist 3.28.3 (brew) works with this project: from `Package.resolved` and the local checkouts in `build/xcode/SourcePackages` it read all 13 Swift packages (Firebase and its transitives) offline, with no missing license. It gives the license text but not its name, so `scripts/generate-ios-licenses.py` reads Apache, MIT, BSD and zlib off the text.
- 2026-11-08: AboutLibraries' KMP plugin on `:shared-umbrella` exports 97 libraries plus Inter (via `licenses/`), but its `exportLibraryDefinitionsIosArm64` is empty: it cannot resolve the iOS klib graph. The iOS list is therefore the shared modules' dependencies as Android resolves them, with some Android-only artifacts (AndroidX, OkHttp) in it: a superset, license-safe but inexact. Recorded in `docs/LICENSES.md`.
- 2026-11-08: The iOS list is a committed `iosApp/iosApp/Resources/licenses.json` (111 entries) made by a hand-run script, not by the build, so a new Kotlin library or a version bump does not fail anything. `scripts/check-conventions.sh` (in `./gradlew check`) fails only when a `Package.resolved` package has no entry (proved by renaming nanopb's entry). Regenerating is in `docs/LICENSES.md`.
- 2026-11-08: Settings' About section starts with the licenses row (action `licensesClicked`, event `openLicenses`); the shell pushes `LicensesDestination` on the selected tab's stack. It registers its own id in `openSettingsIds`, so the gear stays hidden and the consent prompt waits over the licenses too. Whether the gear or prompt flickers on push or pop (the new screen's appear must land before the old one's disappear) was not observed.
- 2026-11-08: iOS has no logger; the unreadable-list case logs with `os.Logger` at error, error text public. The `<br />` to newline step is done by the script, so it has no Swift test. Strings share Loco assets with Android (same keys and text), so the push reported nothing to update; the expanded and collapsed states reuse `core_section_expanded_cd` and `core_section_collapsed_cd`.
- 2026-11-08: Review fixes: parser moved to `Formatting/`, preview data to `Preview/`, a row preview, `LicenseKind` decoded directly. Not done: the Settings open-id bookkeeping is still per-view. 209 Swift tests, the iOS build and `./gradlew check` pass. Simulator check and VoiceOver are open for the user.
- 2026-11-09: Driven in the iPhone 18 Pro simulator (`deb` build): Settings, About, Open source licenses pushed with the chevron; the gear stayed hidden in every frame I took, including during the push and the pop. An entry (abseil) expanded to its Apache text, and Back returned to Settings. The license texts keep the source files' own hard line breaks, so some lines wrap raggedly. The Libraries group starts with Android-only artifacts (Activity, Android Arch), the known superset gap. VoiceOver was not run.
