---
status: ready-for-agent
blocked_by: [2, 4, 5]
---

# 06: iOS: Open source licenses screen

**What to build:** the same "Open source licenses" row and pushed screen on iOS. The list shows the Swift packages (Firebase and its transitive packages), the Kotlin libraries that ship in the shared framework, and the fonts and assets, reusing the list and Inter's OFL text from 05. See the spec's Implementation Decisions.

**Blocked by:** 02 (iOS Settings as a full screen), 04 (the iOS About section), 05 (the Kotlin library list, the fonts and assets list and the catalog value)

- [ ] Before building on it, check that LicensePlist works with this Xcode project and its SwiftPM packages; if it does not, record the finding as a Comment and ask a human (Ask a human) before choosing another approach
- [ ] The generated Swift package list is merged with the Kotlin library list from 05 and the shared fonts and assets list, so the iOS screen is as complete as Android's; a check fails when a package has no license entry
- [ ] The licenses destination is pushed onto the current tab's stack with the system back chevron; the About row reads to VoiceOver as a button
- [ ] The screen groups, sorts, expands and handles a missing license text and an unreadable list exactly as 05 specifies; text scales with Dynamic Type and scrolls
- [ ] The ViewModel sends `screen_view` for `licenses`
- [ ] Swift tests for the mapping: the same cases as 05
- [ ] Previews cover the list loaded, one entry expanded, empty, and large Dynamic Type and dark mode
- [ ] New `.xcstrings` keys are added in English and German
- [ ] New strings pushed to Loco
- [ ] Opened in the simulator: the list shows, an entry expands, and Back returns to Settings
- [ ] The iOS build passes (docs/DEVELOPMENT.md)
