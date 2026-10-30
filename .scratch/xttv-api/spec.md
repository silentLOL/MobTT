---
---

# Spec: Real data from the XTTV API

## Problem Statement

MobTT only shows made-up data. Release builds bind no repositories at all, and debug builds serve a fake directory of Players, Clubs, Associations, Teams, Leagues and Fixtures. A player or fan cannot find their real Club, see their real Team's League table or learn when the next real Fixture is. XTTV, the source of Austrian table tennis results, publishes an XML API, but its public page names only the methods. It gives no base URL, parameters or schemas, and access requires registering by email.

## Solution

Every domain repository gets an XTTV-backed implementation that reads the XML API, so Home, Club and Team detail show real Associations, Clubs, Teams, Fixtures and League standings on both platforms. Debug builds in the `deb` flavor keep the fake directory so previews and offline work stay deterministic. When XTTV is unreachable, the user sees a retryable error rather than a blank or misleading empty screen, and recently loaded data is shown from a short-lived cache when a refresh fails.

## User Stories

1. As a player, I want to search for my Club by name and find the real one, so that I can set it as My Club.
2. As a player, I want my Club's Teams listed from XTTV, so that I can see which squads we field.
3. As a player, I want my Team's League table to show real standings, so that I know where we stand.
4. As a fan, I want upcoming Fixtures for my Favorites on Home to be real, so that I know when and where to show up.
5. As a fan, I want finished Fixtures to show their real score (Matches won), so that I can follow results.
6. As a player, I want the Club screen to show the real next Fixture of my Teams, so that I can plan my week.
7. As a user in any Association, I want the app to work for my regional Association's data, so that it is not limited to one region.
8. As a user, I want My Club's Association to be derived automatically, so that I never have to pick a host.
9. As a user, I want a clear error with a retry when XTTV cannot be reached, so that I do not mistake it for "no data".
10. As a user, I want a Club with no Teams to show an empty state, not an error, so that I can tell the two situations apart.
11. As a user, I want recently loaded data to appear even if a refresh fails, so that the app stays useful on a bad connection.
12. As a user, I want data to load quickly when I revisit a screen, so that I am not waiting on repeated requests.
13. As an XTTV operator, I want the app to identify itself with a descriptive User-Agent, so that I can see who is calling the API.
14. As an XTTV operator, I want identical concurrent requests collapsed into one and parallel requests capped at 2, so that the app does not strain a volunteer-run service.
15. As an XTTV operator, I want the app to cache results locally, so that request volume stays low.
16. As a developer, I want a `deb` build to keep using the fake directory, so that I can work offline with deterministic data.
17. As a developer, I want `dev`, `beta` and `prod` to use XTTV-backed repositories, so that every non-debug build shows real data.
18. As a developer, I want XTTV's words (Federation, Game) confined to the data layer, so that the domain keeps its own language.
19. As a developer, I want the parser to ignore unknown XML elements, so that XTTV adding fields does not break the app.
20. As a developer, I want every mapper tested against recorded XML, so that schema changes show up as test failures, not production bugs.
21. As a developer, I want no test to call the live API, so that tests are fast, deterministic and polite to XTTV.
22. As a developer, I want one place that decides which host serves an Association, so that a single-host answer from XTTV is a one-line change.
23. As an iOS user, I want the same error and empty states as on Android, so that the platforms behave alike.
24. As an iOS developer, I want the data error to arrive as a typed error through SKIE, so that Swift can handle it without string matching (delivered as a `DataResult`, not a thrown error).
25. As a user with Favorites, I want them to keep resolving after the switch to real data, so that I do not lose my bookmarks.
26. As the project owner, I want Favorites persistence on XTTV IDs held back from `prod` until ID uniqueness is confirmed, so that I do not ship a format that needs a live migration.
27. As a player, I want Player search and the Team lineup to arrive once XTTV documents them, so that I do not see guessed data in the meantime.
28. As the project owner, I want real XTTV responses recorded and verified, so that the guessed schemas are replaced by facts.

