---
status: resolved
blocked_by: [3]
---

# 05: 12 months / All, and the details of one Rating update

**What to build:** the user chooses between the last 12 months and the whole Rating history, and touches the chart to read one Rating update's date, name, Rating ±Deviation and change. See the spec's "Chart" and user stories 21 to 29.

**Blocked by:** 03

- [x] A 12 months / All segmented control from each design system, starting on 12 months, held per screen and not stored
- [x] The 12 months view says so when the Player has no Rating update in that span
- [x] Dots are drawn only when the visible span has 40 Rating updates or fewer
- [x] Tapping or dragging selects the nearest Rating update; a callout shows its date, name, Rating ±Deviation and change
- [x] New texts in English, German and Austrian German on both platforms
- [x] Android and iOS ViewModel tests: the 12 months window, the empty 12 months message, the 40-point limit for dots
- [x] Previews of the callout in light and dark
- [x] `./gradlew check` and the iOS build pass

## Comments
- 2026-11-07: The window, the no-recent message and the 40-dot rule live in the section's UI model (tested through the ViewModels with a fixed clock); the touched Rating update is screen-local state, since it is transient. The callout sits under the chart instead of floating over it, so it never covers the line and reads as plain text. New design-system components: `BatSegmentedControl` / `BatSegmentedControlView`; `BatRatingChart` gained `showDots`, `selectedIndex` and `onSelect`.
