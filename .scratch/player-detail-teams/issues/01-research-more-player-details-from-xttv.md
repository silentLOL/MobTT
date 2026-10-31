---
status: needs-human
blocked_by: []
---

# 01: Research further Player details XTTV can give us

**Question:** the Player screen shows only the Club, its Association and the Teams whose lineup lists the Player. What else does XTTV offer per Player?

- [x] Record a real `getSingleStandings` row and list every field it carries (ranking, wins and losses, points, quota), with the XML kept as a test sample
- [x] Say which of those fields are per League and which per Player, since a Player can appear in several Leagues
- [ ] Ask XTTV whether `getPlayerDetails`, currently closed to API access, can be opened to us, and note the answer
- [x] Propose which fields earn a place on the Player screen, without building them

## Answer

Recorded 2026-10-31 from `getSingleStandings` (Leagues 70, 8935, 8936, 8937, 8947); sample `XttvSamples.SINGLE_STANDINGS_RECORDED`.

**Fields of a `standing_row`:** `ranked` (1, or 0 for a Player below the League's minimum games), `rank`, `player_id`, first and last name, `player_name_suffix`, `player_pass_number`, `team_id`, `team_code`, `club_id`, `games_played`, `matches_won`, `matches_lost`, `player_rc_id`, `player_rc_points`, `player_rc_deviation`. The document also carries `league_id`, `rc_usage` and `rc_type` (all `rc` in the Leagues sampled). There is no points or quota field.

**Per League vs per Player:** a Player appears once per League and Team they are listed in (53865 sits in 8935 for EAMS1 and in 8947 for EAMS4; 62323 in 8936 for WOLK2 and 8937 for WOLK1).
- Per League row: `rank`, `ranked`, `team_id`, `team_code`, `games_played`, `matches_won`, `matches_lost`.
- Per Player, identical in every row: `player_pass_number`, `player_rc_id`, `player_rc_points`, `player_rc_deviation`, `club_id`, name.

**`getPlayerDetails`:** still closed with our key. It answers HTTP 200 with the comment "Access allowed only for Android App or API users with sufficient rights to access restricted API functions." and an empty `<xml>`. Whether XTTV will open it is not answered; see the comment below.

**Proposal** (not built):
1. RC rating (`player_rc_points`, optionally with the deviation): the one per-Player value, and the same in every League.
2. Per League and Team: rank, and matches won and lost with games played, shown on each Team row of the Teams section.
3. Skip pass number and `rc_id`: internal identifiers, not useful to a fan.
Cost: one `getSingleStandings` per League of the Player's Teams (the Teams lookup already knows their Leagues), and standings are not cached for Players by decision (ADR-0007), so a Player visit would add those requests.

## Comments

- 2026-10-31: split out of the Player screen change (Teams, tappable Club, no hero card), which ships only what the API provably gives us.
- 2026-10-31: Needs a human: please ask XTTV by email whether `getPlayerDetails` can be opened to our API key, and note the answer here. Everything else is answered above.
