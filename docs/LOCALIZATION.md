# Localization with Loco

Strings are managed in the Loco project "MobTT" (localise.biz). The repo files stay the build input; Loco is where translations are edited.

| Platform | File | Loco tag |
|---|---|---|
| Android | `android/core/localization/src/main/res/values*/strings.xml` | `android` |
| iOS | `iosApp/iosApp/Resources/Localizable.xcstrings` | `ios` |

Each platform keeps its own key set (iOS keys carry the format specifier, e.g. `club_favorite_add_cd %@`), so the tags keep them apart in one project.

## Keys

Put both keys in the git-ignored `local.properties`:

```properties
loco.fullAccessKey=...   # push
loco.readKey=...         # pull
```

## Commands

```sh
./scripts/loco.sh push   # repo -> Loco (creates the locale on first run)
./scripts/loco.sh pull   # Loco -> repo
```

`push` overwrites the Loco text of existing ids with the repo text; it never deletes assets. `pull` replaces the Android files and merges into the iOS catalog. The Android export has a Loco header comment and no blank lines between groups.

## Languages

| Loco | Android | iOS |
|---|---|---|
| `en` | `values` | `en` |
| `de-AT` (Austrian German) | `values-de` | `de` |

Austrian German serves every German-language device, so it uses the plain `de` resources and needs no extra Xcode region. Edit translations in Loco, then `pull`.

## Adding a language

Add a row to `LOCALES` in `scripts/loco.sh`: Loco code, Android folder, iOS language code. Add the language to `knownRegions` in the Xcode project if it is new on iOS.
