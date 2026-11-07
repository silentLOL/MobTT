---
status: ready-for-agent
blocked_by: []
---

# 01: Android: open a Fixture's Venue in a maps app

**What to build:** on the Fixture screen, the Venue lines in the red card get a map pin and become one tap target. Tapping it opens the user's maps app, searching for the Venue's name and every address part that exists. Without a maps app, Google Maps opens in the browser. See the spec's Decisions for the search text and URLs.

**Blocked by:** None (can start immediately)

- [ ] The kickoff stays plain text; the Venue's name and address form one tap target with a map pin, shown only when the Venue has a name or any address part
- [ ] The tap target reads to TalkBack as a button, e.g. "Open Sporthalle Hagenberg in Maps" (CODING_STANDARDS §12)
- [ ] The tap is a Fixture detail action; the ViewModel logs `fixture_venue_tapped` with `status` and emits a one-off event carrying the search text
- [ ] The Screen opens a `geo:0,0?q=…` intent through a private `Context` extension, falling back to the Google Maps search URL in the browser when no app handles it
- [ ] `fixture_venue_tapped` has its catalog row in `docs/ANALYTICS.md` (add it if ticket 02 has not)
- [ ] Unit test for the search text: every part, name only, city only, address without name, nothing at all
- [ ] ViewModel test: the action logs the event and emits the search text
- [ ] Previews cover a Fixture with and without a Venue
- [ ] New strings are added in English and German
- [ ] New strings pushed to Loco (wait for the go-ahead: `loco.sh push` overwrites Loco's text for every key)
- [ ] Tapped in a `deb` build: the maps app opens on the Venue
- [ ] `./gradlew check` passes
