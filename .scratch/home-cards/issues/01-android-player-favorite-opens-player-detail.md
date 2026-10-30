# 01: Android: a Player Favorite opens Player detail

**What to build:** on Android, tapping a Player Favorite row on Home opens a new Player detail screen showing the Player's name, Club and Association, a Favorite star and a top-left back chevron. A Player that cannot be resolved shows an unavailable state. This includes the shared way of resolving one Player to those details.

**Blocked by:** None (can start immediately).

**Status:** ready-for-agent

- [ ] A shared UseCase resolves a Player ID to name, Club name and Association name; an unresolvable Player yields a data-unavailable result, not an exception. It is registered in `:core:di` and reachable from the iOS Koin helper
- [ ] Tapping a Player Favorite row opens that Player's detail; Club and Team rows behave as today
- [ ] The Player detail destination lives in the Players feature's `:api` module and its screen in `:impl`; Home depends only on `:api`
- [ ] Player detail shows name, Club, Association, a Favorite star that adds or removes the Favorite, and a back chevron at the top left
- [ ] An unresolvable Player shows an unavailable state instead of crashing or going blank
- [ ] Taps route through `onAction` and a one-off navigation event; screens take navigation as function parameters
- [ ] UseCase-seam tests cover resolution and the unresolvable Player; ViewModel tests cover the Player-row tap, and the Player detail loaded, unavailable and star-toggle states
- [ ] Previews for the Player detail and a Player row; accessibility labels (button role, name and kind), 48dp touch targets, EN and DE strings

**Manual check outstanding:** TalkBack, 200% font scale and the back chevron on a device.
