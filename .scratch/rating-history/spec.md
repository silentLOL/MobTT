---
---

# Rating history on the Player screen

## Problem Statement

The Player screen shows a Player's Rating as one number in its stat row and nothing else. A fan cannot see whether the Player is improving, how the last weeks went, or how certain that number is. Ratings Central keeps every Player's full Rating history and draws it as a graph, but only on its own website. The fan has to find the Player there by hand, and the graph is a small static image that does not work well on a phone.

The Player screen is also slow to appear. It waits behind one spinner until the Player, the Club, every Team's lineup and the Association have all loaded, even though the Player's name is known at once and the sections do not depend on each other.

## Solution

The Player screen draws at once with the Player's name and Favorite star. Club, Teams and the stat row each load on their own, show their own loading and Unavailable states, and retry on their own.

Below the Teams, a **Rating history** section shows how the Player's Rating changed after each Rating update. It is always shown open, with no collapse arrow, and loads as soon as the Player's Ratings Central entry is known. The chart is drawn natively on both platforms: a line through the Rating, a shaded band of ±1 Deviation, and a dot per Rating update. A **12 months / All** toggle chooses the time span. Touching the chart shows the date, name, Rating ±Deviation and change of the nearest Rating update.

An info button next to the section title opens a short explanation of what the Rating, the Deviation and the band mean, with a link to Ratings Central's own explanation.

The data comes from Ratings Central's public CSV export, keyed by the Ratings Central ID that XTTV gives for every Player. Ratings Central has no API and its robots.txt disallows these pages, so we ask its maintainers for permission. Until they agree, the section is enabled only in `deb` and `dev` builds (ADR-0010).

Android and iOS ship together.

## User Stories

### A faster Player screen

1. As a fan, I want the Player's name and Favorite star to appear as soon as I open a Player, so that I know at once I am in the right place
2. As a fan, I want the Club section to show as soon as the Club is known, so that I am not waiting for the Teams to load
3. As a fan, I want the Teams section to load on its own, so that a Club with many Teams does not hold back the rest of the screen
4. As a fan, I want the stat row with the Rating and age group to appear once the Teams have loaded, so that I see the numbers without a full-screen spinner
5. As a fan, I want a section that failed to load to say so and offer a retry, so that one failed request does not blank the whole screen
6. As a fan, I want retrying one section to leave the others as they are, so that what already loaded does not flicker
7. As a fan, I want to mark a Player as a Favorite while the sections are still loading, so that I am not blocked by slow requests
8. As a fan opening a Player XTTV no longer knows, I want the screen to say the Player is unavailable, as it does today, so that I am not shown empty sections

### Rating history section

9. As a fan, I want a Rating history section on the Player screen, so that I can see how the Player's playing strength developed
10. As a fan, I want the Rating history always shown open on the Player screen, so that I see it without an extra tap
11. As a fan, I want the Rating history to load with the Player screen, so that it is there when I scroll to it
12. As a fan, I want the section to keep its chart when I close and reopen it, so that I am not waiting again
13. As a fan, I want the section hidden for a Player without a Ratings Central entry, so that I am not shown a section that can never fill
14. As a fan, I want "No Rating history yet" when the Player has an entry but no Rating update, so that I know the data is not missing by mistake
15. As a fan, I want a Player with one Rating update to show a single point with its value, so that a new Player still gets an answer
16. As a fan, I want the section to say it is unavailable and offer a retry when Ratings Central cannot be reached, so that I can try again later
17. As a fan, I want the Rating history held for a day once loaded, so that reopening a Player is instant and Ratings Central is not asked again

### The chart

