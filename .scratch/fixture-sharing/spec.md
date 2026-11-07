---
---

# Sharing a Fixture

## Problem Statement

A fan looking at a Fixture in MobTT, whether an upcoming one or a finished one, has no way to pass it on. To tell their team chat who plays when and where, or how a Fixture ended Match by Match, they have to retype it or send a screenshot. Whoever receives it can't tap through to anything, and nothing tells them the information came from MobTT. That wastes MobTT's best chance of being discovered: fans talking about Fixtures in club chats.

## Solution

The red card at the top of the Fixture screen gets a **Share** button at the top right, on the same line as the "Fixture" pill. Tapping it opens the system share sheet with everything the screen shows: the pairing, the score or "Not played yet", the kickoff, the venue and address, and for a Finished Fixture every Match with its Players and Sets score. The text ends with a **Fixture link**.

A Fixture link is a web address on MobTT's own domain, `https://<links-domain>/f/<Fixture id>`. On a phone with MobTT installed, it opens that Fixture in the app. Anywhere else, it opens a small MobTT page with the app's logo, a one-line pitch, both store badges, and a link to the Fixture's page on XTTV.

Later, the share also carries an **image** of the Fixture drawn in MobTT's style: the red card, every Match, and a footer with the MobTT logo, "Get MobTT" and a QR code for the Fixture link. With the image attached, the text shrinks to a short caption.

Sharing stays switched off until MobTT is listed in both stores, so no shared link leads to an empty store page. Opening Fixture links works as soon as it's built, so it can be tested before then.

The work comes in three parts, in this order:
1. **Fixture links**: the domain, the static site, and opening a Fixture from a link.
2. **Share button**: the button, the share text and the analytics.
3. **Share image**: the image and the shorter caption.

Android and iOS ship each part together.

## User Stories

### Sharing from the Fixture screen

1. As a fan, I want a Share button on the Fixture's red card, so that I can pass a Fixture on without retyping it
2. As a fan, I want the Share button next to the "Fixture" pill at the top right of the card, so that it is easy to find and doesn't push the score down
3. As a fan, I want to share a Scheduled Fixture, so that I can tell my team who plays when and where
4. As a fan, I want to share a Finished Fixture, so that I can tell others how it ended
5. As a fan, I want the system share sheet, so that I can send the Fixture to whichever app I use (WhatsApp, Signal, mail, SMS)
6. As a fan, I want the shared text in the app's language, so that it reads naturally to my German-speaking club
7. As a fan, I want the date and time in the shared text to read the same as on the card, so that nothing gets lost between the screen and the message
8. As a fan, I want the shared text to name both Teams and the score, so that the first line tells the whole story
9. As a fan, I want the shared text of a Scheduled Fixture to say "Not played yet" where the score would be, so that nobody mistakes it for a result
10. As a fan, I want the venue name and address in the shared text, so that teammates and spectators can find the hall
11. As a fan, I want every Match of a Finished Fixture in the shared text, with its number, Singles or Doubles, the Players and the Sets score, so that the recipient sees everything I see
12. As a fan, I want a Match without a result shown without a score, so that it isn't mistaken for 0:0
13. As a fan, I want a Finished Fixture without Match results to say "No Match results available yet.", as the screen does, so that the recipient knows the details are missing and not forgotten
14. As a fan, I want the shared text to be plain, one item per line, so that it looks right in any app and shows no stray formatting characters
15. As a fan sending by email, I want the pairing as the subject, so that the email is recognisable in an inbox
16. As a fan, I want the shared text to end with a link to the Fixture, so that the recipient can open it
17. As a fan with the larger text setting, I want the Share button to stay usable, so that I can share like everyone else
18. As a screen reader user, I want the Share button announced as "Share Fixture", so that I know what it does
19. As a fan, I want the Share button big enough to hit, at least 48dp on Android and 44pt on iOS, so that I don't tap a Team link by mistake
20. As a fan, I want the Share button to be visible against the red card in light and dark mode, so that I can see it

### Opening a Fixture link

