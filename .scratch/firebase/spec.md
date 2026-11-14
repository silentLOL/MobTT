---
---

# Spec: Firebase Crashlytics and Analytics

## Problem Statement

As the people building MobTT, we cannot see crashes in the field or how Players and fans use the app. A crash on a user's phone is invisible to us until someone reports it, and we have no evidence about which screens, selections and Favorites matter. MobTT's users are in Austria, so any collection must respect GDPR, and the app has no ads, so nothing advertising-related should be collected.

## Solution

Crashlytics runs on both platforms in all four environments (`deb`, `dev`, `beta`, `prod`), and Firebase Analytics in `dev`, `beta` and `prod` only. `deb` sends no Analytics events. Both only run after the user has given Usage Consent. A one-time consent step follows the first-run Player or Club choice (and appears once for existing installs on their next launch), and Settings offers a toggle afterwards. Until the user answers, or after they decline, nothing is collected, crashes included. A curated set of interaction events is sent to Analytics, and errors from shared Kotlin code reach Crashlytics through the existing Kermit logging. Events carry only IDs and enum values, never names, free text or a user ID. See ADR-0009.

## User Stories

1. As a user opening MobTT for the first time, I want to be asked clearly whether anonymous statistics and crash reports may be collected, so that I decide before anything is sent.
2. As a user, I want the consent question to appear right after I choose my Player or Club, so that it fits the first-run flow.
3. As a user, I want "Not now" to be a real answer, so that I am never nagged again.
4. As a user, I want nothing collected while I have not answered, so that my privacy is respected by default.
5. As a user, I want nothing collected after I decline, so that my choice is honoured, including for crashes.
6. As an existing user who updates the app, I want to be asked once on my next launch, so that the new collection never starts without my say.
7. As a user, I want a toggle in Settings, so that I can change my mind at any time.
8. As a user who turns the Settings toggle off, I want collection to stop immediately, so that I do not have to restart the app.
9. As a user who turns the toggle on, I want collection to start without reinstalling, so that I can help later.
10. As a user, I want the consent text and toggle in my language, so that I understand what I agree to.
11. As a user relying on VoiceOver or TalkBack, I want the consent step and toggle to be fully accessible, so that I can answer like anyone else.
12. As a user, I want no advertising ID collected and no tracking-permission prompt on iOS, so that the app asks me nothing it does not need.
13. As a user, I want my Player, Club and search text never sent, so that the statistics stay anonymous.
14. As a developer, I want crashes reported with readable stack traces on Android, so that I can fix them.
15. As a developer, I want unhandled Kotlin exceptions on iOS to show Kotlin stack traces, so that I can fix shared-code crashes.
16. As a developer, I want `Error` logs with a throwable in shared code reported as non-fatal issues, so that handled failures such as XTTV outages are visible.
17. As a developer, I want lower-severity shared logs attached as breadcrumbs, so that I can see what led to a crash.
18. As a developer, I want logs never to contain tokens or the XTTV API key, so that secrets do not reach Crashlytics.
19. As a product owner, I want a screen view event for each screen, so that I know which screens are used.
20. As a product owner, I want to know when users select My Player or My Club and from where, so that I can judge the first-run flow.
21. As a product owner, I want to know when Favorites are added or removed, by kind, so that I can see what people follow.
22. As a product owner, I want to know when Player, Team, League and Fixture details are opened, and from which screen, so that I can see how people navigate.
23. As a product owner, I want to know how often search is used and the query length, never the text, so that I can judge search without storing content.
24. As a product owner, I want to know when consent is changed and from where, so that I can judge the consent step.
25. As a product owner, I want to know which settings people change, so that I can see what matters to them.
26. As a product owner, I want data from `dev`, `beta` and `prod` to be separate, so that prod numbers are not mixed with testing.
27. As a developer, I want `deb` builds to report crashes to their own Firebase app but send no Analytics events, so that development never pollutes usage numbers while crashes stay visible.
28. As a developer, I want the app to build without the Firebase config files, so that a contributor without Firebase access can still run it.
29. As a developer, I want one event catalog document used by both platforms, so that names never drift apart.
30. As a developer, I want a convention check to flag event names missing from the catalog, so that the catalog stays true.
31. As a developer, I want ViewModels to depend on interfaces and not the Firebase SDK, so that I can unit test them with a recording fake.
32. As a developer, I want previews and tests to use a no-op tracker, so that they never touch Firebase.
33. As a developer, I want automatic screen reporting off, so that screen views are not counted twice.
34. As a maintainer, I want the Firebase setup documented per environment, so that the next person can add an app or rotate a config.

## Implementation Decisions

