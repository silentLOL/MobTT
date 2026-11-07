---
status: ready-for-agent
blocked_by: [5]
---

# 07: Make the Rating history usable with a screen reader and large text

**What to build:** TalkBack and VoiceOver users hear the Rating history as one summary and can step through its Rating updates; VoiceOver users can play it as an audio graph. See the spec's user stories 38 to 42.

**Blocked by:** 05

- [ ] The chart reads as one summary: current Rating, the last change, and how many Rating updates since when, for the visible span
- [ ] TalkBack steps through Rating updates with custom actions; VoiceOver with Swift Charts' accessibility, including the audio graph
- [ ] Each step reads the same values as the callout
- [ ] The section title, toggle and callout grow with the user's text size without clipping
- [ ] Summary and step texts in English, German and Austrian German on both platforms
- [ ] Previews with the largest text size
- [ ] `./gradlew check` and the iOS build pass

## Comments
