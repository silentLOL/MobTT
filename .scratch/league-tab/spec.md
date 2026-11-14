---
status: resolved
---

# League tab and League screen links

## Problem Statement

A user who follows My Player has no quick way to their own League. They open Home or the Club, find My Player's Team, open the Team, then tap its League card: three steps for the screen they look at most during a season.

Once on the League screen, the links sit in the wrong place. The standings table, where a fan looks for another Team, is not tappable, while every Fixture row carries two Team links that compete with the row's info button. And the League's Players appear nowhere on it: to see who plays in the League, and how well, the user has to go through the Player picker, which shows names only.

## Solution

The bottom navigation gains a **League** tab, between Home and Club. It shows the League of My Player's Team straight away. When My Player plays in several Teams, the tab lists one card per Team (the same card the Team screen shows: League, rank and next Fixture, plus the Team's name), and each card opens its League. Without My Player the tab explains that My Player must be chosen first and offers a button to choose one.

On the League screen, every row of the standings table opens that Team, while the Fixture rows lose their Team links and keep only the info button. Below the Fixtures, a new **Player ranking** lists the League's Players by rank, with their Team and matches won and lost; each row opens the Player, and My Player is highlighted.

Android and iOS ship together.

## User Stories

### League tab

1. As a fan, I want a League tab in the bottom navigation, so that my League is one tap away
2. As a fan, I want the League tab next to Home, so that the screens I use most sit together
3. As a fan, I want the tab labelled "League" in English and "Liga" in German, so that it reads naturally
4. As a fan, I want the tab to have its own trophy icon, so that I can tell it apart from the Club tab at a glance
5. As a fan who plays in one Team, I want the League tab to open directly on that Team's League, so that I do not pass through a list of one
6. As a fan who plays in one Team, I want my Team highlighted in the League table, so that I find my row at once
7. As a fan who plays in one Team, I want to narrow the Fixtures to my Team's, so that I see only the Fixtures I play in
8. As a fan who plays in two or more Teams, I want one card per Team, so that I can choose which League to look at
9. As a fan who plays in two or more Teams, I want each card to show the League, my Team's rank and its next Fixture, so that I get the overview before I open anything
10. As a fan who plays in two or more Teams, I want each card to name the Team, so that I can tell two Teams in the same League apart
11. As a fan who plays in two Teams in the same League, I want two cards, so that each Team's rank and next Fixture stay visible
12. As a fan, I want tapping a card to open its League with that Team highlighted, so that I land on the right row
13. As a fan who opened a League from the card list, I want back to return me to the list, so that I can check my other League
14. As a fan without My Player, I want the League tab to tell me that My Player must be chosen first, so that I understand why it is empty
15. As a fan without My Player, I want a button on that message that opens the Player picker, so that I can fix it without searching for the setting
16. As a fan who only chose My Club, I want the same message, so that the tab never guesses a League from my Club
17. As a fan, I want the tab to follow a change of My Player at once, so that it never shows the League of a Player I no longer follow
18. As a fan, I want a loading indicator while my Teams are found, so that I know the tab is working when XTTV is slow
19. As a fan whose Player appears in no Team lineup, I want a message saying no Team was found, so that I do not mistake it for an error
20. As a fan whose Player appears in no Team lineup, I want a link to My Player's screen, so that I can check the Player I chose
21. As a fan, I want an error message with a retry button when XTTV cannot answer, so that I can try again without leaving the tab
22. As a fan, I want switching away from the League tab and back to keep my place, so that the tab behaves like the others

### League screen: standings and Fixtures

23. As a fan, I want to tap any Team in the League table, so that I can open that Team's screen
24. As a fan, I want my highlighted Team's row in the table to be tappable from the League tab, so that I can reach my own Team from there
25. As a fan who came to the League from a Team's screen, I want that Team's row not to be a link, so that I do not loop back to the screen I came from
26. As a fan, I want the Fixture rows to show Team names as plain text, so that the info button is the one clear action on each row
27. As a fan, I want the info button on each Fixture to keep opening the Fixture details, so that nothing I rely on disappears
28. As a fan, I want my Team's Fixtures to stay highlighted, so that I can still find them in the full list

### League screen: Player ranking