21. As a recipient with MobTT installed, I want a Fixture link to open that Fixture in the app, so that I see it with all its detail in one tap
22. As a recipient, I want Back from a Fixture opened by link to take me to Home, so that I land somewhere familiar and not out of the app
23. As a recipient on iOS, I want the Fixture opened by link pushed onto the Home tab, so that the tab bar behaves as usual
24. As a recipient who installed MobTT but hasn't chosen My Player or My Club yet, I want the Fixture to open anyway, so that the link does what it promised; onboarding can wait one screen
25. As a recipient, I want a Fixture link to work whether MobTT was closed or already running, so that the link always lands on that Fixture
26. As a recipient, I want a link to a Fixture XTTV no longer knows to show the usual "Couldn't load this Fixture" screen with Try again, so that I'm not left on a blank screen
27. As a recipient without MobTT, I want the link to open a page that tells me what MobTT is and where to get it, so that I can decide whether to install it
28. As a recipient without MobTT, I want the page to offer a link to the Fixture on XTTV, so that I can still see the details without installing anything
29. As a recipient on a desktop computer, I want the same page with both store badges, so that I can find the app for my phone
30. As a recipient, I don't want to be thrown into a store without asking, so that opening a link from a friend doesn't feel like a trap
31. As a recipient who installs MobTT from the page, I want the usual onboarding, so that the app is set up for me before anything else
32. As a tester with a `prod`, `beta` or `dev` build, I want Fixture links to open my build, so that I can test the feature on real links
33. As a developer with a `deb` build, I want to be able to verify Fixture links by hand on my device, so that I can work on link handling locally

### The share image (part 3)

34. As a fan, I want the share to carry an image of the Fixture styled like MobTT, so that it stands out in a busy chat
35. As a fan, I want the image to show the red card and every Match, so that it carries all the details on its own
36. As a fan, I want the image always in MobTT's light style, whatever my phone's appearance, so that every recipient sees it the same way
37. As a fan, I want a Fixture with 16 or more Matches to stay one tall image, so that nothing is cut off
38. As a recipient who only got the picture, I want a QR code for the Fixture link on it, so that I can still open the Fixture
39. As a recipient, I want the MobTT logo and "Get MobTT" in the image's footer, so that I know where it comes from
40. As a fan, I want the text shortened to the pairing, the score or kickoff, and the link once the image is attached, so that the image carries the details and the caption doesn't repeat them

### Release and measurement

41. As the app owner, I want sharing switched off until MobTT is in both stores, so that no shared link leads to a missing store page
42. As the app owner, I want to turn sharing on in one place, so that launching it is a one-line change
43. As the app owner, I want to know how often Fixtures are shared, split by Scheduled and Finished, so that I can see whether the feature is used
44. As the app owner, I want to know how often the app is opened from a Fixture link, and whether it was closed or already running, so that I can see whether shares bring people in
45. As a user who declined Usage Consent, I want neither event collected, so that my choice holds for sharing too
46. As the app owner, I want the static site free of analytics and cookies, so that recipients aren't tracked for opening a friend's link

## Implementation Decisions

### Fixture link (shared)

- A Fixture link is `https://<links-domain>/f/<Fixture id>`, using the plain XTTV Fixture id (ADR-0008). `/t/`, `/p/`, `/c/` and `/l/` are reserved for Team, Player, Club and League links but are not built. The domain, self-hosting and URL shape are recorded in ADR-0011. The domain's name is not chosen yet. Until it is, `AppConfig.linksHost` is null: Fixtures are shared without a link (the text and caption end after their last detail, and the image has no QR code), and nothing reads as a Fixture link. The Android intent filter and the iOS Associated Domain carry the placeholder `links.mobtt.example`, which never verifies, so no link reaches the app. Registering the domain means setting the real host in those three places, with no code change.
- Building a Fixture link from a Fixture id, and reading a Fixture id back from a URL, is one piece of shared Kotlin in the club domain. Both platforms use it so the shape can't drift apart. Reading returns nothing for another host, a reserved prefix, a missing or empty id, or extra path segments.
- `AppConfig` gains the links host (the same in every environment, like `xttvHost`) and a `fixtureSharingEnabled` switch. The switch is on for `deb` and `dev` so the feature can be tried on a device, and off for `beta` and `prod` until both store listings are public, then turned on in one place, like `ratingHistoryEnabled`. It only hides the Share button; opening Fixture links isn't switched.

### Static site

