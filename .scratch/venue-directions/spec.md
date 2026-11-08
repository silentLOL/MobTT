---
status: resolved
---

# Directions to a Fixture's Venue

## Problem Statement

The Fixture screen shows where a Fixture is played: the Venue's name and its address, under the kickoff in the red card. A Player or fan who wants to get there has to copy the address into a maps app by hand. Nothing on the screen can be tapped to get directions.

## Solution

On the Fixture screen, the Venue lines in the red card get a map pin and become one tap target. Tapping it opens the Venue in a maps app, searching for the Venue's name and every address part XTTV gives, e.g. "Sporthalle Hagenberg, Softwarepark 101, 4232 Hagenberg".

- **Android** opens the user's own maps app (Google Maps, OsmAnd, Waze…). When none is installed, Google Maps opens in the browser.
- **iOS** opens Apple Maps. When the user has deleted Maps, iOS opens Apple Maps on the web.

XTTV gives no coordinates, only text, so the search finds whatever the maps app matches. When XTTV gives only a city, the map opens on that city; the user sees the same text on screen, so this is no surprise. When there is neither a name nor any address part, there is nothing to tap.

## User Stories

1. As a Player travelling to an away Fixture, I want to tap the Venue and get directions in my maps app, so that I don't retype the address.
2. As a fan on Android without Google Maps, I want the Venue to open in the maps app I use, or in the browser if I have none.
3. As a screen reader user, I want the Venue announced as a button that opens it in Maps, so that I know what tapping does.

## Decisions

- **Link-out only, no inline map.** An inline map needs geocoding, can place a wrong pin, and on Android needs a map provider (Google Maps with an API key and billing, or MapLibre/OSM). It is a separate decision for later.
- **Search text**: the Venue's name first, then street, postal code and city, using every part that exists, joined with ", ".
- **Android**: a `geo:0,0?q=<search text>` intent; when no app handles it, `https://www.google.com/maps/search/?api=1&query=<search text>` in the browser.
- **iOS**: `https://maps.apple.com/?q=<search text>`, with no chooser for Google Maps or Waze.
- **Where**: the Venue lines in the red card, with a map pin. The kickoff stays plain text.
- **Analytics**: `fixture_venue_tapped` with `status` (`scheduled` or `finished`), like `fixture_share_tapped`. It records the tap; whether a maps app opened is not known.
- **Flow**: the tap is an action to the Fixture detail ViewModel, which logs the event and emits a one-off event; the Screen opens the maps app (Android through a private `Context` extension).

## Out of Scope

- An inline map of the Venue.
- Making the Venue row on the Home "next Fixture" card tappable: the card as a whole opens the Fixture.
- A maps link in the shared Fixture text or image: messengers turn the address into a link themselves.
- Renaming `Fixture.venue` (the Venue's name) to match the glossary's Venue.
- A chooser between maps apps on iOS.
