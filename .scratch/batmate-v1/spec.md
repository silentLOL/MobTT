---
---

# MobTT v1: follow Clubs, Teams, Players and Fixtures

Scope note: the parts that depend on the undocumented XTTV XML v1 API are listed under "Blocked on API" in Further Notes and are `needs-info` until the docs or sample responses are provided. Everything else is ready to build.

## Problem Statement

Austrian table tennis Players and fans have no pleasant native mobile way to follow their own results. The data lives in XTTV, a web system that is awkward on a phone. A Player wants to see their own rating and form, when their Team plays next and what the League table looks like. A fan wants to follow a Club or a Fixture as it is played. Today they have to navigate several web pages for each of those.

## Solution

MobTT is a native Android and iOS app that reads XTTV data and presents it in four areas: Home, Club, Players and Live. The user picks **My Player** once and the app derives their Club and Association from it. Users with no Player choose **My Club** instead. They can bookmark Players, Teams and Clubs as **Favorites** for quick access from Home. There is no account or login: everything the user chooses is stored on the device. The app is available in German and English.

## User Stories

1. As a Player, I want to choose myself as My Player, so that the app shows my own rating, form and next Fixture on Home.
2. As a Player, I want my Club and Association derived from My Player, so that I never have to enter them separately or enter contradictory values.
3. As a fan with no Player to follow, I want to choose My Club, so that I still get a personalised Home.
4. As a user, I want to change My Player or My Club later, so that I can follow someone else or correct a mistake.
5. As a user, I want to search Players by name or Club, so that I can find My Player or someone I want to follow.
6. As a user, I want a search with no matches to say so, so that I know the search worked and found nothing.
7. As a user, I want to open a Player profile, so that I can see their rating, rank, singles record and recent form.
8. As a user, I want a Player's recent form shown as wins and losses, so that I can judge it at a glance.
9. As a screen reader user, I want each form result announced as a win or a loss, so that the form is not conveyed by colour alone.
10. As a user, I want to open a Club, so that I can see its Teams and each Team's League, standing and next Fixture.
11. As a user, I want to see a Team's lineup, so that I know who plays for it.
12. As a user, I want to see a Team's standing in its League, so that I know how the season is going.
13. As a user, I want to see upcoming Fixtures for My Player's Teams, with date, time, opponent and venue, so that I know when and where to play.
14. As a user, I want a Team with no Fixture to read as a free week, so that a blank next Fixture is not mistaken for an error.
15. As a user, I want to favorite a Player, Team or Club, so that I can reach it quickly from Home.
16. As a user, I want to remove a Favorite, so that Home stays relevant.
17. As a user, I want my Favorites to survive closing the app, so that I only set them up once.
18. As a user, I want to see Fixtures in progress on the Live tab, so that I can follow a Fixture as it is played.
19. As a user, I want a Live Fixture to show its score (Matches won) and the Sets of the current Match, so that I understand how it stands.
20. As a user, I want finished Fixtures clearly separated from Fixtures in progress, so that I don't mistake an old result for a live one.
21. As a user, I want to refresh the Live data on demand, so that I can get the latest score.
22. As a user, I want the Live tab to show a live indicator, so that I know at a glance when something is in progress.
23. As a user, I want a friendly message when I am offline or XTTV cannot be reached, so that I know why the data is missing.
24. As a user, I want to retry after a failure, so that I can recover without restarting the app.
25. As a user, I want to switch between light and dark appearance, or follow the system, so that the app suits my surroundings.
26. As a user, I want the app in German or English following my device language, so that I can read it comfortably.
27. As a user with large system text, I want the layout to survive 200% font scaling, so that I can still use every screen.
28. As a screen reader user, I want every interactive element labelled with at least a 48dp (Android) or 44pt (iOS) target, so that I can navigate by touch or by assistive technology.
29. As a user on a tablet or narrow phone, I want the layout to adapt, so that it never breaks or stretches unreadably.
30. As an iOS user, I want the app to feel like an iOS app, and as an Android user like an Android app, so that it behaves as I expect on my device.
31. As a developer, I want each platform's design tokens to share names, so that a change to a token lands the same way on both.

## Implementation Decisions

