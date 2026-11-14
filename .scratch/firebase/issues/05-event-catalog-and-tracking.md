---
status: resolved
blocked_by: [2, 3]
---

# 05: Event catalog and tracking

**What to build:** `docs/ANALYTICS.md` as the single source of truth, then the curated events on both platforms.

- [x] The catalog lists `screen_view`, `my_player_selected`, `my_club_selected`, `favorite_toggled`, `detail_opened`, `search_performed`, `consent_changed` and `setting_changed`, with their parameters.
- [x] Android events fire from each ViewModel's `onAction` handler and iOS events from the matching Swift ViewModel method, asserted with a recording fake.
- [x] No events are sent from `deb`, shown by a test that the `deb` tracker records nothing.
- [x] No event carries a user ID, a name or free text; `search_performed` sends only the query length.
- [x] `scripts/check-conventions.sh` flags an event name in code that the catalog does not list.
- [x] `./gradlew check` and the iOS build pass.
