# Google Play Console checklist (MobTT, Android)

Answers and values for the first release, derived from the code. Split into what the repo already holds and what must be done by hand in Play Console. Signing and the build are in [`RELEASE_ANDROID.md`](RELEASE_ANDROID.md); texts and graphics in [`store/google-play/`](../store/google-play/README.md). Check Play Console's wording against these answers when filling the forms: Google renames questions from time to time.

## 1. Done in the repo

| Item | Where |
|---|---|
| Release signing from untracked settings, unsigned fallback | `androidApp/build.gradle.kts`, [`RELEASE_ANDROID.md`](RELEASE_ANDROID.md) |
| R8 minify and resource shrinking on release, run on an emulator | `androidApp/build.gradle.kts` |
| `prod` AAB builds reproducibly (`bundleProdRelease`), `versionCode`/`versionName` overridable per build | [`RELEASE_ANDROID.md`](RELEASE_ANDROID.md) |
| Title, short and full description, release notes in `en-US` and `de-DE` | `store/google-play/<locale>/*.txt` |
| App icon 512x512, feature graphic 1024x500, 5 phone screenshots per language | `store/google-play/` |
| Privacy policy drafts (EN and DE) to publish on a web page | `store/google-play/privacy-policy.*.md` |
| `targetSdk` 37 (`gradle/libs.versions.toml`), `minSdk` 24 | |

## 2. By hand in Play Console (cannot be done before the account is back)

1. **Account**: finish Google support re-activation. Complete identity verification and the developer profile. Contact email and, for a personal account, address are shown on the store page.
2. **Create app**: name `MobTT: Table Tennis Results` (default language English, United States; add German), type App, free, accept the declarations. Choose **Play App Signing** with a Google-generated key. Application ID is fixed by the first upload: `at.stefanirndorfer.mobtt`.
3. **Publish the privacy policy** on a public URL (section 4), then enter the URL under App content.
4. **App content** forms: section 3 below.
5. **Store listing**: copy texts and upload graphics per language from `store/google-play/`. Add the German translation as a custom listing language.
6. **Upload key and AAB**: [`RELEASE_ANDROID.md`](RELEASE_ANDROID.md). Upload to Internal testing first, install through the opt-in link, tap through the app.
7. **Testing requirement**: a personal developer account created after 13 Nov 2023 must run a closed test with a set number of testers for 14 days before it may apply for production. This account dates from before that and may be exempt; the Console's Dashboard shows which applies. If it applies, recruit the testers early.
8. **Release**: Production track, release name `1.0`, release notes from `release-notes.txt`, countries: Austria and the German-speaking region at minimum (pick all, if you like). Send for review.

## 3. App content answers

### App category and contact

- Category: **Sports**. Tags: Sports and fitness is the closest; add table tennis if offered.
- Contact: email (public), website (the privacy policy page or a project page), phone optional.

### Content rating (IARC questionnaire)

Category "Reference, news or educational" does not fit; choose **All other app types** (utility/sports data). Answer **No** to everything: violence, sexual content, profanity, controlled substances, gambling or simulated gambling, user-generated content, user-to-user communication, sharing of the user's location, digital purchases, and unrestricted web access. Expected result: Everyone / PEGI 3 / USK 0.

### Target audience

Not directed at children. Select **18 and over** for the simplest path, or 13 and over if the club's youth players are meant to use it; do **not** select any age under 13, which would pull in the Families Policy. Appeals to children: No.

### Ads, access and other declarations

- Ads: **No** (no ad SDK; `AD_ID` and `ACCESS_ADSERVICES_AD_ID` are removed from the manifest).
- Advertising ID: **No**.
- App access: all functionality is available without login or special access.
- Government app, financial features, health features, news app, COVID-19: **No**.
- Data deletion: no accounts, so no account deletion URL.

### Data safety

Basis: `docs/ANALYTICS.md`, ADR-0009, `AndroidManifest.xml`. MobTT has no accounts. My Player, My Club, Favorites and the appearance setting are stored on the device (DataStore) and never leave it.