## Implementation Decisions

- A new shared KMP module, `:core:xttv`, holds the XML DTOs, mappers and XTTV-backed repository implementations. It depends on the `:domain:*` repository interfaces, like the fake directory module does. `:core:network` keeps only the HTTP client factory and `AppConfig` and does not import the domain. See ADR-0007.
- The HTTP client factory sends a descriptive `User-Agent`, collapses identical in-flight requests and caps concurrency at 2.
- Parsing is lenient: unknown elements are ignored.
- Repositories keep an in-memory TTL cache: Clubs and Teams about 1 hour, Fixtures and standings about 5 minutes. A failed refresh serves the stale entry if one exists.
- A failed request throws a `DataUnavailable` exception defined in the domain. Empty results remain an empty list or `null`, so "failed" and "empty" stay distinct. Each ViewModel on both platforms maps it to a retryable error state; on iOS it surfaces as a typed `DataResult.Unavailable` (see `docs/SKIE_BRIDGE.md`; a thrown exception would arrive as an `NSError`).
- Each Association has its own XTTV host, chosen by a single host resolver from the Association. `AppConfig` carries only a default host. This is an assumption until XTTV answers (ADR-0008, Proposed).
- IDs stay as plain strings. If real data shows collisions across Associations, they become `{associationId}:{id}` with a Favorites migration. Favorites persistence on XTTV IDs must not ship to `prod` before that question is answered.
- `deb` builds on Android and iOS bind the fake directory. `dev`, `beta` and `prod` bind the XTTV-backed repositories and all talk to production XTTV, which has no known staging server. This replaces the release `TODO`.
- Vocabulary: XTTV's Federation maps to Association and its Game to Fixture. The domain keeps its terms (see the XTTV terms table in the glossary). Match is added to the domain only when a screen needs it.
- Player search and Team lineup stay on the fake until XTTV documents how it exposes them. `TeamDetail` stays PROVISIONAL until then.
- Access depends on registering with XTTV by email. Until access is granted, all work uses hand-written sample XML and no live calls.

## Testing Decisions

- A good test checks behaviour visible through the public interface: given XTTV's XML, the repository returns the right domain objects, caches, fails and recovers correctly. It does not assert on DTOs, mappers or the cache's internals.
- One seam: the repository interfaces (`ClubRepository`, `TeamRepository`, `LeagueRepository`, `FixtureRepository`, `AssociationRepository`). Tests build the real `:core:xttv` repositories on a Ktor `MockEngine` that serves recorded XML from `commonTest` resources.
- The client's `User-Agent`, de-duplication and concurrency cap are not visible through the repositories. They get a small number of direct tests against the `MockEngine`.
- ViewModel error handling extends the existing ViewModel tests with a `DataUnavailable` case; no new seam is added.
- No test calls the live API.
- Prior art: the existing fake-directory test covers the same interfaces through behaviour using `runTest` and kotlin-test.

## Out of Scope

- Offline support and persistent caching (`:core:database`); revisit if offline use becomes a product goal.
- Match- and Set-level detail (`getGameDetails`).
- Doubles and player standings (`getDoublePlayerStandings`, `getDoubleTeamStandings`, `getSingleStandings`).
- The other XML output formats XTTV lists (e.g. honours, championship overview).
- A staging environment for XTTV.
- Writing data to XTTV; the API is read-only for us.

## Further Notes

- ADR-0007 (module layout, caching, errors, build split) is Accepted. ADR-0008 (host per Association, plain string IDs) is Proposed until XTTV replies.
- The registration email asks the questions that decide ADR-0008: base URLs, authentication, schemas, ID uniqueness, staging, and an acceptable request rate.
- XTTV is non-commercial and volunteer-run, so the usage limits are a hard constraint, not a preference.
