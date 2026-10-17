# 01: XTTV client foundation

**What to build:** The shared plumbing every XTTV-backed repository will use, so that a failing or slow XTTV is handled politely and predictably. It covers the new `:core:xttv` module, a client that identifies the app and limits its own request rate, an in-memory cache that shields XTTV and keeps the app useful when a refresh fails, a domain error for "XTTV unavailable", and the recorded-XML test harness. See ADR-0007.

**Blocked by:** None (can start immediately).

**Status:** ready-for-agent

- [ ] `:core:xttv` exists, applies the shared KMP library plugin and depends on the domain interfaces, not the other way round.
- [ ] Every request carries a descriptive `User-Agent` naming the app and version.
- [ ] Identical concurrent requests are collapsed into one; no more than 2 requests run in parallel.
- [ ] A TTL cache serves repeat reads without a request and serves a stale entry when a refresh fails.
- [ ] A failed request with no cached entry throws `DataUnavailable`, defined in the domain; empty results are never turned into errors.
- [ ] XML parsing ignores unknown elements, shown by a test with an extra element.
- [ ] A `MockEngine` harness serves recorded XML from `commonTest` resources, and no test touches the network.
- [ ] `./gradlew check` passes.
