---
---

# Fixture details

## Problem Statement

League detail lists every Fixture with its Matches-won score, but a fan cannot see how it was decided: who played whom, and who won which Match. XTTV's Ergebnisdienst page shows this per Fixture; the app does not.

## Solution

Each Fixture row in League detail gets an info button. It opens a Fixture screen with a back button: a header with both Teams (each opens its Team), the score, kickoff, venue and address, then the list of Matches with the players of both sides and the Sets won. A Fixture not yet played shows "Not played yet" in place of the score and no Matches.

## Decisions

- Data comes from XTTV's XML API `getGameDetails` (ADR-0007), not from scraping the Ergebnisdienst HTML. Its `game_result` is our Match; players are resolved from their positions.
- The info button is on League detail rows only, on every Fixture, finished or not.
- Players are plain text until Player detail has real data.
- Left out: XTTV's `confirmed` flag (the XML does not say which Team confirmed), the `points` field (empty so far) and a map link.
- Details are cached with the Fixture TTL; a failed refresh serves the stale entry, and a failure with nothing cached is `DataUnavailable`, shown as the error state with a retry.

## User Stories

1. As a fan, I want an info button on each Fixture in a League, so that I can open its details
2. As a fan, I want to go back from the Fixture to the League, so that I keep my place in the list
3. As a fan, I want to see both Teams, the score, kickoff, venue and address, so that I know what and where it was
4. As a fan, I want to tap a Team in the header, so that I land on that Team
5. As a fan, I want each Match listed with its players and Sets won, with doubles pairs shown together, so that I can see who won what
6. As a fan, I want the winner of a Match in bold, so that I can scan the result
7. As a fan, I want "Not played yet" for a future Fixture, so that I do not look for results that do not exist
8. As a fan, I want a clear message and a retry when XTTV cannot answer, so that I know it is not my fault
9. As a screen reader user, I want each Match read as one sentence and the info button to name its Fixture, so that I can use the list without seeing it
10. As a German-speaking fan, I want the screen in German, so that it reads naturally
