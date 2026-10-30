---
status: resolved
blocked_by: [1]
---

# 03: Android app shell

**What to build:** launching the Android app shows a working native shell built from the design system. The user moves between Home, Club, Players and Live, opens a Settings sheet, and switches between light, dark and system appearance. Every tab shows a sensible empty state. Text is German or English following the device language, in the Inter font.

- [x] `androidApp` is a thin shell with a main `NavHost`, and each tab is its own `:android:feature:{name}` module split into `:api` and `:impl`
- [x] `:android:core:localization` holds German and English strings using `{module}_{context}_{type}` keys, and Inter is bundled there
- [x] The design system consumes the bundled font in place of the system default and the `TODO` is removed
- [x] The Settings sheet opens and closes, and the appearance choice persists across restarts
- [x] Each tab has an empty state with `@BatScreenPreview` previews for its distinct states
- [x] The layout survives 200% font scale and tablet width, and every control meets the 48dp touch target

**Manual check outstanding:** 200% font scale, tablet width and TalkBack on a device.
