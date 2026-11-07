# ADR-0011: Fixture links on our own domain, self-hosted

**Status:** Proposed (accepted once the links domain is registered and named here)
**Date:** 2026-11-07
**Deciders:** Stefan Irndorfer

## Context
The Fixture screen gets a Share button, and sharing should also advertise MobTT: a shared Fixture link should open that Fixture in the app when it is installed, and lead to the app stores when it is not.

Forces:
- Only an https URL on a domain we control can do both. Android App Links and iOS Universal Links open the app when the domain hosts `assetlinks.json` and `apple-app-site-association`; without the app, the same URL opens in the browser. A custom scheme (`mobtt://`) does nothing without the app, and most messengers do not make it tappable.
- Firebase Dynamic Links, the ready-made service for this, was shut down in August 2025. The remaining link services (Branch, AppsFlyer, Adjust) are paid and ship an SDK that would have to sit behind our opt-in Usage Consent (ADR-0009).
- Links sent into chats live for years, so the domain and the URL shape become a public contract.
- MobTT is not yet listed on Google Play or the App Store.
- XTTV has its own public page per Fixture (`https://oettv.xttv.at/ed/index.php?meid=<Fixture id>`), but linking there sends people to XTTV, not to us, and it only has a report once the Fixture is played.

## Decision
We will serve Fixture links as `https://<links-domain>/f/<Fixture id>` from a dedicated domain that we host ourselves as a static site. The site carries the association files for the `prod`, `beta` and `dev` builds and one page for browsers: MobTT's logo and pitch, both store badges, and a link to the Fixture's XTTV page. It does not redirect on its own. The prefixes `/t/`, `/p/`, `/c/` and `/l/` are reserved for Team, Player, Club and League links. Building and reading Fixture links is one piece of shared Kotlin, with the host from `AppConfig`.

## Options Considered

### Option A: Own domain, self-hosted static site — chosen
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Low: two JSON files and one HTML page |
| Cost             | The domain; free static hosting |
| Scalability      | New link kinds need a prefix and an app route, no server |
| Team familiarity | Medium: App Links and Universal Links are new to the project |

**Pros:** No SDK, no tracking, no running cost; we own the URLs for as long as we keep the domain.
**Cons:** No install attribution; no deferred deep link (a fresh install starts at onboarding); the browser page cannot show the Fixture's data, because that would need the XTTV API key in the browser.

### Option B: A link service (Branch, AppsFlyer, Adjust)
| Dimension        | Assessment |
|------------------|------------|
| Complexity       | Medium: an SDK on both platforms, inside the consent flow |
| Cost             | Paid above small volumes |
| Scalability      | High |
| Team familiarity | Low |

**Pros:** Install attribution, deferred deep links, hosted fallback pages.
**Cons:** A tracking SDK under ADR-0009's opt-in rules; links depend on a vendor staying in business.

### Option C: Custom URL scheme
**Pros:** No domain. **Cons:** Dead for anyone without the app and not tappable in most messengers, which defeats the advertising purpose.

### Option D: Link to XTTV's Fixture page
**Pros:** Works today with nothing to build. **Cons:** Advertises XTTV, not MobTT; empty for Scheduled Fixtures.

## Trade-off Analysis
The point of the links is to bring people into MobTT. Option A does that for anyone who already has the app and sends everyone else to the stores, at no running cost and without adding tracking. What it gives up, install attribution and deferred deep links, matters only once there is a marketing budget to measure; the `fixture_share_tapped` and `fixture_link_opened` events cover the basic question of whether sharing brings people back.

## Consequences
- The domain must stay registered for as long as shared links should work. Losing it breaks every link ever sent and could hand the URLs to someone else.
- The URL shape is fixed: `/f/<Fixture id>` with plain XTTV IDs (ADR-0008). Changing it later means keeping the old shape working on the site and in the app.
- Each new signing key or bundle ID must be added to the association files before its build can open links. `deb` builds are verified by hand on a developer's device.
- Sharing stays switched off by an `AppConfig` flag until the store listings are public, so no shared link leads nowhere.
- Revisit Option B if install attribution or deferred deep links become a requirement.
