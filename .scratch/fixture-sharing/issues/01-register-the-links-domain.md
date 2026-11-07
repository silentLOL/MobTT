---
status: ready-for-human
blocked_by: []
---

# 01: Register the links domain

**What to build:** nothing in code. Fixture links need a domain we own, served over HTTPS (ADR-0011). Until it exists, the app uses the placeholder host `links.mobtt.example`.

**Blocked by:** None (can start immediately)

- [ ] Choose and register the domain (for example `mobtt.at` or `links.mobtt.at`)
- [ ] Set up static hosting with HTTPS on it (GitHub Pages, Netlify or similar)
- [ ] Replace `links.mobtt.example` with the real host in `AppConfig`, the Android manifest placeholder and the iOS Associated Domains entitlement
- [ ] Name the domain in ADR-0011 and set its status to Accepted
