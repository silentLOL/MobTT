---
status: resolved
blocked_by: []
---

# 01: Team links move from Fixtures to the standings

## What to build

On the League screen, every row of the standings table opens that Team's screen, except the row of the Team the user came from. Fixture rows show Team names as plain text and keep their info button and highlight. The League detail destination separates the highlighted Team from the Team the user came from, so the League tab (ticket 03) can highlight My Player's Team while keeping its row a link. Android and iOS.

## Acceptance criteria

- [x] Tapping a standings row opens that Team, on Android and iOS
- [x] The row of the Team the user came from is not a link; it stays highlighted with its badge
- [x] A League opened with a highlighted Team it did not come from keeps that row tappable
- [x] Fixture rows have no Team links; the info button still opens Fixture details and the highlight stays
- [x] Standings rows read as one button with their full content to TalkBack and VoiceOver
- [x] `detail_opened` for Team is still sent from League detail
- [x] ViewModel tests on both platforms cover the links and the non-link row; previews updated