29. As a fan, I want a Player ranking below the Fixtures, so that I see who plays in the League and how well
30. As a fan, I want the Players ordered by rank, so that I see the League's best Players first
31. As a fan, I want each row to show rank, name, Team, matches won and lost, and games played, so that I can compare Players at a glance
32. As a fan, I want Players below the League's minimum number of games listed after the ranked ones without a rank, so that the ranking is not misleading
33. As a fan, I want a Player listed for two Teams in the same League shown once per Team, so that the numbers match what XTTV publishes
34. As a fan, I want to tap a Player in the ranking, so that I open that Player's screen
35. As a fan, I want My Player's row highlighted, so that I find myself in the ranking at once
36. As a fan, I want the standings and Fixtures shown while the ranking is still loading, so that a slow XTTV answer does not hold back the whole screen
37. As a fan, I want a short message in the ranking section when XTTV cannot answer for it, so that the rest of the League stays usable
38. As a fan, I want a message when the League has no ranked Players yet, so that I am not shown an empty heading
39. As a German-speaking fan, I want "Einzelrangliste" as the section title, so that the screen uses the sport's word

### Accessibility and layout

40. As a screen reader user, I want each standings row and each ranking row to read as one button with its full content, so that I know where it leads
41. As a screen reader user, I want highlighted rows to say they are my Team or My Player, so that I do not rely on colour
42. As a screen reader user, I want the League tab's empty state and its button announced, so that I know what to do next
43. As a fan using large text, I want ranking rows to wrap rather than clip at 200%, so that long names stay readable
44. As a fan, I want long League, Team and Player names to wrap on the cards and rows, so that I can read them in full

### Developers

45. As a developer, I want the rule for which Teams belong to My Player shared by both platforms, so that the tab shows the same Leagues on Android and iOS
46. As a developer, I want the Player ranking joined with the League's Team names in shared code, so that iOS does not repeat it
47. As a developer, I want XTTV's ranking fields covered by the recorded sample, so that a change in XTTV's response shows up in a test
48. As the project owner, I want the new screen views and taps to appear in analytics, so that I can see whether the tab is used

## Implementation Decisions

- **Domain language:** "My Teams" are the Teams whose lineup names My Player. The "Player ranking" is a League's Players ordered by rank (German: Einzelrangliste), read from XTTV's `getSingleStandings`. Both terms are added to the glossary.
- **My Teams use case:** a new shared `ObserveMyTeamsUseCase` in `:domain:user` observes the user context. Without My Player (no selection, or My Club only) it emits a "no My Player" result. With My Player it reuses the lineup rule of `GetPlayerDetailUseCase` and loads each Team through `GetTeamDetailUseCase`, emitting the list of Team details (League, standing, next Fixture). An empty list means no Team was found. A `DataUnavailable` on the Team list surfaces as unavailable; a single Team that cannot be loaded is left out, as on the Player screen. It re-emits when My Player changes. A Team without a League is left out, since it has no League to show.
- **Player ranking data:** `PlayerRepository` gains a method returning a League's ranking rows: the Player, rank (null when `ranked` is 0), Team id, games played, matches won and matches lost. It reads the same `getSingleStandings` request as `getLeaguePlayers`, so the five-minute standings cache from ADR-0007 covers both. A Player appears once per Team in the League, as XTTV lists them. The fake directory supplies matching rows for debug use.
- **Player ranking use case:** a new shared use case in `:domain:user`, beside `ListLeaguePlayersUseCase` for the same dependencies, returns the ranking ordered by rank with unranked Players last (by name), each row carrying its Team's name from the League's standings. A Team missing from the standings leaves the name null rather than dropping the row. No Club lookups are made.
- **Navigation contract (`:android:feature:club:api`):** a new top-level `LeagueTabDestination`. The League detail destination separates the highlighted Team from the Team the user came from: from a Team's screen both are that Team; from the League tab only the highlight is set.
- **League tab (`:android:feature:club:impl`):** a new ViewModel and screen, following the UDF pattern. States: loading, no My Player, no Team found, unavailable, one Team, several Teams. With one Team the tab renders the League detail route as its root. With several it shows the card list; tapping a card navigates to League detail within the tab. The empty state's button opens the same Player picker Home opens; the no-Team state's link opens My Player's screen. The tab is a fourth `TopLevelTab` between Home and Club, so `navigateTopLevel` keeps tabs from stacking.
- **Team card:** `TeamLeagueCardView` gains an optional Team name line. The Team screen keeps using it without the line.
- **League detail (Android):** standings rows become `TeamClicked` targets, except the row of the Team the user came from. Fixture rows drop `onTeamClick` and the Team links; the info button and the highlight stay. A Player ranking section follows the Fixtures, loaded on its own stream so its loading and failure never change the screen's main state. Ranking rows emit a new `PlayerClicked` action and `OpenPlayer` event, which the graph forwards through the Players `:api` entry point. My Player is read from the user context to highlight its row.
- **iOS:** a new `ShellTab.league` between Home and Club with its own navigation path, a League tab ViewModel and screen with the same states, the same League detail changes (standings links, plain Fixture names, ranking section, Player navigation), and the Team card with its Team name line. Swift code reaches the shared use cases through `KoinHelper`, following `docs/SKIE_BRIDGE.md` for errors.
- **Design system:** a new `BatIcon.League` (trophy drawable) on Android and an SF Symbol trophy on iOS. Ranking rows reuse `BatListRow` (leading rank, title name, supporting Team and won:lost with games played, highlight and badge for My Player), striped like the standings.
- **Strings:** tab label, empty state (title, body, button), no-Team state (title, body, link), ranking section title, empty and unavailable messages, row content descriptions and the My Player badge, in English and German on both platforms.
- **Analytics:** a new `AnalyticsScreen` for the League tab with its `screen_view`, and `detail_opened` for League (from the tab), Team and Player (from League detail), recorded in `docs/ANALYTICS.md`. No new event names.

