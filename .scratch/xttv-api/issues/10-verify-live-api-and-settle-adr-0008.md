# 10: Verify against the live API and settle ADR-0008

**What to build:** Replace the hand-written sample XML with real XTTV responses and confirm the assumptions the earlier tickets rest on: the host layout, how IDs are unique, authentication and acceptable request rate. This needs registration with XTTV, which only the project owner can do.

**Blocked by:** XTTV's reply to the registration email, and 06 League standings from XTTV.

**Status:** ready-for-human

**Progress:** Registration email sent to XTTV on 2026-10-18 (asks the ADR-0008 questions: access, base URLs, schemas, ID uniqueness, date format, staging, request rate). Waiting for the reply; tickets 09 and 10 resume then.

- [ ] The project owner has registered with XTTV and has the base URL(s), authentication details and schemas.
- [ ] Recorded XML in the tests is replaced by real responses (with personal data removed if present) and all repository tests pass.
- [ ] One manual run on a `dev` build shows real Clubs, Teams, Fixtures and standings on both platforms.
- [ ] ADR-0008 is set to Accepted, or superseded if the host layout differs or IDs collide across Associations.
- [ ] If IDs collide, the composite ID format and a Favorites migration are specified before anything ships to `prod`.
- [ ] The request rate XTTV allows is checked against the TTLs and concurrency cap, and they are adjusted if needed.
- [ ] Favorites persistence on XTTV IDs is cleared to ship, or held back.
