# 12: Upcoming Fixtures for My Player's Teams on Home

**What to build:** on both platforms, Home shows the upcoming Fixtures for the Teams of My Player (or the Teams of My Club), each with date, time, opponent and venue. A Team with no Fixture reads as a free week, not as an error.

**Blocked by:** 10, 11

**Status:** ready-for-agent (the venue field is `needs-info` until the XTTV docs or sample responses exist)

- [ ] A shared UseCase observes upcoming Fixtures for the Teams derived from the user's choice, ordered by date
- [ ] Home on Android and on iOS shows date, time, opponent and venue per Fixture, with the free-week state
- [ ] A user with neither My Player nor My Club sees the existing empty Home, not an empty Fixture list
- [ ] UseCase-seam tests cover ordering, finished Fixtures excluded and free week
- [ ] Previews cover loaded, free week and empty on both platforms
- [ ] Accessibility rules and 200% text scaling hold on both platforms
- [ ] If venue is unavailable from the API, the field is omitted from the UI rather than shown blank

## Out of scope

- Live scores (needs-info, later ticket)
