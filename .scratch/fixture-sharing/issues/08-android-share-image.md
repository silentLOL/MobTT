---
status: ready-for-agent
blocked_by: [3]
---

# 08: Android: share image

**What to build:** on Android, sharing a Fixture also attaches an image of it in MobTT's style, and the text shrinks to a caption (spec: "Share image").

**Blocked by:** 03

- [ ] The image is drawn from Compose in the light theme at a fixed width: the red card without the Share button, every Match in one image, and a footer with the MobTT logo, "Get MobTT" and a QR code of the Fixture link generated on the device
- [ ] It is shared as a PNG through a `FileProvider` content URI, with the caption kept as `EXTRA_TEXT`
- [ ] The caption is the pairing, the score or "Not played yet" with the kickoff, and the Fixture link; the subject stays the pairing (test against string resources)
- [ ] Roborazzi screenshots of the image for a Scheduled Fixture, a Finished one and one with 16 Matches
- [ ] Manual check: the QR code scans from a shared image; WhatsApp shows the image with its caption
- [ ] `./gradlew check` passes