- A static site we host ourselves (GitHub Pages, Netlify or similar) on the links domain, with no analytics and no cookies.
- `/.well-known/assetlinks.json` lists the `prod`, `beta` and `dev` application IDs with their release signing fingerprints. `/.well-known/apple-app-site-association` lists the matching bundle IDs for paths under `/f/`. `deb` isn't listed and is verified by hand on a developer's device.
- Every `/f/<id>` path serves the same page: MobTT's logo, a one-line pitch, Google Play and App Store badges, and a smaller "View this Fixture on XTTV" link to `https://oettv.xttv.at/ed/index.php?meid=<id>`, built in the browser from the path. It doesn't redirect on its own. XTTV shows "report not available yet" for a Scheduled Fixture; we accept that.
- The site's own `og:` tags give a generic MobTT link preview in messengers.
- No deferred deep link: after installing from the page, a user goes through the regular onboarding.

### Opening a Fixture link

- Android: the app's activity declares a verified App Link intent filter for `https://<links-domain>/f/`. An incoming link, on a cold start or while running, reads the id through the shared Fixture link piece and leaves the back stack as Home with that Fixture's destination on top.
- iOS: the app declares the Associated Domain `applinks:<links-domain>`. The shell handles the incoming URL the same way and pushes the Fixture onto the Home tab's navigation stack, selecting the Home tab.
- A link opens the Fixture even when onboarding (My Player or My Club) isn't done. The Fixture shows above whatever the app would normally start on, and Back leads there.
- A URL that isn't a Fixture link is ignored, and the app starts as usual. An id XTTV doesn't know lands on the Fixture screen's existing error state.
- The shell's ViewModel logs `fixture_link_opened` with whether it was a cold start or the app was already running. The catalog row in `docs/ANALYTICS.md` comes first, as that doc requires.

### Share button

