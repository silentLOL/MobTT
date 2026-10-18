# 06: My Player and My Club domain

**What to build:** the shared domain can remember who the user is. A user chooses My Player or My Club, exactly one is active, and the Club and Association shown are always derived. The choice survives restarts.

**Blocked by:** 01

**Status:** done

- [x] Domain models exist for Association, Club, Team and Player, using the glossary terms, with a Player belonging to exactly one Club
- [x] UseCases set and observe the user's choice as `Flow` or `suspend`
- [x] Choosing My Player derives the Club and Association, and choosing My Club derives the Association
- [x] Choosing one replaces the other, so there is never both
- [x] The choice is persisted with DataStore through `:core:preferences`
- [x] Repository interfaces live in separate files from their implementations
- [x] Tests at the UseCase seam run against fake repositories and assert only on observable outcomes
