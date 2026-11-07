---
status: resolved
blocked_by: [4]
---

# 06: Collapsible Fixtures and Player ranking, loaded on demand

## What to build

Requested after the device check (ticket 05). On the League screen, the Fixtures and the Player ranking sections are collapsible and start collapsed; the League table stays open. Each section loads only when the user first opens it, so a League opens on its table alone: the Fixtures are no longer part of the League's first load. A section keeps what it loaded when collapsed again. Android and iOS.

## Acceptance criteria

- [x] The League's first load reads the League and its table only; the Fixtures come from their own shared use case
- [x] Fixtures and Player ranking start collapsed with a header that toggles them, on both platforms
- [x] Opening a section the first time loads it, with loading, empty and unavailable states inside the section; collapsing and reopening does not load again
- [x] Retry on the League reloads only what was already opened
- [x] The section header reads as a heading and a button with its expanded or collapsed state to TalkBack and VoiceOver
- [x] ViewModel and domain tests on both platforms; previews for collapsed and expanded sections