- **Architecture:** follows ADR-0002. Domain, repositories and infrastructure are shared Kotlin. ViewModels, navigation and UI are native on each platform. No shared ViewModels and no UI in `domain/` or `core/`.
- **Domain vocabulary:** all code uses the glossary terms: Association, Club, Team, League, Player, Fixture, Match, Set, My Player, My Club, Favorite. A Player has exactly one Club. A Fixture's score counts Matches won, and a Match's score counts Sets won.
- **Domain modules (shared KMP, `bat.kmp.library`):** granular modules per area, such as Player, Club and Team, Fixture, and a user-preferences domain holding My Player, My Club and Favorites. UseCases return `Flow<T>` or are `suspend`. Repository interfaces live in separate files from their implementations.
- **My Player and My Club:** exactly one of the two is active. The Club and Association shown to the user are always derived, never stored independently. The prototype's free-text club field and manual association dropdown are dropped.
- **Favorites:** a Favorite is a Player, Team or Club. It is independent of My Player and My Club.
- **No authentication in v1.** The token and secure-storage machinery described in CLAUDE.md §14 is added only when the API needs it, and that decision is recorded in an ADR at that point.
- **Persistence:** My Player, My Club and Favorites are stored locally with DataStore (`:core:preferences`). Remote data is not stored in v1.
- **Data access:** online only. Repositories are interfaces, so a SQLDelight cache can be added later without changing UseCases. Network access goes through `:core:network` (Ktor) with the base URL supplied by `AppConfig`, never hardcoded in features.
- **Navigation:** Home, Club, Players and Live are the four top-level destinations. Settings (choosing My Player or My Club) is a bottom sheet. The prototype's "XTTV API" tab is not part of the app.
- **Android:** `:android:feature:{name}` modules split into `:api` and `:impl`, with Koin modules registered through `:core:di`. UI is built from `:android:core:design-system`.
- **iOS:** Swift ViewModels and SwiftUI views in `iosApp`, using the shared framework from `shared-umbrella` with SKIE.
- **Design system:** Android tokens and components already exist. The iOS equivalents (colour sets, Swift accessors, shadow and background modifiers, matching components) must be built with the same token names and the same contrast-corrected values (ADR-0003).
- **Localization:** German and English from the first screen, using `{module}_{context}_{type}` string keys. Inter is bundled through `:android:core:localization` on Android.
- **Naming:** the user-facing name is "MobTT"; the package prefix `at.stefanirndorfer.batmate` remains the code name.
- **Workflow:** work lands on a feature branch off `develop` and goes through a pull request.

## Testing Decisions

- **What a good test looks like:** it exercises external behaviour through a public seam and asserts on outcomes (what the user would see or what the UseCase returns), never on implementation details such as which repository method was called.
- **Primary seam: UseCases in `:domain:*`.** Tests live in `commonTest` and run against fake Repository implementations. They cover deriving Club and Association from My Player, My Club as the fallback, Favorites add, remove and persistence behaviour, Fixture and Match score semantics, and the split between live and finished Fixtures.
- **Second seam: the XTTV repository implementation against a Ktor `MockEngine`.** It parses captured sample responses into domain models and maps HTTP and parse failures to domain errors. It is the only place the external contract is tested.
- **No new presentation seam in v1.** Screens are checked through `@BatScreenPreview` (Android) and `#Preview` (iOS), covering loading, loaded, empty and error states. ViewModel tests are added per platform only where a ViewModel contains real logic.
- **Prior art:** `domain/app/src/commonTest` holds the one `kotlin-test` example (`GetPlatformNameUseCaseTest`, run with `runTest`); there is no other test code yet, so these tests set the pattern.

## Out of Scope

- Login, accounts, or any server-side user data.
- Offline-first storage and a local database (SQLDelight).
- Push notifications for scores or Fixtures.
- Editing or submitting any data to XTTV: the app is read-only.
- Match reporting for referees or captains.
- The prototype's "XTTV API" developer-documentation tab.
- Avatar photos (initials only until an image-loading decision is made).
- A shared UI layer: UI stays native per ADR-0002.

## Further Notes

**Blocked on API (`needs-info`):** the XTTV XML v1 endpoints are undocumented and cannot be discovered by probing (the root path returns 403, guessed endpoint names return 404). Until the docs or sample responses are provided, these are unresolved and must not be assumed from the prototype's mock data:

- Endpoint URLs, parameters, response schemas and any API key.
- Whether in-progress Fixtures expose Set scores, or only final results (this decides what the Live tab can show).
- The rating's real name (the prototype uses both "QTTR" and "PTR") and whether an ÖTTV rank exists.
- What a Fixture contains: lineups, doubles pairs, venue and table numbers.
- Cross-origin and rate-limit behaviour is not a concern for native clients, but any usage terms of XTTV need confirming.

**Ready now (no API needed):** domain models and UseCases against fake repositories, My Player, My Club and Favorites with DataStore, the iOS design system, localization and Inter bundling, and Settings and navigation scaffolding.

**Known prototype quirks not to carry over:** the hardcoded "Michael Huber" player and the three independent association, club and player settings that can contradict each other.
