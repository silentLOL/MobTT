---
status: resolved
blocked_by: [2]
---

# 03: Android: share a Fixture as text

**What to build:** on Android, the Fixture's red card has a Share button that opens the system share sheet with everything the Fixture screen shows, as plain text in the app's language, ending with the Fixture link. See the spec's "Share button" and "Fixture link (shared)" decisions for the text layout and the link's rules.

**Blocked by:** 02

- [x] Shared Kotlin builds a Fixture link from a Fixture id (`https://<links host>/f/<id>`) and reads a Fixture id back from a URL; another host, a reserved prefix (`/t/ /p/ /c/ /l/`), a missing or empty id, extra segments and non-https URLs read as no Fixture link (`commonTest`)
- [x] `AppConfig` carries the links host (placeholder `links.mobtt.example`, see 01) and `fixtureSharingEnabled`: on for `deb` and `dev`, off for `beta` and `prod` until 10
- [x] The Share button sits in the hero card's trailing slot for Scheduled and Finished Fixtures when sharing is enabled: platform share icon, `onHero` colour, 48dp touch target, announced as "Share Fixture"
- [x] Tapping it logs `fixture_share_tapped` with `status` `scheduled` or `finished` (catalog row in `docs/ANALYTICS.md` first) and sends a one-off event with the `FixtureDetail` and its Fixture link (`FixtureDetailViewModelTest`)
- [x] The Screen opens an `ACTION_SEND` chooser through a private `Context` extension, with the pairing as `EXTRA_SUBJECT` and the share text as `EXTRA_TEXT`
- [x] The share text matches the spec's layout in English and German: Scheduled, Finished with Singles and Doubles, a Match without a result, Finished without Matches, with and without venue and address (Robolectric test against real string resources)
- [x] New strings are added in English and German
- [ ] New strings pushed to Loco (waiting for the go-ahead: `loco.sh push` overwrites Loco's text for every key)
- [x] `./gradlew check` passes

## Comments

- 2026-11-07: `FixtureLinks` (club domain) builds and reads links and carries `sharingEnabled`; `:core:di` binds it from `AppConfig.linksHost` and `fixtureSharingEnabled`. The share text lives in `FixtureShare.kt` as a `Context` extension, tested with Robolectric at SDK 33 (the compile SDK does not start on this JDK). `fixture_detail_share_with_result` is quoted in `strings.xml` so its two spaces survive; check it after the next Loco pull. The Share button uses `BatIconButton`, like the Favorite star.
