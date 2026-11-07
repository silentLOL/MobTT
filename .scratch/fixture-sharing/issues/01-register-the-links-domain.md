---
status: ready-for-human
blocked_by: []
---

# 01: Register the links domain

**What to build:** no code. Fixture links need a domain we own, served over HTTPS (ADR-0011). Until then `AppConfig.linksHost` is null: Fixtures are shared without a link or QR code, and the app's link filters carry the never-verifying placeholder `links.mobtt.example`. Setting the real host switches links and QR codes on.

**Blocked by:** None (can start immediately)

- [ ] Choose and register the domain (for example `mobtt.at` or `links.mobtt.at`)
- [ ] Set up static hosting with HTTPS on it (GitHub Pages, Netlify or similar)
- [ ] Set `AppConfig.linksHost` to the host
- [ ] Replace `links.mobtt.example` with the host in `manifestPlaceholders["linksHost"]` (androidApp/build.gradle.kts) and `LINKS_HOST` (iosApp/Configuration/Config.xcconfig)
- [ ] Update the test placeholder hosts only if you want them to read like the real one (tests pass hosts explicitly, so this is optional)
- [ ] Name the domain in ADR-0011 and set its status to Accepted
- [ ] Share a Fixture in a `deb` build: the text ends with the link and the image has a QR code

## Comments

- 2026-11-07: The domain can come after the store launch. Sharing works without it; see ADR-0011's update of today.
