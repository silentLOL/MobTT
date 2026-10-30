# 01: Favorite toggle on Club, Team and Player screens

**What to build:** a star toggle on every Club, Team and Player screen, on Android and iOS. Tapping it adds or removes the shown item as a Favorite, using the existing Favorites UseCases. Persistence, deduplication and independence from My Player and My Club already exist and do not change.

**Blocked by:** none

**Status:** ready-for-agent

- [ ] Club, Team and Player screens show a star reflecting whether the item is a Favorite
- [ ] Tapping the star adds or removes the Favorite, and Home shows the change
- [ ] State flows through the ViewModel's single UI state and `onAction`, with Android screens stateless and Swift ViewModels using the Koin helper
- [ ] Tests at the UseCase seam against fake repositories assert only observable outcomes
- [ ] ViewModel tests cover toggling on and off on both platforms
- [ ] German and English strings, with accessibility labels that announce the state and the action
- [ ] Previews cover favourited, not favourited and a long-name state

**Manual check outstanding:** 200% font scale and TalkBack or VoiceOver on a device.
