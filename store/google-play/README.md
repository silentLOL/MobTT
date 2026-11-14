# Google Play store material

Everything Play Console asks for in the listing, one folder per Play language (`en-US`, `de-DE`, `hr`, `tr-TR`, `ro`, `hu-HU`, `uk`; German is Austrian German, as in the app, see `docs/LOCALIZATION.md`). The `hr`, `tr-TR`, `ro`, `hu-HU` and `uk` texts are machine-drafted: have a native speaker read them before publishing. Those five folders hold texts and a feature graphic only; phone screenshots are shown in the `de-DE` default for them until localized ones are captured. Console steps and form answers: [`docs/PLAY_CONSOLE_CHECKLIST.md`](../../docs/PLAY_CONSOLE_CHECKLIST.md).

| File | Play field | Limit |
|---|---|---|
| `<locale>/title.txt` | App name | 30 |
| `<locale>/short-description.txt` | Short description | 80 |
| `<locale>/full-description.txt` | Full description | 4000 |
| `<locale>/release-notes.txt` | What's new | 500 |
| `<locale>/graphics/feature-graphic.png` | Feature graphic, 1024x500 | |
| `<locale>/graphics/phone/*.png` | Phone screenshots, 1242x2208 (9:16), 5 per language | 2 to 8 |
| `icon/icon-512.png` | App icon, 512x512 | |
| `privacy-policy.en.md`, `privacy-policy.de.md` | Drafts of the policy page to publish | |

The text files hold the plain field content, with no trailing text. Check lengths with `wc -m`.

## Screenshots

The phone shots are captured from the `deb` debug build on the Pixel 9a emulator, with the fake data from `:core:fake-directory` (fictional Players, TTC Olympic and friends), so no real person appears. XTTV is bound after the fake in `initKoin`, so the real repositories win; for the capture `xttvModule` was commented out of `repositoryModules` locally and the change not committed. Set the app language per run with `adb shell cmd locale set-app-locales at.stefanirndorfer.mobtt.deb --locales de-AT`.

Order, as in the folders: Home with My Club and Favorites, Club with Teams, League table, Player with Rating history, Settings. The Fixture screen is left out on purpose: it shows a Share button that `prod` hides until the app is in both stores. Raw shots are 1080x2424, which Play would reject (longer side may be at most twice the shorter), so `tools/compose.py` frames them on the brand red with a caption:

```sh
python3 -m venv /tmp/venv && /tmp/venv/bin/pip install pillow
/tmp/venv/bin/python store/google-play/tools/compose.py <raw-en-dir> <raw-de-dir>   # needs rsvg-convert
```

Captions are in the script. `compose.py --feature-graphics` rebuilds only the feature graphics (taglines in `TAGLINE`) without raw screenshots. Tablet (7" and 10") screenshots are optional and not made: the app has no tablet-specific layout. Add them if you want the "tablet-ready" listing label; the Pixel_Tablet AVD exists.

## Icon and feature graphic

`icon-512.png` is `docs/brand/app-icon.svg` rendered at 512x512 (full bleed, Play applies the mask). The feature graphic is the launch mark on the brand gradient with the app name and a tagline; edit `tools/compose.py` to change it.
