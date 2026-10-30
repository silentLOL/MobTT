# 06: iOS: the hero is hidden from Favorites

**What to build:** on iOS, Home's Favorites list no longer repeats the Player or Club shown as the hero card, using the shared filtered stream from ticket 02. The stored Favorite reappears when the hero changes.

**Blocked by:** 02 (The hero is hidden from Favorites, shared and Android).

**Status:** ready-for-agent

- [ ] iOS Home lists Favorites from the shared stream that omits the hero Favorite
- [ ] Changing the hero makes the previously hidden Favorite reappear
- [ ] Swift tests at the Home ViewModel seam cover the hero absent from the Favorites list and its reappearance
