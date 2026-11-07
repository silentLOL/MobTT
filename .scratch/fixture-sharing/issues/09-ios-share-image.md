---
status: resolved
blocked_by: [4, 8]
---

# 09: iOS: share image

**What to build:** the same image and caption on iOS, matching 08's layout.

**Blocked by:** 04, 08

- [x] The image is rendered from SwiftUI (`ImageRenderer`) in the light appearance with the same content and footer as on Android; the QR code comes from Core Image
- [x] The share sheet carries the image and the caption
- [x] The caption matches Android's (Swift Testing)
- [x] Previews of the image for Scheduled, Finished and 16 Matches
- [x] The iOS build and tests pass

## Comments

- 2026-11-07: `FixtureShareImageView.render` draws the image with `ImageRenderer` at the screen scale in the light appearance; the QR code comes from Core Image. The route shares the picture with the caption, or the full text if the picture cannot be drawn. A Swift test renders it (width 360 pt, taller with Matches); the rendered image matched Android's layout and its QR code decoded to the Fixture link. Not tried in a real share sheet on a device.
