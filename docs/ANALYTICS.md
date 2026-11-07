# Analytics event catalog

The single source of truth for the events both apps send to Firebase Analytics (ADR-0009). Android and iOS use these exact names and parameters. `scripts/check-conventions.sh` fails when code names an event or parameter this file does not list, so add a row here first.

## Rules

- Names and parameter keys are `snake_case`.
- Parameters carry IDs and enum values only. Never a user ID, a name, or free text. `search_performed` sends the query *length*, never the text.
- Events fire from a ViewModel's `onAction` handler (Android) or the matching Swift ViewModel method (iOS), never from composables, views or shared code.
- Nothing is sent before Usage Consent is allowed, and nothing is sent from `deb` (its tracker is a no-op).
- Automatic Firebase screen reporting is off, so `screen_view` is sent by hand.
- Booleans are sent as the strings `true` and `false`, because Firebase accepts only strings and numbers.

## Values

| Parameter | Values |
|---|---|
| `screen` | `home`, `league`, `players`, `choose_player`, `choose_club`, `club`, `player_detail`, `team_detail`, `league_detail`, `fixture_detail`, `settings` |
| `kind` | `player`, `club`, `team`, `league`, `fixture` |
| `source` | `home`, `settings`, `prompt` |
| `from` | a `screen` value |
| `status` | `scheduled`, `finished` |
| `start` | `cold`, `running` |

## Events

| Event | Parameters | Sent when |
|---|---|---|
| `screen_view` | `screen` | A screen's ViewModel is created, so a screen kept alive on a tab is counted once per session. |
| `my_player_selected` | `source` (`home`, `settings`) | The user saves a Player as My Player. |
| `my_club_selected` | `source` (`home`, `settings`) | The user saves a Club as My Club. |
| `favorite_toggled` | `kind` (`player`, `club`, `team`), `id`, `enabled` | A Favorite is added (`true`) or removed (`false`). |
| `detail_opened` | `kind`, `id`, `from` | A Player, Team, League or Club screen is opened from another screen. |
| `search_performed` | `screen`, `query_length` | The user stopped typing in a search field for a moment and the query is not blank. |
| `consent_changed` | `enabled`, `source` (`prompt`, `settings`) | The user answers the consent step or flips the Settings toggle. Only an allow can reach Firebase (collection is off after a decline), and it can be lost if it is logged before collection switches on. |
| `rating_info_opened` | none | The user opens the explanation of the Rating history from its info button. |
| `fixture_share_tapped` | `status` | The user taps Share on a Fixture. Whether anything was sent from the share sheet is not known. |
| `fixture_link_opened` | `start` | The app opens a Fixture from a Fixture link, started by it (`cold`) or already running (`running`). |
| `setting_changed` | `setting`, `value` | A setting is changed. `setting` is `appearance` with `value` `system`, `light` or `dark` (Android only: iOS has no in-app setting yet). |

## Where each platform fires them

| Screen | Android | iOS |
|---|---|---|
| Home | `HomeViewModel` | `HomeViewModel` |
| League tab | `LeagueTabViewModel` | `LeagueTabViewModel` |
| Players, choose Player, choose Club | `PlayersViewModel`, `ChooseClubViewModel` | `PlayersViewModel` (by mode) |
| Club | `ClubViewModel` | `ClubViewModel` |
| Player, Team, League, Fixture detail | the matching `*DetailViewModel` | the matching `*DetailViewModel` |
| Settings | `SettingsViewModel` | `SettingsViewModel` |
| Fixture links (`fixture_link_opened` only) | `FixtureLinkViewModel` | `FixtureLinkViewModel` |
| Consent step (`consent_changed` only) | `UsageConsentViewModel` | `UsageConsentViewModel` |

## Adding tracking to a feature

Every feature that adds a screen or a user interaction decides what to track before it is done.

1. **Events.** Add a row to the Events table first (and a value to Values if needed), then fire it on both platforms from the ViewModel. Track a screen opened, a choice saved, a favorite toggled, a search. Do not track every tap, and never anything the catalog's Rules forbid (names, free text, user IDs).
2. **Logging.** Use `co.touchlab.kermit.Logger` with a tag per class. Log at the points where support would otherwise be blind: a failed load or request (`Error`, with the throwable), a retry or fallback (`Warn`), a state change worth a breadcrumb (`Info`). Not every function call, and nothing per frame or per list item.
3. **Console output.** `prod` writes nothing to logcat or OSLog. `installLogWriters` (`:core:network`) removes Kermit's default console writer there and keeps only the Crashlytics writer. Do not add `println`, `Log.*`, `NSLog` or `print` to get around it.
4. **No sensitive data.** Never log tokens, the XTTV API key, or names and free text a user typed. Log IDs and enum values. Shared code reaches Crashlytics only through the Kermit writer, which redacts secrets, but do not rely on it for personal data.
5. **Failures.** Pass the throwable to `Logger.e`. On `Error` with a throwable the Crashlytics writer records a non-fatal, so a swallowed exception still shows up in the console.
6. **Tests.** Assert the events in the ViewModel's unit test with the recording tracker.

## Firebase config files

The config files are untracked (`.gitignore` covers `google-services.json` and `GoogleService-Info-*.plist`), as with the XTTV key (ADR-0009). Commit only the `.example` templates. The Firebase client keys are not strong secrets, but they let anyone send events and crashes into our project, and keeping them out of git leaves the project switchable per developer and CI.

- **Android:** one `google-services.json` per flavor at `androidApp/src/<flavor>/`. A file covering all four apps is copied into each folder. The Gradle plugins are applied only when at least one exists.
- **iOS:** `iosApp/Firebase/GoogleService-Info-<env>.plist`, copied into the app by a build phase.
- **CI:** inject the files from secrets before the build.

## Verifying

Events and crashes are checked by hand in the Firebase console, with the unit tests covering the rest.

1. Build `dev` with the config file in place and install it. `deb` sends no events, so use `dev` or `beta`.
2. Allow Usage Consent in the app. Nothing is sent before.
3. Turn on debug reporting. Android: `adb shell setprop debug.firebase.analytics.app at.stefanirndorfer.mobtt.dev`. iOS: add `-FIRAnalyticsDebugEnabled` to the scheme's launch arguments.
4. Open Firebase console, Analytics, DebugView, pick the device, and use the app. The `screen_view` and interaction events from the table appear within seconds. Parameters must match the catalog.
5. Crashlytics: log an error path or force a test crash in a `dev` build, restart the app, and the report shows up in the console within a few minutes.
6. Check the declined path too: decline consent, then confirm no events arrive.
7. Check the opposite case: remove the config file and rebuild. The app must still build and run with the SDK disabled.

Unit tests assert the events against a recording tracker on both platforms.