- The design system's hero card gets an optional trailing action in the pill row, on Android (`BatHeroCard` and its pill row) and iOS (`BatHeroCardView`), so Team, Player and Club hero cards can use it later. The Fixture screen fills it with an icon-only Share button: the platform's own share icon, `onHero` colour, a minimum touch target of 48dp or 44pt, and the accessibility label "Share Fixture".
- The button shows for Scheduled and Finished Fixtures whenever `fixtureSharingEnabled` is on. The card only exists in the Content state, so Loading and Error never show it.
- Tapping it sends a new action to `FixtureDetailViewModel` on each platform. The ViewModel logs `fixture_share_tapped` with `status` set to `scheduled` or `finished`, and sends a one-off event carrying the `FixtureDetail` and its Fixture link. There is no completion tracking, because neither platform reliably reports whether the user sent anything.
- Android: the Screen handles the event through a private `Context` extension that starts an `ACTION_SEND` chooser with `EXTRA_TEXT` and `EXTRA_SUBJECT` (the ViewModel holds no `Context`, §11). iOS: the screen presents the system share sheet with the text and the subject.
- The share text is built natively on each platform by a plain function from `FixtureDetail` and the Fixture link to a subject and a body, using the platform's strings and the same kickoff formatting as the card.
  - Subject: `<home Team> – <away Team>`.
  - Body, one item per line, no bold or other markup:
    - `<home Team> – <away Team>  <score>`, or `Not played yet` where the score would be;
    - the kickoff;
    - the venue, if XTTV gives one;
    - the address, if XTTV gives one;
    - for a Finished Fixture, a blank line, the "Matches" heading, then one line per Match: `<number> <Singles|Doubles>: <home Players> – <away Players>  <h> : <a>`, with doubles pairs joined by " / " and the score left off when a Match has no result. A Finished Fixture without Matches gets "No Match results available yet." under the heading;
    - a blank line and the Fixture link.
  - It reuses the existing Fixture-detail strings (Singles, Doubles, Matches, Not played yet, the score format, no Match results). New keys (the button's label, the share sheet title) are added in English and German and pushed to Loco.

German example of a Finished Fixture:

```
Hagenberg 3 – Kefermarkt 4  10 : 0
Fr., 2. Okt. 2026, 19:30
Sporthalle Hagenberg
Hauptstraße 1, 4232 Hagenberg

Spiele
1 Doppel: Huber / Bauer – Gruber / Wolf  3 : 1
2 Einzel: Huber – Gruber  3 : 0
…

https://<links-domain>/f/464821
```

### Share image (part 3)

- Rendered on the device from native views, always in the light theme, at a fixed width, as one image however many Matches there are.
- Content: the red card as on screen (without the Share button), the Matches list, and a footer with the MobTT logo, "Get MobTT" and a QR code encoding the Fixture link. The QR code is generated on the device; no network is needed.
- Attached to the same share (Android: a content URI from a `FileProvider` with `image/png`, the text kept as `EXTRA_TEXT`; iOS: the image as a transferable item next to the text). Some apps drop the text when an image is attached, and the QR code covers that.
- With the image attached, the text shrinks to the pairing, the score or kickoff, and the Fixture link. The subject stays the same.

## Testing Decisions

A good test drives a module through its public interface with fake data and checks what a caller sees: the state, the one-off events, the analytics, or the text produced. It doesn't check internal calls, private state or call order. No test calls XTTV or the links domain.

The seams:

1. **Fixture link, shared Kotlin (`commonTest`).** Id to URL and back; another host, a reserved prefix (`/t/`, `/p/`, `/c/`, `/l/`), a missing or empty id, extra path segments and a non-https URL all read as "not a Fixture link". Prior art: the club domain tests such as `TeamDetailTest` and `LeagueDetailTest`.
2. **`FixtureDetailViewModel` on both platforms (fake use cases, recording analytics).**
   - Tapping Share sends the event with the `FixtureDetail` and its Fixture link, and logs `fixture_share_tapped` with the right status.
   - With the switch off, the state says the Fixture can't be shared.

   Prior art: `FixtureDetailViewModelTest` (Android) and `FixtureDetailViewModelTests` (iOS), with `RecordingAnalyticsTracker` on each platform.
3. **Share text, native, one per platform.** Subject and body in English and German for a Scheduled Fixture; a Finished one with Singles and Doubles; a Match without a result; a Finished Fixture without Matches; with and without venue and address; and, in part 3, the shortened caption. The Android test reads real string resources through Robolectric, which is already on the classpath for Roborazzi.
4. **Opening a Fixture link: the Android and iOS shells.**
   - An incoming Fixture link leaves the Android back stack as Home with the Fixture on top, and selects the Home tab with the Fixture pushed on iOS.
   - It does this before onboarding is done.
   - A URL that isn't a Fixture link changes nothing.
   - `fixture_link_opened` is logged as cold start or already running.

   On Android this is tested against `BackStackGlobalNavigator` and the shell's ViewModel (the navigator has no tests yet); on iOS against the shell's URL handling.
5. **Screenshots (Roborazzi preview scanning on Android; previews on iOS).**
   - The hero card with and without its trailing action, in light, dark and large text.
   - The share image for a Scheduled Fixture, a Finished one, and one with 16 Matches.
6. **Manual only.**
   - The association files, checked with Google's Digital Asset Links tester, Apple's AASA validator, and `adb shell pm get-app-links` for each flavor.
   - The browser page on a phone and a desktop.
   - The share sheet in WhatsApp, Signal, mail and SMS on both platforms.
   - Scanning the QR code from a shared image.

## Out of Scope

- Deferred deep links: opening the shared Fixture right after a fresh install.
- Install attribution and any link service SDK (Branch, AppsFlyer, Adjust).
- Analytics, cookies or any tracking on the static site.
- Showing the Fixture's data on the browser page, which would need a server and the XTTV API key.
- Sharing Teams, Players, Clubs or Leagues. Their URL prefixes are only reserved.
- Linking to XTTV as the main link of the share.
- Showing the League in the shared text or the image. `FixtureDetail` doesn't carry it.
- Marking Match winners in the text. Plain text can't carry bold everywhere, and the Sets score shows who won.
- Tracking whether a share was actually sent.
- Publishing the store listings themselves.

## Further Notes

- **Domain later:** the links domain may come after the store launch. Sharing ships without links until then (see Fixture link (shared)); ADR-0011 stays Proposed until the domain is named there. The store listings are needed before `fixtureSharingEnabled` is switched on, the domain is not.
- **Glossary:** "Fixture link" is defined in `GLOSSARY.md` under Sharing. Avoid "deep link", "share link" and "dynamic link" in code and tickets.
- **XTTV page:** the XTTV Fixture page `https://oettv.xttv.at/ed/index.php?meid=<id>` was checked against a recorded played Fixture (`meid=464821`, Hagenberg 3 – Kefermarkt 4). It takes the API's Fixture id, needs no other parameters, and serves every Association from one host. It isn't built for phones and shows a cookie banner. Only played Fixtures have a report.
- **Signing keys:** each new signing key or bundle ID must be added to the association files before that build can open Fixture links.
