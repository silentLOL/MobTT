---
status: resolved
blocked_by: [3]
---

# 08: Android: share image

**What to build:** on Android, sharing a Fixture also attaches an image of it in MobTT's style, and the text shrinks to a caption (spec: "Share image").

**Blocked by:** 03

- [x] The image is drawn from Compose in the light theme at a fixed width: the red card without the Share button, every Match in one image, and a footer with the MobTT logo, "Get MobTT" and a QR code of the Fixture link generated on the device
- [x] It is shared as a PNG through a `FileProvider` content URI, with the caption kept as `EXTRA_TEXT`
- [x] The caption is the pairing, the score or "Not played yet" with the kickoff, and the Fixture link; the subject stays the pairing (test against string resources)
- [x] Roborazzi screenshots of the image for a Scheduled Fixture, a Finished one and one with 16 Matches
- [x] Manual check: the QR code in a shared image decodes to the Fixture link (Core Image), and the share sheet offers the image with its caption
- [ ] Manual check in WhatsApp (not installed on the emulator)
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: The route composes `FixtureShareImage` out of sight in the light theme and records it into a `GraphicsLayer`; the PNG goes to `cache/fixture_share/` behind a `FileProvider` (`<applicationId>.fixtureshare`) declared in the club feature. If drawing or saving fails, the full text is shared instead, and the failure is logged. Added `com.google.zxing:core` 3.5.3 for the QR code. On the emulator, Hagenberg 3 – Kefermarkt 4 gave a 1080 × 3231 image with all 10 Matches, and its QR code decoded to the Fixture link.
