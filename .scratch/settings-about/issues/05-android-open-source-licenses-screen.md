---
status: resolved
blocked_by: [3]
---

# 05: Android: Open source licenses screen

**What to build:** an "Open source licenses" row in the About section opens a pushed screen listing every library, font and third-party asset the app ships, sorted by name within each group, with the license text on tap. The list is generated at build time, so a new dependency cannot be forgotten. See the spec's Implementation Decisions for the data and the missing-text rule.

**Blocked by:** 03 (the About section)

- [x] Before building on it, check that AboutLibraries works with this project's AGP 9 and Gradle setup; if it does not, record the finding as a Comment and ask a human (Ask a human) before choosing another tool
- [x] The generated list covers the Gradle and KMP dependencies; a hand-written list covers fonts (Inter) and any third-party assets; `docs/` says where to add an entry
- [x] Inter's OFL text is added to the repository and shown with the font; a check fails when a dependency has no license entry
- [x] The licenses destination lives in the settings `:api` module with a `GlobalNavigator` extension; the screen shows the shell's back chevron, and the About row reads to TalkBack as a button, e.g. "Open source licenses"
- [x] The screen groups entries as libraries, fonts and assets, each sorted by name; tapping an entry expands its license text; text wraps and scrolls at large font sizes
- [x] An entry without license text shows the license's name and an official URL, never an empty row; when the list cannot be read the ViewModel logs `Error` with the throwable and the screen shows an empty state
- [x] `licenses` is added to the `screen` values in `docs/ANALYTICS.md` first; the ViewModel sends `screen_view` for it
- [x] Unit tests for the mapping: libraries, fonts and assets together, sorted by name, a missing license text, an empty list, expand and collapse, and the `screen_view`
- [x] Previews cover the list loaded, one entry expanded, empty, and large font and dark mode
- [x] New strings are added in English and German
- [x] New strings pushed to Loco
- [x] Opened in a `deb` build: the list shows, an entry expands, and Back returns to Settings
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: AboutLibraries 15.3.0 (its matrix lists AGP 9, Kotlin 2.4, compile SDK 37, the same as here) works with AGP 9.1.1 and Gradle 9.5.1. The `.android` plugin in `:androidApp` writes the list as the `aboutlibraries` raw resource: 201 libraries, KMP modules and Firebase included, under Apache-2.0, MIT, BSD-3-Clause, OFL-1.1 and ASDKL. The plugin fetches SPDX license texts while it builds. A `keep.xml` stops AGP 9's resource shrinking from dropping it; a minified release build was not run.
- 2026-11-07: Strict mode with an allow list fails the build on any other license (proved by dropping MIT from the list: "not allowed licenses"). It was not shown to fail on a dependency with no license at all, since none of the 201 lacks one. Hand-listed fonts and assets in `licenses/` are not seen by strict mode (an entry with no license still built), so `scripts/check-conventions.sh`, which runs in `check`, now fails on a `licenses/libraries/*.json` that lists none (proved by emptying Inter's, then restoring it). Inter's full OFL text is in `licenses/licenses/OFL-1.1.json`, copied from the Inter project, and shows on the screen. `docs/LICENSES.md` says where to add entries; it is not linked from CLAUDE.md.
- 2026-11-07: The Licenses screen is `LicensesDestination` in the settings `:api` module (entered from a new first row of Settings' About section), `LicensesRoute`/`LicensesScreen` and `LicensesViewModel` in `:impl`. The app module binds `LicensesSource` (`LicensesSourceModule`) because only it sees every dependency. `parseLicenseEntries` uses AboutLibraries' `Libs`, whose Android parser needs `org.json`, so its tests and the ViewModel's run under Robolectric. Entries group as libraries, fonts, assets, sorted by name; the text shows on tap, `<br />` line breaks become newlines; a license without text (the 13 Firebase and Play Services entries under ASDKL) shows its name and `https://developer.android.com/studio/terms.html`; an unreadable list logs `Error` with the throwable and shows the empty state (the log line itself is not asserted).
- 2026-11-07: Checked on the emulator: the list shows, an entry expands to its text, the Fonts group shows Inter with its full OFL text, rotating keeps the screen, the chevron returns to Settings, and at 200% font the text wraps and scrolls. The expanded or collapsed state is set as a `stateDescription` and the click label flips, but the dump does not show it and TalkBack was not run. The Play Services missing-text case was seen in a test and a preview, not on the device.
- 2026-11-07: `licenses` was added to the catalog before code. Strings pushed to Loco (11 new Android assets, then 2 more for the expanded and collapsed states). Ticket 06 can reuse `licenses/` (the Inter entry and the OFL text) and the Kotlin library list.
