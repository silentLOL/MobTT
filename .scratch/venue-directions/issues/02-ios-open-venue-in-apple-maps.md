---
status: ready-for-agent
blocked_by: []
---

# 02: iOS: open a Fixture's Venue in Apple Maps

**What to build:** on the Fixture screen, the Venue lines in the red card get a map pin and become one tap target. Tapping it opens Apple Maps, searching for the Venue's name and every address part that exists; when Maps is deleted, iOS opens Apple Maps on the web. See the spec's Decisions for the search text and URL.

**Blocked by:** None (can start immediately)

- [ ] The kickoff stays plain text; the Venue's name and address form one tap target with a map pin, shown only when the Venue has a name or any address part
- [ ] The tap target reads to VoiceOver as a button, e.g. "Open Sporthalle Hagenberg in Maps" (CODING_STANDARDS §12)
- [ ] The tap is a Fixture detail action; the ViewModel logs `fixture_venue_tapped` with `status` and the view opens `https://maps.apple.com/?q=…` with `openURL`
- [ ] `fixture_venue_tapped` has its catalog row in `docs/ANALYTICS.md` (add it if ticket 01 has not)
- [ ] Swift test for the search text: every part, name only, city only, address without name, nothing at all
- [ ] ViewModel test: the action logs the event and hands over the search text
- [ ] Previews cover a Fixture with and without a Venue
- [ ] New `.xcstrings` keys are added in English and German
- [ ] New strings pushed to Loco (wait for the go-ahead: `loco.sh push` overwrites Loco's text for every key)
- [ ] Tapped in the simulator: Apple Maps opens on the Venue
- [ ] The iOS build passes (docs/DEVELOPMENT.md)
