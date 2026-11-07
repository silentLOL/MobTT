---
status: ready-for-agent
blocked_by: [3]
---

# 05: Android: Open source licenses screen

**What to build:** an "Open source licenses" row in the About section opens a pushed screen listing every library, font and third-party asset the app ships, sorted by name within each group, with the license text on tap. The list is generated at build time, so a new dependency cannot be forgotten. See the spec's Implementation Decisions for the data and the missing-text rule.

**Blocked by:** 03 (the About section)

- [ ] Before building on it, check that AboutLibraries works with this project's AGP 9 and Gradle setup; if it does not, record the finding as a Comment and ask a human (Ask a human) before choosing another tool
- [ ] The generated list covers the Gradle and KMP dependencies; a hand-written list covers fonts (Inter) and any third-party assets; `docs/` says where to add an entry
- [ ] Inter's OFL text is added to the repository and shown with the font; a check fails when a dependency has no license entry
- [ ] The licenses destination lives in the settings `:api` module with a `GlobalNavigator` extension; the screen shows the shell's back chevron, and the About row reads to TalkBack as a button, e.g. "Open source licenses"
- [ ] The screen groups entries as libraries, fonts and assets, each sorted by name; tapping an entry expands its license text; text wraps and scrolls at large font sizes
- [ ] An entry without license text shows the license's name and an official URL, never an empty row; when the list cannot be read the ViewModel logs `Error` with the throwable and the screen shows an empty state
- [ ] `licenses` is added to the `screen` values in `docs/ANALYTICS.md` first; the ViewModel sends `screen_view` for it
- [ ] Unit tests for the mapping: libraries, fonts and assets together, sorted by name, a missing license text, an empty list, expand and collapse, and the `screen_view`
- [ ] Previews cover the list loaded, one entry expanded, empty, and large font and dark mode
- [ ] New strings are added in English and German
- [ ] New strings pushed to Loco
- [ ] Opened in a `deb` build: the list shows, an entry expands, and Back returns to Settings
- [ ] `./gradlew check` passes
