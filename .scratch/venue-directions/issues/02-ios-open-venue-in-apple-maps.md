---
status: resolved
blocked_by: []
---

# 02: iOS: open a Fixture's Venue in Apple Maps

**What to build:** on the Fixture screen, the Venue lines in the red card get a map pin and become one tap target. Tapping it opens Apple Maps, searching for the Venue's name and every address part that exists; when Maps is deleted, iOS opens Apple Maps on the web. See the spec's Decisions for the search text and URL.

**Blocked by:** None (can start immediately)

- [x] The kickoff stays plain text; the Venue's name and address form one tap target with a map pin, shown only when the Venue has a name or any address part
- [x] The tap target reads to VoiceOver as a button, e.g. "Open Sporthalle Hagenberg in Maps" (CODING_STANDARDS §12)
- [x] The tap is a Fixture detail action; the ViewModel logs `fixture_venue_tapped` with `status` and the view opens `https://maps.apple.com/?q=…` with `openURL`
- [x] `fixture_venue_tapped` has its catalog row in `docs/ANALYTICS.md` (add it if ticket 01 has not)
- [x] Swift test for the search text: every part, name only, city only, address without name, nothing at all
- [x] ViewModel test: the action logs the event and hands over the search text
- [x] Previews cover a Fixture with and without a Venue
- [x] New `.xcstrings` keys are added in English and German
- [x] New strings pushed to Loco
- [ ] Tapped in the simulator: Apple Maps opens on the Venue
- [x] The iOS build passes (docs/DEVELOPMENT.md)

## Comments

- 2026-11-07: `Formatting/VenueSearch.swift` holds `venueLines`, `venueSearchText` and `AppleMaps.search`, which also encodes `+`. VoiceOver reads the Venue lines as the label and "Open in Maps" as the hint (`fixture_detail_venue_open_cd`). Not done yet: the Loco push and tapping it in the simulator.
- 2026-11-07: The `.xcstrings` key was pushed to Loco with 01's.
