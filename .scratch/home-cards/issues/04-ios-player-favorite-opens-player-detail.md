# 04: iOS: a Player Favorite opens Player detail

**What to build:** on iOS, tapping a Player Favorite row on Home opens a new Player detail view showing the Player's name, Club and Association, a Favorite star and the native back chevron at the top left. A Player that cannot be resolved shows an unavailable state.

**Blocked by:** 01 (Android: a Player Favorite opens Player detail), for the shared Player resolution.

**Status:** ready-for-agent

- [ ] Tapping a Player Favorite row opens that Player's detail; Club and Team rows behave as today
- [ ] A Player route in the Home `NavigationStack` in `ShellScreen`, like the Club and Team routes
- [ ] Player detail shows name, Club, Association, a Favorite star that adds or removes the Favorite, and a back chevron at the top left
- [ ] An unresolvable Player shows an unavailable state instead of going blank
- [ ] Swift tests at the Home ViewModel seam cover the Player-row tap; the Player detail ViewModel covers loaded, unavailable and star toggle
- [ ] Previews for the Player detail and a Player row; VoiceOver labels and button trait, 44pt touch targets, Dynamic Type safe, String Catalog EN and DE

**Manual check outstanding:** VoiceOver, 200% Dynamic Type and the back chevron on a device.
