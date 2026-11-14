---
status: resolved
blocked_by: [4, 5, 6, 7]
---

# 08: Push the Rating history strings to Loco

**What to build:** every string key the Rating history added reaches Loco in one push, so translators can review the English, German and Austrian German texts.

**Blocked by:** 04, 05, 06, 07

- [x] Every new key exists on both platforms with English, German and Austrian German values, with the same meaning on each platform
- [x] The maintainer confirms the push in chat before it runs
- [x] The keys are pushed with the project's Loco script, and a pull afterwards changes nothing
- [x] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: Pushed after the maintainer's OK in chat. A pull beforehand showed Loco had no text differing from the repo, only missing keys, so no translator edit was overwritten. The push also uploaded keys from earlier features that had never reached Loco (League tab, Fixture detail, consent, settings): Android 74 new assets per locale, iOS 15. A pull afterwards returns the same strings on both platforms; Loco only lists the Android ones in a different order, so the repo order was kept.