- **Native SDKs behind our own interfaces**, one integration per platform; no KMP Firebase wrapper (ADR-0009). Xcode keeps linking only the shared framework for Kotlin; Firebase comes into the iOS app through Swift Package Manager.
- **Usage Consent** is a domain concept with three states: unanswered, allowed, declined. It lives in the shared user domain and is persisted through the preferences module, like other user preferences (ADR-0004). It is independent of My Player, My Club and Favorites. "Unanswered" and "declined" are stored distinctly so the question is asked exactly once.
- **Consent UX:** one step after the first-run Player or Club choice, with Allow and Not now; shown once to existing installs that finished first run and have not answered; a Settings toggle afterwards. A consent change takes effect at once without a restart.
- **Nothing is collected until allowed.** Firebase automatic collection is off in each platform's configuration; the platform turns collection on and off from the Usage Consent state at launch and whenever it changes. This covers both Analytics and Crashlytics, so crashes before an answer are not reported.
- **Android:** a new pure-Android core module holds an `AnalyticsTracker` interface, a `CrashReporter` interface, the Firebase implementation, a no-op fake and a Koin module. Feature modules depend on the interfaces only. Registered in the central Koin orchestration per the usual feature workflow.
- **iOS:** an analytics area under the app's Common folder holds the equivalent Swift protocols, the Firebase implementation and a no-op, injected into the Swift ViewModels.
- **Firebase layout:** one Firebase project with a separate app for each of `deb`, `dev`, `beta` and `prod` on each platform (eight apps), reusing the existing application IDs and bundle IDs (docs/BUILD_VARIANTS.md). Crashlytics collection is enabled in all four environments and Analytics collection in `dev`, `beta` and `prod`, all subject to consent. In `deb` the Analytics tracker is a no-op, so no events are sent, and Analytics is switched off in the SDK. Events are therefore verified with DebugView in `dev`.
- **Config files** (`google-services.json`, `GoogleService-Info.plist`, one per environment) are untracked with `.example` templates, following the XTTV key pattern (ADR-0006). The Google Services and Crashlytics Gradle plugins, and the iOS Firebase initialisation, apply only when the file is present; otherwise the SDK stays disabled and the build still succeeds. The Crashlytics symbol and dSYM upload runs for release-style builds and the non-debug iOS configurations.
- **No advertising identifiers:** the Android `AD_ID` permission is removed and ad personalization disabled; iOS links the SDK without the AdSupport variant and adds no tracking-permission prompt.
- **Privacy rules for events:** no user ID is set; parameters are IDs and enum values only; no names or free text.
- **Event catalog** (single source of truth in `docs/ANALYTICS.md`, snake_case names): `screen_view`, `my_player_selected`, `my_club_selected`, `favorite_toggled`, `detail_opened`, `search_performed`, `consent_changed`, `setting_changed`. Parameters: `screen`; `source`; `kind`, `id`, `enabled`; `kind`, `id`, `from`; `screen`, `query_length`; `enabled`, `source`; `setting`, `value`. Automatic Firebase screen reporting is off.
- **Where events fire:** from the action handler of each Android ViewModel (§11 UDF) and the matching Swift ViewModel method, never from composables or views and never from shared code.
- **Crashlytics from shared Kotlin:** each platform supplies a Kermit `LogWriter` through the platform part of Koin start-up. `Error` severity with a throwable is recorded as a non-fatal; lower severities are breadcrumbs. On iOS, unhandled Kotlin exceptions are also forwarded so they show Kotlin frames. Logged URLs and exception messages must not contain tokens or the XTTV API key (CLAUDE.md §14).
- **Convention check:** the existing convention script flags an event name used in code that the catalog does not list.
- **Delivery:** five tickets in `issues/`: Usage Consent; Android foundation; iOS foundation; Crashlytics from shared code; event catalog and tracking.

## Testing Decisions

- A good test checks external behaviour: a user action produces the expected tracker calls, or a consent transition produces the expected state. It does not assert on private structure or on Firebase internals.
- **Seam 1, the ViewModel boundary:** every Android feature ViewModel and Swift ViewModel is tested with a recording tracker fake. Tests assert the catalog events and parameters, that no event carries a name or free text, and the consent step and Settings toggle behaviour. Prior art: `HomeViewModelTest` on Android and `SettingsViewModelTests` (Swift Testing) on iOS.
- **Seam 2, Usage Consent in the shared user domain:** tests cover the transitions (unanswered to allowed or declined, a later toggle, an existing install asked once, "Not now" never asked again) and persistence. Prior art: `DataStoreUserSelectionRepositoryTest` and `FakeSelectionRepository` in `domain/user`.
- **One small security test:** the Kermit `LogWriter` never emits the XTTV key or tokens.
- **Not automated:** the Firebase SDK and the consent-to-SDK switch in each thin platform adapter. These are verified by hand with DebugView in `dev`, and by checking that a build without config files runs with the SDK disabled.
- `./gradlew check` and the iOS build must pass for every ticket.

## Out of Scope

- Separate Firebase projects per environment.
- Any Firebase product beyond Crashlytics and Analytics (Remote Config, Performance, Messaging, Auth).
- Tracking every tap, or automatic event collection.
- Setting a user ID, user properties, or sending names or free text.
- Advertising features, attribution, and the iOS tracking-permission prompt.
- A KMP Firebase wrapper or a shared analytics abstraction in Kotlin.
- CI setup for config files and symbol upload (no CI exists yet).
- Dashboards, funnels and Analytics console configuration.
- Per-event consent granularity (consent is a single choice for Analytics and Crashlytics together).

## Further Notes

- The decisions behind this spec are in ADR-0009 (Proposed), and the new glossary term is Usage Consent.
- Privacy wording for the consent step and a possible privacy policy entry need review outside the code before release.
- The five tickets are ordered by dependency: 01 first; 02 and 03 can run in parallel; 04 and 05 need both.