## Testing Decisions

- A good test drives behaviour through the highest existing seam and asserts only what a user could observe: which state the screen is in, which rows appear in which order, which rows are links, which events fire. It does not assert call order or internal helpers.
- **My Teams use case (domain `commonTest`, fake repositories):** no My Player; My Club only; one Team; two Teams; two Teams in the same League; no Team found; a Team without a League; a Team that fails alone; XTTV unavailable; My Player changing.
- **Player ranking use case (domain `commonTest`):** order by rank, unranked Players last, a Player listed for two Teams, a Team missing from the standings.
- **XTTV data:** `XttvPlayerRepositoryTest` reads rank, `ranked`, Team, matches won and lost and games played from the recorded `getSingleStandings` sample through the mock Ktor engine.
- **Android:** `LeagueDetailViewModelTest` gains standings links, the non-link row for the Team the user came from, the ranking loading on its own, the ranking failing alone, Player events and the My Player highlight. A new `LeagueTabViewModelTest` runs the real `ObserveMyTeamsUseCase` over `FakeClubData` and covers every tab state, the Player picker event and a change of My Player.
- **iOS:** `LeagueDetailViewModelTests` gains the same League detail cases; a new `LeagueTabViewModelTests` stubs the shared calls and covers every tab state and its events.
- **Screens:** previews for every League tab state, the card with a Team name, League detail with the ranking (loaded, loading, unavailable, empty, long names), rendered by `check` on Android. The Fixture row change is covered by its previews.
- **Prior art:** the existing League detail and Team detail ViewModel tests, `GetPlayerDetailUseCase`'s tests, and the recorded-XML repository tests in `:core:xttv`.

## Out of Scope

- A League tab for users with My Club only (they get the empty state).
- A main-Team rule or ordering of My Teams beyond the order the Club lists them.
- RC rating or any other per-Player detail on the Player screen (player-detail-teams ticket 01).
- Sorting or filtering the Player ranking.
- Moving League and Fixture screens into their own feature module.
- Making the whole Fixture row tappable.
- Offline use or a persistent cache for My Teams.

## Further Notes

- Finding My Teams costs one lineup request per Club Team plus the Team details, as on the Player screen. Teams stay uncached by ADR-0007, so the tab asks XTTV again each time its ViewModel is created; it is created once per tab visit kept alive.
- `getSingleStandings` carries no points or quota field (recorded 2026-10-31), so the ranking shows matches won and lost and games played only.
- The highlighted-versus-came-from split replaces the current meaning of the League detail destination's highlight, which today means both.
