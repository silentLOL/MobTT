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
| `screen` | `home`, `players`, `choose_player`, `choose_club`, `club`, `player_detail`, `team_detail`, `league_detail`, `settings` |
| `kind` | `player`, `club`, `team`, `league` |
| `source` | `home`, `settings`, `prompt` |
| `from` | a `screen` value |

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
| `setting_changed` | `setting`, `value` | A setting is changed. `setting` is `appearance` with `value` `system`, `light` or `dark` (Android only: iOS has no in-app setting yet). |

## Where each platform fires them

| Screen | Android | iOS |
|---|---|---|
| Home | `HomeViewModel` | `HomeViewModel` |
| Players, choose Player, choose Club | `PlayersViewModel`, `ChooseClubViewModel` | `PlayersViewModel` (by mode) |
| Club | `ClubViewModel` | `ClubViewModel` |
| Player, Team, League detail | the matching `*DetailViewModel` | the matching `*DetailViewModel` |
| Settings | `SettingsViewModel` | `SettingsViewModel` |
| Consent step (`consent_changed` only) | `UsageConsentViewModel` | `UsageConsentViewModel` |

## Verifying

Events are checked with DebugView in `dev` (`deb` sends none). Unit tests assert the events against a recording tracker on both platforms.
