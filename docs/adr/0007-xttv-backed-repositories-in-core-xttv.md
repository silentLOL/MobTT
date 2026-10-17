# ADR-0007: XTTV-backed repositories live in `:core:xttv`; the fake directory stays for `deb`

**Status:** Accepted
**Date:** 2026-10-17
**Deciders:** Stefan Irndorfer

## Context
Every repository behind the domain interfaces is served by `:core:fake-directory`, bound only in debug builds ([ADR-0005](0005-android-shell-navigation-and-fake-directory.md)). Release builds bind nothing. XTTV publishes an XML API with about 18 methods (`getClubs`, `getLeagueGames`, `getTeamStandings`, ...). Its public page gives no base URL, parameters or schemas, and access needs registration by email.

Forces:
- XTTV is non-commercial, volunteer-run and asks for local caching; it may restrict the API under load.
- New XML elements can appear at any time, so parsing must ignore unknown elements.
- `:core:network` may not import `domain`, but a repository implementation must (CLAUDE.md §2). `:core:di` is the only core module allowed to.
- XTTV words differ from ours (Federation, Game); the glossary stays canonical.

## Decision
We will add `:core:xttv` (`bat.kmp.library`). It holds the XML DTOs, mappers and the XTTV-backed repository implementations, and depends on the `:domain:*` interfaces. `:core:network` keeps only the Ktor client factory (descriptive `User-Agent`, concurrency cap of 2) and `AppConfig`. `:core:xttv` exposes one `XttvApi` that owns the in-memory cache, de-duplication of identical concurrent requests and the failure rules. `:core:di` registers `:core:xttv` for `dev`, `beta` and `prod`; `deb` keeps `:core:fake-directory`. All non-debug environments talk to production XTTV, which has no known staging server.

Parsing uses `xmlutil` with unknown children ignored. Tests run each mapper against recorded XML in `commonTest` through a Ktor `MockEngine`; none call the live API. Repositories keep an in-memory TTL cache (Clubs and Teams about 1 hour, Fixtures and standings about 5 minutes) and serve the stale entry when a refresh fails. A failed request throws a `DataUnavailable` exception defined in the domain; empty results stay `emptyList()` or `null`.

## Options Considered

### Option A: Separate `:core:xttv` module — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low-Med: one new module |
| Cost             | A module, `xmlutil` |
| Scalability      | A second data source can sit beside it |
| Team familiarity | High: mirrors `:core:fake-directory` |

**Pros:** keeps `:core:network` free of XTTV and domain; one clear job per module; DTOs stay internal.
**Cons:** one more module to wire in `:core:di`.

### Option B: Everything in `:core:network`
**Pros:** no new module. **Cons:** `:core:network` would import `domain`, breaking §2, and mix generic HTTP with XTTV specifics.

### Option C: Persistent cache in `:core:database` from the start
**Pros:** offline support. **Cons:** `:core:database` does not exist yet; XTTV asked only for request reduction, and offline is a separate product decision.

### Option D: Sealed `Result` return types instead of exceptions
**Pros:** failures visible in signatures. **Cons:** touches every use case and both platforms' ViewModels for little gain over one typed exception.

## Trade-off Analysis
Option A costs one module and keeps the layering rules intact. The in-memory cache meets XTTV's request, and Option C can be added later behind the same repositories. Exceptions are the smallest change to the existing interfaces.

## Consequences
- Release builds finally resolve the repositories; the `TODO` in `androidApp/src/release` goes away.
- Every ViewModel needs a `catch` for `DataUnavailable`.
- `PlayerRepository.searchPlayers` and `TeamRepository.getLineup` stay on the fake until XTTV documents how it exposes them (`TeamDetail` is still PROVISIONAL).
- Revisit the cache when we want offline use, or when XTTV states an acceptable request rate.
- Access depends on XTTV's registration; until it is granted, work uses recorded sample XML only.