18. As a fan, I want a line through the Player's Rating after each Rating update, so that I see the trend at a glance
19. As a fan, I want a shaded band of ±1 Deviation around the line, so that I see how certain each value was
20. As a fan, I want a dot for each Rating update when there are few of them, so that I can tell where the Rating actually moved
21. As a fan, I want the dots left out when there are many Rating updates, so that a long history stays readable
22. As a fan, I want dates along the bottom and Rating points along the side, so that I can read values off the chart
23. As a fan, I want the vertical scale fitted to the band with some room above and below, so that small changes are still visible
24. As a fan, I want the wide starting value Ratings Central gives an unrated Player left out, so that the real curve is not squashed
25. As a fan, I want a 12 months / All toggle, so that I can look at the current form or the whole career
26. As a fan, I want the chart to start on 12 months, so that what I see first is the recent form
27. As a fan whose Player has no Rating update in the last 12 months, I want the 12 months view to say so, so that an empty chart is not mistaken for an error
28. As a fan, I want to tap or drag on the chart to select the nearest Rating update, so that I can read its exact values
29. As a fan, I want the selected Rating update to show its date, name, Rating ±Deviation and change, so that I know what happened that week
30. As a fan, I want the chart to follow the app's colours in light and dark mode, so that it looks like the rest of the app

### Explanation

31. As a fan, I want an info button next to the Rating history title, so that I can find out what the chart means
32. As a fan, I want the info sheet to explain in plain words what the Rating, the Deviation and the band mean, so that I can read the chart without knowing statistics
33. As a fan, I want the info sheet to say that the Rating moves once per Rating update, so that I understand why the line has steps
34. As a fan, I want a "Learn more at Ratings Central" link in the info sheet, so that I can read the full explanation at the source
35. As a fan, I want the info sheet to close with a swipe or a button, so that I return to the chart quickly

### Language and accessibility

36. As a German-speaking fan, I want every new text in German and Austrian German, so that the section reads naturally
37. As a translator, I want the new texts available in Loco, so that I can review and change them without a code change
38. As a screen-reader user, I want the chart read as one summary of the current Rating, the last change and the span covered, so that I get the gist without exploring the chart
39. As a screen-reader user, I want to step through the Rating updates one by one, so that I can hear each value
40. As a VoiceOver user on iOS, I want the chart available as an audio graph, so that I can hear the trend
41. As a user with large text, I want the section title, toggle, callout and info sheet to grow with my text size, so that I can read them
42. As a screen-reader user, I want the info button labelled "About the Rating history", so that I know what it opens

### Release and measurement

43. As the maintainer, I want the Rating history section enabled only in `deb` and `dev` builds until Ratings Central agrees, so that release builds do not use their data without permission
44. As the maintainer, I want to turn the section on for `beta` and `prod` with one shared change, so that the release after their answer is small
45. As the maintainer, I want requests to Ratings Central to identify the app, so that its maintainers can see and contact us
46. ~~As the maintainer, I want an analytics event when the Rating history section is opened~~ (dropped: the section is always open)
47. As the maintainer, I want an analytics event when the info sheet is opened, so that I know whether the explanation is needed
48. As a developer, I want `deb` builds to show a fake Rating history, so that I can work on the chart without network access

## Implementation Decisions

### Ratings Central ID

- XTTV's `getSingleStandings` carries `player_rc_id`, `player_rc_points` and `player_rc_deviation` on every standing row, identical in every League. Only the ID is needed now.
- `RankedPlayer` gains a nullable Ratings Central ID, mapped from `player_rc_id`. An empty or missing value maps to null.
- The Player's Ratings Central ID is looked up through the Player ranking of one League of the Player's Teams (each `Team` already knows its `leagueId`). One League is enough; if the Player has no row there, the next League is tried. League standings are already held for 5 minutes (ADR-0007).
- No Team, no League, no row or no ID means the Player has no Rating history; the section is hidden.

### Ratings Central data source

- New KMP module `:core:ratings-central` (`bat.kmp.library`), following ADR-0007's shape for `:core:xttv`: it holds the HTTP access, the CSV parsing and the repository implementation, and depends on the domain interface.
- It reads the history export `PlayerHistory.php?PlayerID=<id>&CSV_Output=Text`. Columns: `EventID, EventDate, EventName, InitialMean, InitialStDev, PointChange, FinalMean, FinalStDev`. Unknown extra columns are ignored, by header name.
- A response that is not CSV with that header (for example an HTML page) is treated as unavailable and logged with Kermit, without the Player's name.
- Requests use the shared Ktor client factory, with a `User-Agent` naming the app and its version.
- One in-memory cache entry per Ratings Central ID, held for 24 hours, with the stale entry served when a refresh fails, as `XttvApi` does. Identical concurrent requests share one call.
- The base URL comes from `AppConfig`, never from source.

