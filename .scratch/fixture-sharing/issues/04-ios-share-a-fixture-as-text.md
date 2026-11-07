---
status: resolved
blocked_by: [2, 3]
---

# 04: iOS: share a Fixture as text

**What to build:** the same Share button and share text on iOS, through the system share sheet, using the shared Fixture link from 03 and the same strings and analytics event.

**Blocked by:** 02, 03

- [x] The Share button sits in `BatHeroCardView`'s trailing slot when `fixtureSharingEnabled` is on: `square.and.arrow.up`, 44pt touch target, accessibility label "Share Fixture"
- [x] Tapping it logs `fixture_share_tapped` with the status and presents the system share sheet with the subject and the text (`FixtureDetailViewModelTests`)
- [x] The share text matches Android's for the same Fixture, in English and German (Swift Testing)
- [x] New strings are in the `.xcstrings` catalogue in English and German
- [x] The iOS build and tests pass

## Comments

- 2026-11-07: `FixtureShare.content` builds the text from `FixtureDetailUi`, looking strings up in the locale's `.lproj` so tests can check German; `ShareSheetView` (Common/Share) wraps `UIActivityViewController` with the pairing as subject. `KoinHelper` also gained `fixtureIdOfLink` for 06. The new `.xcstrings` keys were pushed to Loco with 03's.
