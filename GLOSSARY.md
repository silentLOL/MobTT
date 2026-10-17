# BatMate Domain

BatMate lets Austrian table tennis players and fans follow clubs, teams, players and league fixtures, using data published by XTTV. Terms below are named in English in code; the German name used by the sport is given in each definition.

## Language

### Organisation

**Association**:
A regional or national governing body that runs leagues (German: Verband), e.g. WTTV or the ÖTTV Bundesligen. A Club belongs to exactly one Association.
_Avoid_: Federation, region, Landesverband

**Club**:
A table tennis organisation that fields Teams and has Players (German: Verein).
_Avoid_: Verein, organisation

**Team**:
A squad a Club enters into a League (German: Mannschaft), e.g. "TTC Olympic 1".
_Avoid_: Mannschaft, side, squad

**League**:
A competition in which Teams play Fixtures against each other (German: Liga), e.g. "1. Landesliga Herren".
_Avoid_: Liga, division, championship

**Player**:
A registered person who plays for exactly one Club (German: Spieler).
_Avoid_: Spieler, athlete, member

### Competition

**Fixture**:
The scheduled encounter between two Teams in a League (German: Begegnung), made up of Matches. Its score counts Matches won, e.g. 4:2.
_Avoid_: Begegnung, game, encounter, event

**Match**:
A single game inside a Fixture, between individual Players or doubles pairs (German: Einzelspiel or Doppel). Its score counts Sets won, e.g. 3:1.
_Avoid_: Spiel, game

**Set**:
One played segment of a Match, won by reaching the points target with a margin (German: Satz), e.g. 11:8.
_Avoid_: Satz, game, round

### The user

**My Player**:
The one Player the user chooses to follow as themselves. Their Club and Association are derived from it.
_Avoid_: Active player, profile, account

**My Club**:
The Club the user follows when they have no Player to choose. Their Association is derived from it.
_Avoid_: Home club, Stammverein

**Favorite**:
A Player, Team or Club the user has bookmarked for quick access. Independent of My Player and My Club.
_Avoid_: Bookmark, pin, Quick Link

## XTTV terms

XTTV, the source of our data, names some things differently. Our terms above stay canonical; XTTV's words appear only where we read its data.

| XTTV term | Our term |
|-----------|----------|
| Federation (`getFederations`) | Association |
| Game (`getClubGames`, `getLeagueGames`, `getGameDetails`) | Fixture |
