# Favorites from detail screens

**Status:** ready-for-agent

## Problem Statement

A user can only make something a Favorite from the picker on Home. When they are already looking at a Club or Team they care about, they have to leave, open the picker and search for it again. Favorites should also be clearly safe to keep: they must never store personal data about the people the user follows.

## Solution

The Club and Team screens each show a star toggle. Tapping it adds or removes that item as a Favorite, and the state is reflected immediately and on Home. Favorites keep persisting only as IDs on the device, as they do today. Adding and removing from Home stays as it is.

## User Stories

1. As a user viewing a Club, I want to mark it as a Favorite, so that I can reach it quickly from Home.
2. As a user viewing a Team, I want to mark it as a Favorite, so that I can reach it quickly from Home.
3. As a user viewing a Team or Club, I want to find it again on Home without searching, so that I skip the picker.
4. As a user viewing something that is already a Favorite, I want the star to show it is one, so that I do not add it twice or wonder.
5. As a user viewing a Favorite, I want to tap the star to remove it, so that Home stays relevant without leaving the screen.
6. As a user, I want the star state to match Home after I toggle it, so that the two never disagree.
7. As a user, I want my Favorites to survive closing the app, so that I only set them up once.
8. As a user, I want Favorites to stay independent of My Player and My Club, so that changing who I am does not change what I follow.
9. As a user who removes a Favorite by mistake, I want to re-add it with one tap on the same screen, so that a slip costs little.
10. As a screen reader user, I want the star to announce whether the item is a Favorite and what tapping does, so that I can use it without sight.
11. As a user with large text, I want the star to stay tappable and readable at 200% font scale, so that the toggle is not clipped.
12. As a German or English speaking user, I want the star's labels in my language, so that the app feels native.
13. As a privacy-minded user, I want the app to store only identifiers for my Favorites, so that nothing about the people I follow is kept on my device or in a backup beyond what is public.
14. As a user restoring a phone from backup, I want my Favorites to come back, so that I do not rebuild them.
15. As an iOS user and as an Android user, I want the same behaviour on both, so that the app is consistent.

## Implementation Decisions

- Storage does not change. Favorites stay a set of kind-plus-ID entries in DataStore behind the existing Favorites repository, under ADR-0004. SQLDelight was considered and rejected: no query, ordering or per-Favorite field is needed, and no database module exists.
- No domain change. The existing add, remove and observe UseCases already cover Player, Team and Club, and adding the same Favorite twice stays one entry.
- Persisted entries hold IDs only. Names, photos and other personal data are resolved from XTTV at display time and never written into a Favorite. ADR-0004 states this explicitly.
- "Modify" means managing the list through add and remove. Reordering and per-Favorite notes or nicknames are not offered; free text about a person would be personal data.
- Teams stay favouritable, per the glossary.
- The Club and Team screens each gain a star toggle driven by their ViewModel: it observes whether the shown item is a Favorite and sends add or remove through the existing `onAction` entry point. Screens stay stateless. On iOS, Swift ViewModels do the same through the Koin helper.
- Removal needs no confirmation. Re-adding is one tap on the same star.
- Default platform backup behaviour is kept, so IDs restore on a new device.
- Strings are added in German and English on both platforms, with the same accessibility wording.
- Plain string IDs remain subject to ADR-0008: Favorites persistence for XTTV-backed IDs must not ship to `prod` until ID uniqueness across Federations is settled (tracked in the xttv-api feature).

## Testing Decisions

- A good test asserts observable outcomes only: after a toggle, the Favorites observed through the UseCases contain or no longer contain the item, and the star state shown by the ViewModel matches.
- Seam: the UseCases in `:domain:user` against fake repositories, which is the existing seam and needs no new one. ViewModel tests sit one level up and use fake UseCases' repositories in the same way.
- The existing DataStore Favorites tests already cover persistence, deduplication and independence from My Player and My Club, and are left unchanged.
- Prior art: the Home Favorites ViewModel tests on Android and the iOS Home Favorites tests, and the domain Favorites tests.
- Manual check: 200% font scale and TalkBack or VoiceOver on a device.

## Out of Scope

- Reordering Favorites, and nicknames or notes on Favorites.
- Moving Favorites to SQLDelight or any other store.
- Confirmation dialogs for removal.
- Excluding Favorites from device backup.
- Settling XTTV ID uniqueness across Federations (ADR-0008, xttv-api issue 10).
- Changes to Home's picker and row removal.
- A Player toggle: there is no Player detail screen yet, only the Players search and list. It becomes a small follow-up once that screen exists.

## Further Notes

- Vocabulary follows `GLOSSARY.md`: Favorite, Player, Team, Club, My Player, My Club. The glossary needs no change.
- Tickets: 01 Android and 02 iOS, independent of each other. 03 makes iOS Home list Team Favorites, which it left out before this feature.
