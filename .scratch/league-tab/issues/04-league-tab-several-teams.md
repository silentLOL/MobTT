---
status: resolved
blocked_by: [3]
---

# 04: League tab for several Teams

## What to build

When My Player plays in two or more Teams, the League tab lists one card per Team: the Team screen's League card (League, rank, next Fixture) with an added line naming the Team. Tapping a card opens its League on the tab's own stack with that Team highlighted; back returns to the list. Two Teams in the same League give two cards. Android and iOS.

## Acceptance criteria

- [x] The League card takes an optional Team name line; the Team screen keeps it without
- [x] Two or more Teams show the card list; one Team still opens its League directly
- [x] A card opens its League with the Team highlighted, and back returns to the list
- [x] `detail_opened` for League from the League tab
- [x] Cards read as one button to TalkBack and VoiceOver
- [x] ViewModel tests on both platforms; previews for the list and long names
