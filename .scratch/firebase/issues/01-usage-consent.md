---
status: ready-for-agent
blocked_by: []
---

# 01: Usage Consent

**What to build:** The Usage Consent state and its UI on both platforms. It collects nothing yet. See ADR-0009 and the Usage Consent term in GLOSSARY.md.

- [ ] A domain model and preference hold unanswered, allowed or declined, persisted via `:core:preferences`, with unit tests.
- [ ] A one-time consent step follows the first-run Player or Club choice on Android and iOS, with Allow and Not now.
- [ ] Existing installs that finished first run see the step once on the next launch; "Not now" is never asked again.
- [ ] Settings has a toggle on both platforms, in line with accessibility and localization standards (strings via Loco, `.xcstrings`).
- [ ] `./gradlew check` and the iOS build pass.
