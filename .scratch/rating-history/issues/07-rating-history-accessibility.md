---
status: resolved
blocked_by: [5]
---

# 07: Make the Rating history usable with a screen reader and large text

**What to build:** TalkBack and VoiceOver users hear the Rating history as one summary and can step through its Rating updates; VoiceOver users can play it as an audio graph. See the spec's user stories 38 to 42.

**Blocked by:** 05

- [x] The chart reads as one summary: current Rating, the last change, and how many Rating updates since when, for the visible span
- [x] TalkBack steps through Rating updates with custom actions; VoiceOver with Swift Charts' accessibility, including the audio graph
- [x] Each step reads the same values as the callout
- [x] The section title, toggle and callout grow with the user's text size without clipping
- [x] Summary and step texts in English, German and Austrian German on both platforms
- [x] Previews with the largest text size
- [x] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: The summary avoids a count that needs plurals ("Rating updates shown: 21, since …"). Android steps with two custom actions (Previous/Next Rating update) that select a point, and the callout under the chart is a polite live region, so each step is read. iOS steps with the adjustable action, reads the selection as the chart's value, and describes the audio graph with `AXChartDescriptor`. The 200% font-scale render shows no clipping; nothing here was tried with a real screen reader yet (ticket 09).
