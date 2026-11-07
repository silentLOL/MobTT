---
status: ready-for-agent
blocked_by: [4, 8]
---

# 09: iOS: share image

**What to build:** the same image and caption on iOS, matching 08's layout.

**Blocked by:** 04, 08

- [ ] The image is rendered from SwiftUI (`ImageRenderer`) in the light appearance with the same content and footer as on Android; the QR code comes from Core Image
- [ ] The share sheet carries the image and the caption
- [ ] The caption matches Android's (Swift Testing)
- [ ] Previews of the image for Scheduled, Finished and 16 Matches
- [ ] The iOS build and tests pass
