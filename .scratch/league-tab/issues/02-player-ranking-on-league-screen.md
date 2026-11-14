---
status: resolved
blocked_by: [1]
---

# 02: Player ranking on the League screen

## What to build

Below the Fixtures, the League screen shows the Player ranking: the League's Players ordered by rank, unranked Players last, each row with rank, name, Team, matches won and lost and games played. A row opens the Player; My Player's row is highlighted. The section loads on its own, so standings and Fixtures never wait for it, and a failure shows a short message in the section only. Data comes from the same `getSingleStandings` request as the League's Players; Team names come from the League's standings, with no Club lookups. Android and iOS. Blocked by 01 only because both change the same screen.

## Acceptance criteria

- [x] `XttvPlayerRepositoryTest` reads rank, ranked flag, Team, matches won and lost and games played from the recorded sample
- [x] The fake directory serves matching ranking rows
- [x] A shared use case orders the ranking by rank with unranked Players last and joins Team names from the standings; covered by domain tests
- [x] The section shows loading, loaded, empty and unavailable without changing the rest of the screen
- [x] Tapping a row opens the Player on Android and iOS, and sends `detail_opened` for Player
- [x] My Player's row is highlighted with a badge and announced as such
- [x] English and German strings ("Player ranking" / "Einzelrangliste") on both platforms
- [x] ViewModel tests on both platforms; previews for loaded, loading, unavailable, empty, long names
