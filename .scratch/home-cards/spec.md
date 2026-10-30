---
---

# Home cards open their detail screens

## Problem Statement

Home shows the user's hero card (My Player or My Club) and a list of Favorites, but the cards do not lead anywhere consistently: the hero card is not tappable, Player Favorites are not tappable, and no Player detail screen exists. The user cannot get from "the thing I follow" to more about it.

## Solution

Every card on Home is tappable. A Club card (hero or Favorite) opens the Club screen; a Player card (hero or Favorite) opens a new Player detail screen. Both open with a back chevron at the top left. The hero card stays visually prominent and there is only ever one; the Favorites below stay persistent, on white cards. Player detail content is a placeholder, refined later.

## User Stories

1. As a user who picked a Player, I want my hero card to open that Player's detail, so that I can see more about myself.
2. As a user who picked a Club, I want my hero card to open that Club, so that I can see its Teams and Fixtures.
3. As a user, I want a Player Favorite to open that Player's detail, so that I reach it without searching.
4. As a user, I want a Club Favorite to open that Club, so that I reach it without searching.
5. As a user, I want a Team Favorite to keep opening its Team, so that nothing I already rely on regresses.
6. As a user, I want a back chevron at the top left of Player detail and of the Club screen, so that I return to Home in one tap.
7. As a user, I want only one hero card, so that the most important thing stays obvious.
8. As a user whose hero is also a Favorite, I do not want to see it twice, so that Home stays uncluttered.
9. As a user who changes my hero, I want the previously hidden Favorite to reappear in the list, so that I never silently lose a Favorite.
10. As a user, I want my hero and Favorites to survive closing the app, so that I set them up once.
11. As a user viewing a Player, I want to see their name, Club and Association, so that I know I opened the right person.
12. As a user viewing a Player, I want a Favorite star, so that I can add or remove them like a Club or Team.
13. As a user who opens a Player that can no longer be resolved, I want a clear unavailable message instead of a crash or blank screen, so that I understand what happened.
14. As a user with no hero chosen, I want the existing choose prompt to stay, so that first-run Home does not change.
15. As a screen reader user, I want each card announced as a button with its name and kind (Player, Club, Team), so that I can navigate Home without sight.
16. As a user with large text, I want cards to stay readable and tappable at 200% font scale and at least a 48dp / 44pt touch target, so that nothing is clipped.
17. As a German or English speaking user, I want all new labels in my language, so that the app feels native.
18. As an Android user and as an iOS user, I want the same behaviour on both, so that the app is consistent.

## Implementation Decisions

- No new domain term. "Hero card" is a presentation term for My Player / My Club; Favorites stay independent of them per the glossary.
- Persistence is unchanged (ADR-0004: DataStore, IDs only).
- Team Favorites stay and keep their existing tap-through to Team detail.
- "ClubDetailScreen" is the existing Club screen, reused as is; only its back chevron is verified when opened from Home.
- Player detail placeholder: name, Club, Association, Favorite star, back chevron. Navigation is a push on Home's stack, like Club and Team. On Android its destination lives in the Players feature's `:api` module and its screen in `:impl`; Home depends only on `:api`.
- The shared domain gains a use case that resolves one Player to name, Club and Association, absent when unresolvable, and a Home Favorites stream that omits the Favorite equal to the current My Player or My Club. Both are registered in `:core:di` and reachable from the iOS Koin helper. The stored Favorite is never touched; hiding is a read-time filter, so both platforms agree.
- `PlayerRepository.getPlayer` only resolves Players the user picked (`keepPicked`); Player detail resolves through it and shows an unavailable state for an unresolvable ID.
- Tapping goes through `onAction` and a one-off navigation event on Android; screens take navigation as function parameters. iOS Swift ViewModels follow the same shape.

## Testing Decisions

A good test exercises external behaviour through the highest existing seam (inputs in, observable output out) and does not assert on internal calls, view structure or private state.

Seams:

1. **Shared use-case seam** (`commonTest`, fake repositories): Player resolution including the unresolvable case; the Home Favorites stream hiding the hero Favorite and restoring it when My Player or My Club changes. Prior art: the existing use-case tests for resolved Favorites and upcoming Fixtures.
2. **Android Home ViewModel seam** (`HomeViewModelTest`): a hero tap or Player-row tap through `onAction` emits the right navigation event (Club hero opens the Club, Player hero opens the Player, Player row opens the Player); `HomeUiState` Favorites omit the hero.
3. **iOS Home ViewModel seam** (`HomeViewModelFavoritesTests`, `HomeViewModelSelectionTests`): the same assertions.
4. **Android Player detail ViewModel seam**: loaded state, unavailable state, Favorite star toggle. Prior art: the Club and Team detail ViewModel tests.

Not tested automatically: Composable and SwiftUI rendering (covered by previews), and the back chevron (navigation-container behaviour, checked manually).

## Out of Scope

Refined content of Player detail; visual restyling of the cards; changes to the picker; reordering Favorites.

## Further Notes

Manual checks outstanding after implementation: TalkBack and 200% font scale on Android, VoiceOver and 200% Dynamic Type on iOS, and the back chevron on Player detail and the Club screen from Home.

Work is split into six vertical slices in `issues/`. 01 and 02 can start immediately; 03 follows 01, 04 follows 01, 05 follows 04, 06 follows 02.
