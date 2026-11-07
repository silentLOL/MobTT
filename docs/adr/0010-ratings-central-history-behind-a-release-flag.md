# ADR-0010: Ratings Central's history export as a second data source, off in release builds until permitted

**Status:** Accepted
**Date:** 2026-11-07
**Deciders:** Stefan Irndorfer

## Context
The Player screen should show a Player's Rating history as a chart. XTTV gives each Player's Ratings Central ID (`player_rc_id` in `getSingleStandings`) but not their history. Ratings Central has it.

Forces:
- Ratings Central has no documented API. Its only machine-readable output is the per-Player CSV export (`PlayerHistory.php?PlayerID=<id>&CSV_Output=Text`), which carries every Rating update with the Rating and Deviation before and after.
- Its robots.txt disallows `PlayerHistory.php`, `HistoryGraph.php`, `GraphImage.php` and `Player.php` for every user agent, with a 30-second crawl delay. The site states "All rights reserved", publishes no terms of use, and asks people to link to it.
- Ratings Central, like XTTV (ADR-0007), is a small, volunteer-run service. Every install that calls it adds load.
- The user asked for a native, interactive chart and an explanation of what it shows, not a picture of the website.
- The feature is wanted before an answer from Ratings Central can be expected.

## Decision
We will read Ratings Central's CSV history export in a new `:core:ratings-central` module, only when a user opens the Rating history section for one Player, hold each history in memory for 24 hours, and identify the app in the `User-Agent`. The app draws the chart natively. We will ask Ratings Central's maintainers for permission, and an `AppConfig` flag keeps the section on only for `deb` and `dev` until they agree. If they refuse, we drop the in-app history and link to the Player's graph page instead.

## Options Considered

### Option A: CSV export, native chart, release builds off until permitted — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Medium: new core and domain modules, two charts |
| Cost             | One request per Player opened per day per device |
| Scalability      | Bounded by on-demand loading and the 24-hour hold |
| Team familiarity | High: same shape as `:core:xttv` |

**Pros:** Clean, complete data; a chart that fits the app, with selection, dark mode and accessibility; respects the site by asking first and loading only on demand.
**Cons:** Release depends on someone else's answer; the export format can change without notice.

### Option B: Ship the CSV-backed chart without asking
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Same as A |
| Cost             | Same as A |
| Scalability      | Same as A |
| Team familiarity | High |

**Pros:** Ships immediately.
**Cons:** Ignores the site's stated wishes; risks being blocked, and uses a volunteer service against its robots.txt.

### Option C: Link out to Ratings Central's graph page
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low |
| Cost             | None to us; the user's browser loads the page |
| Scalability      | Not a concern |
| Team familiarity | High |

**Pros:** A user browsing a public page; no permission question.
**Cons:** A small static image on a desktop page, no explanation in the app, leaves the app.

### Option D: Embed Ratings Central's graph image
**Pros:** No chart to build.
**Cons:** `GraphImage.php` is disallowed like the export; the image does not scale, theme or speak to screen readers.

## Trade-off Analysis
Option A gives the experience the feature exists for, and the flag means building it costs nothing if the answer is no: the domain and chart stay, and only the data source is swapped for Option C's link. Option B saves a few weeks at the price of using a volunteer service against its stated wishes. Option D has the same permission problem as A with none of the benefits.

## Consequences
- Easier: turning the feature on for release is one `AppConfig` value per platform once permission arrives.
- Easier: the domain sees a `RatingHistoryRepository`, so another source (an official API, or a field from XTTV) can replace the CSV without touching the screens.
- Harder: a second external format to watch. A non-CSV response is treated as unavailable and logged.
- Revisit when Ratings Central answers: on yes, enable `beta` and `prod` and follow any request rate or credit they ask for; on no, replace the section with Option C and mark this ADR superseded.
- Revisit if Ratings Central publishes an API, or XTTV opens `getPlayerDetails` with rating history.
