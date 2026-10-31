# ADR-0008: One XTTV host per Association, and plain string IDs

**Status:** Accepted (the host decision is superseded, see the 2026-10-28 update; the plain string ID decision stands)
**Date:** 2026-10-17
**Deciders:** Stefan Irndorfer

## Context
Evidence for XTTV's layout is thin: the mockup mentions `oettv.xttv.at` and `wttv.xttv.at`, the former serves the XTTV Ergebnisdienst, and the API page names no base URL. We do not know whether IDs (Club, Team, League, Player) are unique across Associations or only within one. Favorites are persisted in DataStore by those IDs, so changing their format later needs a migration.

This ADR rests on assumptions the registration reply (see ADR-0007) will confirm or correct.

## Decision
~~We will assume one XTTV host per Association until XTTV says otherwise. `Association` gets a base URL, and My Club, My Player and Favorites select their host through their Association. `AppConfig` carries only the default host. Lookup goes through a single `XttvHostResolver`, so a one-host answer is a one-line change.~~ Superseded: XTTV serves every Federation from one host, so `AppConfig.xttvHost` is the only host and `XttvHostResolver` is gone.

We will keep XTTV's numeric IDs as plain strings (`"8846"`), with no prefix or wrapper. If real data shows IDs collide across Associations, we switch to the composite `"{associationId}:{id}"` and migrate stored Favorites.

## Options Considered

### Option A: Host per Association, plain string IDs — chosen (host part superseded)
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low |
| Cost             | A resolver and one extra field on `Association` |
| Scalability      | New Associations need no code |
| Team familiarity | High |

**Pros:** matches what the mockup suggests; domain IDs stay simple.
**Cons:** a wrong guess about uniqueness means a Favorites migration.

### Option B: Composite IDs from day one
**Pros:** safe if IDs collide. **Cons:** every ID is longer and parsed in places, a cost paid even if XTTV IDs turn out globally unique.

### Option C: Single central host
**Pros:** simplest client. **Cons:** contradicts the only evidence we have.

## Trade-off Analysis
Plain IDs are cheaper if XTTV IDs are globally unique, and the resolver contains the cost if the host layout differs. The migration risk is real but bounded, because Favorites are a small list of strings.

## Consequences
- Do not ship Favorites persistence for XTTV-backed IDs to `prod` before XTTV answers the uniqueness question, or the migration is a live one.
- `Association` and `Club` domain models may gain a base-URL or Association field when the first real response is recorded.

## Update 2026-10-28: single host
XTTV's per-method documentation shows one host, `https://oettv.xttv.at/xml/v1/<method>.php`, serving every Federation (`getFederations` lists them all, and `getClubs`/`getLeagues` take a `federationId`). The host-per-Association assumption is dropped, along with `XttvHostResolver`; repositories use `AppConfig.xttvHost`. The ID question is still open: IDs are unique per kind in every response seen so far, but this was not checked across Federations.

## Update 2026-10-31: accepted
Verified against the live API (xttv-api ticket 10). The single host stands and plain string IDs are kept, so the composite ID and Favorites migration are not needed.
