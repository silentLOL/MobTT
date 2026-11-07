---
status: resolved
blocked_by: []
---

# 01: Android: open a Fixture's Venue in a maps app

**What to build:** on the Fixture screen, the Venue lines in the red card get a map pin and become one tap target. Tapping it opens the user's maps app, searching for the Venue's name and every address part that exists. Without a maps app, Google Maps opens in the browser. See the spec's Decisions for the search text and URLs.

**Blocked by:** None (can start immediately)

- [x] The kickoff stays plain text; the Venue's name and address form one tap target with a map pin, shown only when the Venue has a name or any address part
- [x] The tap target reads to TalkBack as a button, e.g. "Open Sporthalle Hagenberg in Maps" (CODING_STANDARDS §12)
- [x] The tap is a Fixture detail action; the ViewModel logs `fixture_venue_tapped` with `status` and emits a one-off event carrying the search text
- [x] The Screen opens a `geo:0,0?q=…` intent through a private `Context` extension, falling back to the Google Maps search URL in the browser when no app handles it
- [x] `fixture_venue_tapped` has its catalog row in `docs/ANALYTICS.md` (add it if ticket 02 has not)
- [x] Unit test for the search text: every part, name only, city only, address without name, nothing at all
- [x] ViewModel test: the action logs the event and emits the search text
- [x] Previews cover a Fixture with and without a Venue
- [x] New strings are added in English and German
- [x] New strings pushed to Loco
- [ ] Tapped in a `deb` build: the maps app opens on the Venue
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: The search text lives in `formatting/VenueSearch.kt` (`venueLines`, `venueSearchText`). `Context.openInMaps` tries `geo:` and then the Google Maps URL, logging a warning when no app opens one, so a device with neither does not crash. TalkBack reads the Venue lines as the label and "Open in Maps" as the click action (`fixture_detail_venue_open_cd`), reusing the visible text (CODING_STANDARDS §12). Not done yet: the Loco push and tapping it in a `deb` build.
- 2026-11-07: Pushed to Loco (Android and iOS, en and de-AT); `fixture_detail_venue_open_cd` reads back as "Open in Maps" and "In Karten öffnen".