### Domain

- New KMP module `:domain:rating` with the models and the repository interface:
  - `RatingUpdate`: date, name, Rating before and after (points and Deviation each), and point change.
  - `RatingHistory`: the Player's Rating updates, oldest first.
  - `RatingHistoryRepository`: returns the `RatingHistory` for a Ratings Central ID; throws `DataUnavailable` when Ratings Central cannot answer and nothing is held.
- `GetRatingHistoryUseCase` takes the Player's ID and the Leagues of their Teams. It resolves the Ratings Central ID through the Player ranking, then asks the repository. It returns a result that tells "no Ratings Central entry" apart from "an entry with an empty history", and a failure apart from both.
- The use case returns every Rating update. Which part to draw (12 months or all, without the unrated starting value) is presentation logic. The chart plots the Rating after each update, so the starting value never appears as a point.
- The glossary terms Rating, Deviation, Rating update, Rating history and Ratings Central are used throughout. "Event" from Ratings Central's CSV appears only in the data source module.

### Player screen, section by section

- `GetPlayerDetailUseCase`'s single chain is split into calls the screen can make on its own: the Player (known from the picked Players, so instant), the Club with its Association, and the Teams with the lineup-derived Rating and age group for the stat row.
- Both platforms reuse the section state pattern from the League screen (Android `SectionUi`, iOS `SectionUiState`: loading, unavailable, loaded), lifting it where both screens can share it.
- The screen's top-level state keeps only "Player unavailable" versus content; each section holds its own state and retry.
- The Rating history section waits for the Teams section, since it needs the Leagues. Opening it before the Teams have loaded shows it loading until both are done.
- Rating history is a plain section (header with the info button, no collapse arrow) using the shared section state: loaded as soon as the Ratings Central entry is known, retried from its own Unavailable state.

### Chart

- A design-system component on each platform, `BatRatingChart`, with the same name on both. It takes plain points (date, value, low, high) and an optional selection, and knows nothing of the domain.
- iOS uses Swift Charts: an area for the band, a line for the Rating, points for the Rating updates, and a selection for the callout. The audio graph comes from its accessibility support.
- Android draws with a Compose `Canvas` and adds no chart library. It exposes a semantic summary and custom accessibility actions to step through the points.
- Dots are drawn only when the visible span has 40 Rating updates or fewer.
- The 12 months / All toggle is a segmented control from each platform's design system; it starts on 12 months and is held per screen, not stored.

### Info sheet

- A reusable design-system component, `BatInfoSheet`, on both platforms: a title, body text and an optional link. It is a `ModalBottomSheet` on Android and a `.sheet` with a medium detent on iOS.
- It is opened from a `BatIcon.Info` button next to the Rating history title.
- Body, in plain words: the line is Ratings Central's estimate of the Player's playing strength; ± is the Deviation, how certain that estimate is; about two times in three the true strength lies within the band; a wider band means the Rating can move further at the next Rating update; the Rating changes once per Rating update. The link opens Ratings Central's "How it works" page in the in-app browser (Custom Tab or Safari view).

### Strings and Loco

- Every new text is a key in `:android:core:localization` and in the iOS string catalog, with English, German and Austrian German values: section title, toggle labels, empty, no-recent and unavailable texts, callout labels, accessibility summary and labels, info sheet title, body and link.
- The new keys are pushed to Loco with the project's Loco script in the same ticket that adds them. The push is run only after the maintainer confirms it.

### Release toggle

- `AppConfig` gains a flag for the Rating history, derived from the environment like `xttvHost`: on for `deb` and `dev`, off for `beta` and `prod` until Ratings Central agrees. (2026-11-07: they agreed; it is now on everywhere.)
- With the flag off, the section is never shown and Ratings Central is never called.
- `deb` binds a fake `RatingHistoryRepository` from `:core:fake-directory` with a few Players' histories, including one with a single Rating update and one with none.

### Analytics

