---
---

# Settings as a full screen, with open source licenses and a tip link

## Problem Statement

Settings opens as a bottom sheet over the current screen. It has an X and a "Done" button, and no room to grow. The app ships open source libraries and the Inter font, whose licenses require the copyright notice and license text to travel with the app, and nothing in the app shows them. A user who likes the app has no way to support its developer.

## Solution

Settings becomes a full screen, pushed onto the current tab's stack like a Team or Fixture screen. It has the back chevron the other pushed screens already have, so the X and "Done" go away. A new About section at the bottom holds three rows:

- **Open source licenses** opens a second pushed screen listing every library, font and third-party asset the app ships, with its license text on tap.
- **Buy me a coffee** opens the developer's tip page in the browser. The row is hidden until the page's URL is configured.
- **Results powered by XTTV Ergebnisdienst** stays as it is today.

The My Player and My Club pickers stay inside Settings. Back steps out of a picker first, then out of Settings.

## User Stories

1. As a Player, I want Settings to open as a full screen, so that it is easy to read and use one-handed.
2. As a Player, I want a back chevron at the top of Settings, so that I leave it the same way I leave a Team or Fixture screen.
3. As a Player on Android, I want the system Back gesture to leave Settings, so that it behaves like every other screen.
4. As a Player on iOS, I want the edge swipe to go back from Settings, so that it behaves like every other screen.
5. As a Player, I want Back to return me to the screen and tab I opened Settings from, so that I do not lose my place.
6. As a Player choosing My Player or My Club, I want Back to step out of the picker first, so that I do not leave Settings by accident.
7. As a Player, I want no "Done" button, because changes are saved as I make them.
8. As a Player on Settings, I do not want a Settings gear in the top bar, because I am already there.
9. As a Player, I want an About section at the bottom of Settings, so that the rarely used rows stay out of the way.
10. As a Player, I want to open a list of open source licenses, so that I can see what the app is built on.
11. As a Player, I want to tap a library to read its license text, so that I can check its terms.
12. As a Player, I want the licenses screen to have the same back chevron, so that I can return to Settings.
13. As a Player, I want the licenses to be listed alphabetically, so that I can find one.
14. As a Player, I want the Inter font credited with its license, as its license requires.
15. As a developer, I want the license list generated from the dependencies at build time, so that a new library cannot be forgotten.
16. As a developer, I want the Firebase packages on iOS and the Kotlin libraries in the shared framework both listed on iOS, so that the list is complete on each platform.
17. As a fan who likes the app, I want a "Buy me a coffee" row, so that I can support its developer.
18. As a fan, I want the coffee row to open the developer's page in my browser, so that I pay in a place I already trust.
19. As the developer, I want the coffee row hidden until its URL is set, so that nothing ships pointing nowhere.
20. As the developer, I want the URL in `AppConfig`, so that I can change the tip page without touching feature code.
21. As the developer, I want the coffee row switchable off on iOS alone, so that an App Review objection costs one change.
22. As a screen reader user, I want each About row announced as a button with its destination, e.g. "Open source licenses", so that I know what a tap does.
23. As a screen reader user, I want the back chevron labelled "Back", as on other screens.
24. As a German-speaking Player, I want every new string in German (de-AT), so that the screens read naturally.
25. As the product owner, I want screen and tap events in the analytics catalog, so that I can see whether anyone opens the licenses or the tip link.
26. As a user who declined analytics, I want nothing sent for these screens or taps, as for every other event.
27. As a Player with a large font size, I want the licenses list and its text to wrap and scroll, so that nothing is cut off.
28. As a Player in dark mode, I want both new screens to follow the appearance setting, as the rest of the app does.
29. As a developer, I want the Settings navigation decision recorded in an ADR that supersedes the sheet decision, so that the reason is not lost.

## Implementation Decisions

