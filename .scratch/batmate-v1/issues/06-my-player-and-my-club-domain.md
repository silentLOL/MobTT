# 06: My Player and My Club domain

**What to build:** the shared domain can remember who the user is. A user chooses My Player or My Club, exactly one is active, and the Club and Association shown are always derived. The choice survives restarts.

**Blocked by:** 01

**Status:** ready-for-agent

- [ ] Domain models exist for Association, Club, Team and Player, using the glossary terms, with a Player belonging to exactly one Club
- [ ] UseCases set and observe the user's choice as `Flow` or `suspend`
- [ ] Choosing My Player derives the Club and Association, and choosing My Club derives the Association
- [ ] Choosing one replaces the other, so there is never both
- [ ] The choice is persisted with DataStore through `:core:preferences`
- [ ] Repository interfaces live in separate files from their implementations
- [ ] Tests at the UseCase seam run against fake repositories and assert only on observable outcomes
