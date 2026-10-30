---
status: resolved
blocked_by: [2]
---

# 06: iOS: the hero is hidden from Favorites

**What to build:** on iOS, Home's Favorites list no longer repeats the Player or Club shown as the hero card, using the shared filtered stream from ticket 02. The stored Favorite reappears when the hero changes.

- [x] iOS Home lists Favorites from the shared stream that omits the hero Favorite
- [x] Changing the hero makes the previously hidden Favorite reappear
- [x] Swift tests at the Home ViewModel seam cover the hero absent from the Favorites list and its reappearance