- **Settings is a pushed screen, not a sheet.** This supersedes the "Settings as a sheet" part of ADR-0005, which rejected a destination because Settings would leave the tab's back stack. Pushing onto the current tab's stack answers that: Back returns to where the user came from. A new ADR records the supersession and ADR-0005 links to it.
- **Android navigation.** Settings and the licenses screen become destinations in the settings `:api` module, with `GlobalNavigator` extension functions as the only entry points, like the Club and Player features. The navigator's special "settings open" state, its open and close calls, and the sheet hosted by the shell are removed. The shell's top bar already shows the back chevron whenever the back stack has more than one entry, so the chevron needs no new component. The shell hides the gear while Settings is on top.
- **iOS navigation.** Settings and the licenses screen are pushed onto the current tab's `NavigationStack`, with the system back chevron, like Team detail. One shared registration covers all tabs, so the destination is declared once. The existing "open Settings" callbacks (the top bar and the Club tab's event) push instead of presenting a sheet. The top bar hides the gear while Settings is on top. The consent prompt stays suppressed while Settings is open.
- **Settings screen content.** The header with its title and X, and the "Done" button, are removed on both platforms, and with them the Dismiss event. Existing rows and their order stay: My Player, My Club, the derived Club and Association, Appearance, usage consent. A new About section follows, holding the licenses row, the coffee row, and the existing XTTV credit.
- **Pickers.** The My Player and My Club pickers keep swapping in place with their own back arrow. System Back, and the iOS edge swipe, step back inside the picker before they leave Settings. On iOS this needs the system back to be intercepted while a picker is showing; the iOS and Android flows must behave the same.
- **Licenses screen.** One native screen per platform. The ViewModel exposes a list of entries (name, version if any, license name, license text) and which entry is expanded. Entries are grouped by kind (libraries, fonts, assets) and sorted by name. A missing license text shows the license's name and an official URL, never an empty row.
- **License data.** Generated at build time, never hand-maintained. AboutLibraries covers the Gradle and KMP dependencies. LicensePlist covers the Swift packages (Firebase and its transitive packages). On iOS the list shows both, because the Kotlin libraries ship in the shared framework. Fonts and any third-party assets have no dependency manifest, so a small hand-written list is merged into the generated one. Inter's OFL text is added to the repository, since no license file exists there yet. A check fails when a dependency has no license entry.
- **Coffee row.** `AppConfig` gains an optional support URL, null by default, like the Fixture link host. While it is null the row is not shown. When set, tapping the row logs the event and emits a one-off event; the Screen opens the URL in the browser (Android through a private `Context` extension, iOS through the system URL opener). The URL is never hardcoded in a feature module (CLAUDE.md §14). The first value is the developer's PayPal page. A PayPal.me page shows the owner's name and country publicly, which the developer has accepted.
- **App Store risk.** Apple often rejects an external tip link for the developer, since it expects In-App Purchase. The row ships on both platforms with one external link. If App Review rejects it, the row is hidden on iOS by leaving the iOS URL unset; no other change is needed. Google Play is more lenient.
- **Analytics.** Two catalog additions, made in `docs/ANALYTICS.md` before any code: a `licenses` value for the `screen` parameter (sent by the licenses ViewModel's creation, like every `screen_view`), and a new event `support_link_opened` with no parameters, sent when the coffee row is tapped. Whether a browser opened is not known. Both fire from the ViewModels on both platforms, after consent.
- **Logging.** The licenses ViewModel logs at `Error` with the throwable when the list cannot be read, and the screen shows an empty state. Nothing sensitive is logged.
- **Localization.** New strings (About header, three row titles and accessibility labels, licenses screen title, empty state, section titles) are added in English and German to the Android and iOS files, then pushed to Loco. The "Done" and close strings are removed.
- **Accessibility.** Each About row is one button announcing its destination. The back chevron keeps its existing "Back" label. Licenses text scales with the font size and scrolls. Follows CODING_STANDARDS §12.

## Testing Decisions

- A good test checks external behavior: what the ViewModel exposes and emits for an action, not how it is built.
- **Settings ViewModel, both platforms.** Extend the existing Android and iOS Settings ViewModel tests. The licenses and coffee actions emit their one-off events, the coffee tap logs `support_link_opened` through the recording tracker, a null URL means no coffee row in the state, and the removed Dismiss and Done paths are gone. The existing consent, appearance and picker cases keep passing.
- **Licenses ViewModel and mapping, both platforms.** Unit tests for turning the generated data into entries: libraries, fonts and assets together, sorted by name, a missing license text, an empty list, expand and collapse, and `screen_view` for `licenses`.
- **Prior art.** `SettingsViewModelTest` (Android) and `SettingsViewModelTests` (iOS) for ViewModel and recording-tracker tests, and the Venue search-text tests for pure mapping.
- **Previews** cover Settings with and without the coffee row, the licenses list loaded, expanded and empty, and large font and dark mode where the screens already have previews.
- **Manual checks**, recorded in the tickets as for Venue: the chevron and Back from Settings on both platforms, Back out of a picker first, Settings opened from each tab returns to that tab, the licenses screen opens and a license reads, and the coffee link opens in a browser.
- **No new UI or XCUITest tests.** The project has none for Settings today.

## Out of Scope

- A native tip jar with In-App Purchase.
- Making the My Player and My Club pickers pushed screens.
- A web page of licenses, or a licenses link outside the app.
- Reworking Settings' existing rows, their order, or the consent flow.
- Fixing the `setting_changed` catalog note that says iOS has no in-app setting (iOS now has an appearance picker); worth a separate small edit.
- Auditing the licenses of dependencies for compatibility. This spec only shows them.

## Further Notes

- Not legal advice. The licenses in use (Apache 2.0, MIT, BSD, OFL) require the notice and license text with any distribution, including app store builds. Apache 2.0 also requires dependency `NOTICE` text where one is shipped.
- The new ADR should say why this reverses ADR-0005: Settings has grown past a sheet, it now holds a second pushed screen, and pushing onto the current stack removes the original objection.
- Suggested tickets once the spec is accepted: Android Settings as a pushed destination with the ADR; iOS Settings pushed; Android licenses screen with data generation; iOS licenses screen with data generation; the coffee row with `AppConfig` and the analytics rows.
