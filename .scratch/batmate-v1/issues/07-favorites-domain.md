# 07: Favorites domain

**What to build:** the shared domain can remember what the user wants quick access to. A user adds and removes Favorites, each a Player, Team or Club, and the list survives restarts. Favorites are independent of My Player and My Club.

**Blocked by:** 01

**Status:** done

- [x] UseCases add, remove and observe Favorites of Player, Team and Club
- [x] Adding the same Favorite twice results in one entry
- [x] Favorites persist locally with DataStore through `:core:preferences`
- [x] Changing My Player or My Club does not change Favorites
- [x] Tests at the UseCase seam run against fake repositories and assert only on observable outcomes
