# 04: Android: Home opens a Team Favorite

**What to build:** on Android, tapping a Team Favorite in Home's Favorites list opens that Team's detail screen, the way a Club Favorite already opens its Club. Ticket 03 made Home list Team Favorites, but their rows have no tap-through on Android; the Team screen is already reachable through the Club feature's public navigation entry, so Home only needs to depend on that API module.

**Blocked by:** None (can start immediately). Tickets 01 and 03 are committed.

**Status:** ready-for-agent

- [ ] Tapping a Team Favorite row opens that Team's detail screen
- [ ] Club Favorite rows still open the Club, and Player rows stay non-interactive, as today
- [ ] The Home ViewModel handles the tap through `onAction` and reports it as a one-off navigation event; the screen takes the navigation as a function parameter, and the Home feature depends only on the Club feature's `:api` module
- [ ] ViewModel tests cover the tap emitting the Team navigation event
- [ ] Previews show a Team Favorite row with the same accessibility labels and touch target as the Club row

**Manual check outstanding:** TalkBack and 200% font scale on a device.
