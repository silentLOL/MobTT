# Review notes

- fixed: Duplicate-request race in `XttvApi` (in-flight slot released before the cache write). The owner now caches first and a new owner re-checks the cache; a 40-round multi-threaded test covers it.
- fixed: A stale entry served after a failed refresh was retried on every read. It now backs off for at most a minute, with a test.
- fixed: No logging of refresh failures (CLAUDE.md §14). Kermit logs the URL without its query string.
- fixed: Unused `koin-core` dependency in `:core:xttv`.
- accepted: De-duplication lives in `XttvApi`, not the client factory; ADR-0007 and the spec were updated to match.
- deferred: Recorded-XML harness in `commonTest` resources. Ticket 02 adds the first real mapper and the first recorded XML.
- deferred: Real `User-Agent` supplier (app name and version in `AppConfig`). Ticket 02 wires the client into DI.
- deferred: `DataUnavailable` in `:domain:club` is awkward for `:domain:player`. Move it when ticket 09 needs it there.
- deferred: An `XttvEndpoint<T>` type for url, ttl and serializer, and a serializer-per-URL guard on the cache. Revisit when the second repository repeats the triple.
- deferred: Possibly timing-dependent concurrency-cap test, and no test for the owner-cancelled take-over path.
