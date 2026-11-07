---
status: ready-for-agent
blocked_by: [4, 5, 6, 7]
---

# 08: Push the Rating history strings to Loco

**What to build:** every string key the Rating history added reaches Loco in one push, so translators can review the English, German and Austrian German texts.

**Blocked by:** 04, 05, 06, 07

- [ ] Every new key exists on both platforms with English, German and Austrian German values, with the same meaning on each platform
- [ ] The maintainer confirms the push in chat before it runs
- [ ] The keys are pushed with the project's Loco script, and a pull afterwards changes nothing
- [ ] `./gradlew check` and the iOS build pass

## Comments
