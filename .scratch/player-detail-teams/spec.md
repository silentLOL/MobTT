---
---

# Player screen: Teams and links

## Problem Statement

A user who opens a Player sees a blue hero card with the Player's name, Club and Association as plain text. They cannot tell which Team of the Club the Player plays in, and they cannot jump from the Player to the Club or to that Team. To find the Team they have to leave, open the Club, and scan every lineup themselves.

## Solution

The Player screen drops the hero card. It shows the Player's name as a plain large title with the Favorite star beside it. Below it, a "Club" row names the Club (the Association as its detail line) and opens the Club screen. A "Teams" section lists every Team of that Club whose lineup names the Player; each row opens its Team screen. When no Team can be found the section is not drawn. The screen shows only what XTTV provably gives us; anything further is parked behind a research ticket.

## User Stories

1. As a fan, I want to see my favourite Player's name as the screen title, so that I know whose page I am on
2. As a fan, I want the Favorite star next to the name, so that I can bookmark the Player without leaving the screen
3. As a fan, I want the Favorite state to be announced to a screen reader, so that I know whether the Player is already a Favorite
4. As a fan, I want to see the Club the Player plays for, so that I know where they belong
5. As a fan, I want to see the Club's Association under the Club name, so that I can place the Club regionally
6. As a fan, I want to tap the Club row, so that I land on the Club screen with its Teams and standings
7. As a fan, I want to see every Team the Player appears in, so that I know where to follow them
8. As a fan, I want a Player who plays in two Teams of the same Club to show both Teams, so that I do not miss one
9. As a fan, I want to tap a Team row, so that I land on that Team's screen with its League table and lineup
10. As a fan, I want the Teams section to disappear when no Team is found, so that I am not shown an empty heading
11. As a fan, I want the screen to still show the Club when XTTV cannot answer for the Teams, so that one failed lookup does not blank the Player
12. As a fan, I want a Team XTTV cannot answer for to be left out rather than shown wrongly, so that every Team listed is real
13. As a fan, I want a Player whose Club no longer resolves to keep their name, so that the page still identifies them
14. As a fan, I want the Club row hidden when the Club does not resolve, so that I am not offered a link that leads nowhere
15. As a fan, I want a loading indicator while the Player loads, so that I know the screen is working
16. As a fan, I want a clear message when the Player cannot be found, so that I understand it is not my fault
17. As a fan, I want a long Player, Club or Team name to wrap instead of being cut off, so that I can read it in full
18. As a fan using large text, I want the rows to grow with the font size, so that nothing is clipped at 200%
19. As a screen reader user, I want the name to be a heading and each row to read as one button, so that I can navigate by structure
20. As a fan, I want the screen to look like the Club and Team screens' neutral rows rather than a blue card, so that the app feels consistent
21. As a German-speaking fan, I want "Verein" and "Mannschaften" as the section titles, so that the screen reads naturally
22. As an English-speaking fan, I want "Club" and "Teams" as the section titles, so that the screen reads naturally
23. As an Android user, I want back to return me from the Club or Team to the Player, so that I keep my place
24. As an iOS user, I want the Club and Team to open on the same navigation stack as the Player, so that the back button works
25. As a user on a debug build, I want Teams from live XTTV data, so that what I test matches production
26. As a developer, I want the Team lookup inside one use case, so that both platforms share the rule
27. As a developer, I want XTTV's lineup reading covered by a recorded sample, so that a change in XTTV's response shows up in a test
28. As the project owner, I want further Player details left to a research ticket, so that we do not invent fields the API may not offer

## Implementation Decisions

- **Domain language:** a Player plays for exactly one Club and may be listed in several of its Teams (recorded in the glossary). A Team a Player "appears in" means the Team's lineup names the Player.
- **Player detail model:** `PlayerDetail` carries the Player, the Club and Association names (null when XTTV no longer resolves them) and a list of Teams, empty by default.
- **Team lookup:** XTTV has no Player-to-Team lookup. The shared `GetPlayerDetailUseCase` asks the repository for the Club's Teams, reads each Team's lineup, and keeps the Teams whose lineup names the Player. A `DataUnavailable` on the Team list yields no Teams; a `DataUnavailable` on a single lineup leaves only that Team out. Neither fails the Player. Cost is one request per Club Team, covered by the existing one-hour cache.
- **Lineup source:** the XTTV Team repository reads the lineup from the players `getTeamDetails` lists. The fake-directory `LineupSource` seam is removed, so debug builds read live lineups too. Lineups are provisional until XTTV documents their shape.
- **Android presentation:** the ViewModel gains `ClubClicked` and `TeamClicked(teamId)` actions and emits `OpenClub` / `OpenTeam` one-off events through a channel. The route forwards them to the Club-detail and Team-detail navigation entry points exposed by the Club `:api` module; the Players `:impl` module depends only on that `:api` module.
- **iOS presentation:** the ViewModel gains `clubClicked` and `teamClicked(id)` actions and an event stream. `PlayerDetailRoute` takes `onOpenClub` and `onOpenTeam`, which the shell wires to the Home navigation path.
- **Design:** no hero card on the Player screen only. The header is a plain title with the star. Club and Team rows are neutral cards with a title and optional detail line and a minimum touch target. Club and Team screens keep their hero cards until a separate restyle is asked for. No chevron icon exists in the design system, so none is drawn.
- **Strings:** new Club and Teams section titles in English and German on both platforms. The Android hero overline string is removed; the iOS one stays as the navigation title.
- **Further details:** `getPlayerDetails` is closed to API access, and `getSingleStandings` rows are mapped only to id, name and Club. Nothing beyond Club, Association and Teams is shown until the research ticket answers what XTTV offers.

## Testing Decisions

- A good test drives behaviour through the highest existing seam and asserts only what a user could observe: which rows appear, which events fire, which state the screen is in. It does not assert call order or internal helpers.
- **Android seam:** `PlayerDetailViewModelTest` runs the real `GetPlayerDetailUseCase` over fake repositories. It covers the resolved Player with Teams, an unresolvable Player, unreachable data, the Favorite toggle, and the Club and Team events. The use case's own tests in the user domain cover several Teams, a Team filtered out by its lineup, and a lineup that fails.
- **iOS seam:** `PlayerDetailViewModelTests` stubs the shared load call and covers the mapping to the UI model, the Favorite toggle, unavailable states and the Club and Team events.
- **XTTV data seam:** `XttvTeamRepositoryTest` reads the lineup from a recorded `getTeamDetails` sample through the mock Ktor engine. It is the one test that notices a change in XTTV's response shape.
- Screens are covered by previews (loaded, favorite, no Teams, no Club, long names, loading, unavailable), which `check` renders on Android. No new seams are added.
- Prior art: the existing Club and Team ViewModel tests and the recorded-XML repository tests in the XTTV module.

## Out of Scope

- Further Player details (ranking, wins and losses, quota); see ticket 01.
- Asking XTTV to open `getPlayerDetails`.
- Removing the hero card from the Club and Team screens.
- A main-Team rule for a Player listed in several Teams.
- Offline use or a persistent cache of lineups.

## Further Notes

- The lookup was verified on Android against live XTTV: the Player Alfred Schwentner (ATSV Lenzing) resolves to the Team Lenzing 1, and tapping it opens the Team screen. The iOS Teams section has not been checked against live data, and 200% font scale and screen readers have not been checked on a device (ticket 02).
- A build-cache entry from an old project folder name can break dexing of core modules; clear the module's build folder and build with `--no-build-cache`.