**Collected, and only after the user taps Allow on the consent step (or turns the Settings toggle on):**

| Play data type | Why | Shared | Optional |
|---|---|---|---|
| App activity > App interactions | Analytics (Firebase Analytics events from the catalog) | No | Yes |
| App info and performance > Crash logs | App functionality, Analytics (Crashlytics) | No | Yes |
| App info and performance > Diagnostics | Analytics (Crashlytics, device state at crash) | No | Yes |
| Device or other IDs | Analytics (Firebase app instance ID, Crashlytics installation ID) | No | Yes |

- "Shared": answer **No**. Google receives the data as the service provider for Firebase, which Play does not count as sharing.
- Data is **encrypted in transit** (all hosts are `https`).
- Deletion: users cannot request deletion of analytics data tied to an anonymous app instance ID. Answer "No" to a deletion mechanism, or state that data is anonymous and removed by Firebase's retention limits.
- Not collected: name, email, location, contacts, photos, audio, files, financial or health data, advertising ID, free text (search text is never sent, only its length).
- Network use that is not "collection": the app requests public Club, Player and League data from `oettv.xttv.at` (with the app's XTTV API key and a `User-Agent` carrying the app version) and a Player's Rating history from `www.ratingscentral.com` (the Ratings Central ID of the opened Player). Neither request carries a user identifier. Both servers see the device's IP address like any web server; that is the normal operation of a network app and not a declared type under Play's guidance, but it is named in the privacy policy.

> **Ids dropped:** `favorite_toggled` and `detail_opened` no longer send the id of the Player, Club or Team on Android, so the consent text ("never include ... your Player or Club") holds. iOS still sends it until it follows; drop it there before the iOS release (`docs/ANALYTICS.md`).

## 4. Privacy policy

Required: the app collects device IDs and crash data through Firebase. Needs a public, non-PDF, always-reachable HTTPS page, linked in Play Console and, ideally, in the app (Settings > About has no row for it yet; the policy URL is not wired into the app). Drafts: `store/google-play/privacy-policy.en.md` and `.de.md`; fill the placeholders (responsible person, address, email), have them read once, publish them, and keep them in sync with `docs/ANALYTICS.md`. Austrian law also expects an **Impressum** on a website you operate.

## 5. Pre-launch checks

| Check | Status |
|---|---|
| `targetSdk` 37, above Play's required level (check the policy page for the current minimum before each release) | ok |
| 64-bit: `arm64-v8a` and `x86_64` present for every native library | ok |
| 16 KB page-size alignment (`zipalign -c -P 16`) | ok |
| R8 on, mapping file in the AAB, Crashlytics mapping uploaded | ok, smoke-tested `prod` APK on an emulator |
| Permissions: `INTERNET`, `ACCESS_NETWORK_STATE`, `WAKE_LOCK`, install referrer and ad-services attribution entries added by Firebase, no dangerous permissions | ok, nothing to justify in the Console |
| No ad ID | ok, removed in the manifest |
| Fixture links: `linksHost` is the placeholder `links.mobtt.example`, so `autoVerify` never succeeds and no link opens the app. Harmless; sharing is off in `prod` (`AppConfig.fixtureSharingEnabled`). Set the real host with ADR-0011 once the domain exists. | open, not a blocker |
| App signing and upload key set up and backed up | by hand |
| Play's pre-launch report on the internal track: read it for crashes and accessibility findings | by hand |
| Real `prod` `google-services.json` and XTTV API key present in the build that is uploaded | by hand, check the first install shows data and consent |
| Ratings Central agreement is recorded in ADR-0010 and the flag is on | ok |

## 6. Release notes and version

First release: `versionCode` 1, `versionName` 1.0. Notes are in `store/google-play/en-US/release-notes.txt` and `de-DE/release-notes.txt` (500 characters max per language). Raise `versionCode` for every upload.
