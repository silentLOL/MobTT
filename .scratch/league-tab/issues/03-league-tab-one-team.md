---
status: resolved
blocked_by: [1]
---

# 03: League tab for one Team, and its empty states

## What to build

A fourth tab, League (Liga), sits between Home and Club with a new trophy icon. A shared `ObserveMyTeamsUseCase` finds My Teams (Teams whose lineup names My Player) and follows changes of My Player. The tab shows loading; an empty state when there is no My Player (My Club only included) with a button opening the Player picker; a no-Team state linking to My Player's screen; an unavailable state with retry. With one Team, the tab opens directly on its League with that Team highlighted and its standings row still a link. Until ticket 04, several Teams show the first Team's League. Android and iOS.

## Acceptance criteria

- [x] Domain tests cover no My Player, My Club only, one Team, two Teams, two Teams in one League, no Team, a Team without League, a Team that fails alone, unavailable, My Player changing
- [x] Tab bar order Home, League, Club, Players on both platforms with `BatIcon.League` / SF Symbol trophy
- [x] Every tab state renders on both platforms, with English and German strings
- [x] The empty state's button opens the Player picker; the no-Team link opens My Player's screen
- [x] With one Team the League screen is the tab's root, with the Team highlighted and the "only my Team's Fixtures" toggle
- [x] `screen_view` for the League tab, recorded in `docs/ANALYTICS.md`
- [x] ViewModel tests on both platforms; previews for every state
