---
status: ready-for-agent
blocked_by: [3]
---

# 05: 12 months / All, and the details of one Rating update

**What to build:** the user chooses between the last 12 months and the whole Rating history, and touches the chart to read one Rating update's date, name, Rating ±Deviation and change. See the spec's "Chart" and user stories 21 to 29.

**Blocked by:** 03

- [ ] A 12 months / All segmented control from each design system, starting on 12 months, held per screen and not stored
- [ ] The 12 months view says so when the Player has no Rating update in that span
- [ ] Dots are drawn only when the visible span has 40 Rating updates or fewer
- [ ] Tapping or dragging selects the nearest Rating update; a callout shows its date, name, Rating ±Deviation and change
- [ ] New texts in English, German and Austrian German on both platforms
- [ ] Android and iOS ViewModel tests: the 12 months window, the empty 12 months message, the 40-point limit for dots
- [ ] Previews of the callout in light and dark
- [ ] `./gradlew check` and the iOS build pass

## Comments
