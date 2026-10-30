# 02: The hero is hidden from Favorites (shared and Android)

**What to build:** when the Player or Club the user chose as My Player or My Club is also one of their Favorites, Home shows it only as the hero card, not again in the Favorites list. The stored Favorite is untouched and reappears when the hero changes. The shared domain does the filtering so both platforms agree; Android Home uses it.

**Blocked by:** None (can start immediately).

**Status:** done

- [x] A shared Home Favorites stream omits a Favorite Player equal to My Player and a Favorite Club equal to My Club; other Favorites and the stored set are untouched
- [x] Changing My Player or My Club makes the previously hidden Favorite reappear
- [x] Registered in `:core:di` and reachable from the iOS Koin helper
- [x] Android Home lists Favorites from this stream
- [x] UseCase-seam tests cover a hidden hero Favorite, other Favorites unaffected, and reappearance; an Android Home ViewModel test shows the hero absent from the Favorites list
