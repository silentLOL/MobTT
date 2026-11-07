---
status: ready-for-human
blocked_by: [1, 5, 6]
---

# 07: Links site live, verified end to end

**What to build:** the static site for the links domain (ADR-0011), deployed, so a Fixture link tapped in a messenger opens MobTT when installed and MobTT's page otherwise.

**Blocked by:** 01, 05, 06

- [ ] `/.well-known/assetlinks.json` lists `prod`, `beta` and `dev` with their release signing SHA-256 fingerprints (needs the fingerprints from the keystore owner)
- [ ] `/.well-known/apple-app-site-association` lists the matching bundle IDs for `/f/*`
- [ ] Every `/f/<id>` serves one page: logo, one-line pitch, both store badges, and "View this Fixture on XTTV" linking to `https://oettv.xttv.at/ed/index.php?meid=<id>`; no automatic redirect, no analytics, no cookies; a generic `og:` preview
- [ ] Deployed to the links domain, with the store badges linking to the real listings once those are public
- [ ] Checked: Google's Digital Asset Links tester, Apple's AASA validator, `adb shell pm get-app-links` for each flavor
- [ ] Checked by hand: a link tapped in WhatsApp opens the Fixture in the app on Android and iOS, and opens the page on a phone without the app and on a desktop