- One event, added as a catalog row first (docs/ANALYTICS.md): `rating_info_opened` when the info sheet opens. (`rating_history_opened` was dropped once the section stopped collapsing: there is nothing left to open.) No Player identifiers. The 12 months / All toggle is not tracked.

## Testing Decisions

A good test drives a module through its public interface with recorded or fake data and checks what a caller sees. It does not check how the module gets there: no assertions on internal calls, private state or call order. No test calls Ratings Central or XTTV.

Three seams, all of them existing kinds:

1. **Domain use cases** (`commonTest`, fake repositories). `GetRatingHistoryUseCase`: ID found in the first League, found only in a later League, no Teams, no row, no ID, empty history, repository failure. The split Player detail calls: Player unknown, Club unresolved, one Team's lineup failing. Prior art: `PlayerDetailTest`, `PlayerRankingTest`, `LeagueDetailTest`.
2. **Data sources** (`commonTest`, Ktor `MockEngine`, recorded responses). The CSV export of a real Player kept as a sample; an HTML response; a missing column; an extra column; a failed refresh served from the held entry; the 24-hour hold. In `:core:xttv`, `player_rc_id` mapped from the recorded standings sample, including an empty value. Prior art: `XttvApiTest`, `XttvPlayerRepositoryTest`.
3. **ViewModels on both platforms** (fake use cases). The screen draws before any section loads; each section loads, fails and retries on its own; Rating history loads on first expand only, waits for the Teams, is hidden without an ID, and is never requested when the flag is off; the 12 months / All window, the unrated starting value left out, and the 40-point limit for dots; the info-sheet and section-open analytics events. Prior art: `PlayerDetailViewModelTest` and `LeagueDetailViewModelTest` (Android), `PlayerDetailViewModelTests` and `LeagueDetailViewModelTests` (iOS).

The chart and the info sheet are checked with previews (light, dark, large text, empty, one point, many points) and the manual device check, not with unit tests.

## Out of Scope

- Rank and matches won and lost on each Team row from the Player ranking (player-detail-teams ticket 01, proposal 2): a separate feature.
- Showing the Deviation next to the Rating in the stat row. The stat row keeps its lineup-derived Rating.
- Opening a Rating update's results, Matches or opponents, here or on Ratings Central.
- Persisting the Rating history across app launches.
- Comparing two Players' Rating histories.
- The Rating history of anyone other than a Player shown on the Player screen (no Rating on Team or League screens).
- Embedding Ratings Central's graph image or web page in the app.
- Asking XTTV to open `getPlayerDetails` (still open on player-detail-teams ticket 01).

## Further Notes

- **Permission first.** Ratings Central's robots.txt disallows `PlayerHistory.php`, `HistoryGraph.php`, `GraphImage.php` and `Player.php` for every user agent, with a 30-second crawl delay. The site states "All rights reserved", publishes no terms of use, and asks people to link to it. ADR-0010 records why we ship behind a flag and what we do if the answer is no: drop the in-app history and link to the Player's graph page in the in-app browser.
- **Draft email to Ratings Central** (for the maintainer to send, in English):

  > Subject: Permission to show Ratings Central history in a table tennis app
  >
  > Dear Dr. Marcus, dear Mr. O'Neill,
  >
  > I develop MobTT, a free mobile app for Austrian table tennis players and fans. It shows Clubs, Teams, Leagues and Players using data from XTTV, which links every Player to their Ratings Central ID.
  >
  > We would like to show a Player's rating history as a chart on the Player screen, based on your history export (PlayerHistory.php with CSV_Output=Text). The app would request it for one Player at a time, when a user opens that Player's screen, keep the result on the device for 24 hours, identify itself in the User-Agent, and credit Ratings Central with a link to your site in the explanation next to the chart.
  >
  > Your robots.txt disallows these pages, so we would rather ask than assume. May we use the export this way? If you prefer another endpoint, a different request rate or particular wording for the credit, we are happy to follow it.
  >
  > Thank you for Ratings Central and for your time.
- Ratings Central's "How it works" page is the source for the info sheet's wording; the text is written in our own words.
- The example Player used during research is Ratings Central ID 163468 (21 Rating updates, starting from the unrated default of 1450 ±450).
