---
status: ready-for-agent
blocked_by: []
---

# 02: Load the Player screen section by section

**What to build:** on both platforms, the Player screen appears at once with the Player's name and Favorite star instead of a full-screen spinner. Club, Teams and the stat row each load, fail and retry on their own. This is the groundwork the Rating history section sits in. See the spec, "Player screen, section by section".

**Blocked by:** None (can start immediately)

- [ ] The domain offers the Player, the Club with its Association, and the Teams with the lineup-derived Rating and age group as separate calls
- [ ] The screen draws at once with the name and the Favorite star; the Favorite star works while sections load
- [ ] Club, Teams and the stat row each show their own loading and Unavailable states, with a retry that leaves the other sections alone
- [ ] An unknown Player still shows the Player-unavailable state
- [ ] The League screen's section state type is reused on each platform, lifted where both screens can share it
- [ ] Domain tests for the split calls; Android and iOS ViewModel tests cover: drawn before any section loads, each section failing and retrying on its own, unknown Player
- [ ] Previews show each section loading and unavailable
- [ ] `./gradlew check` and the iOS build pass

## Comments
