package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview

import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamDetail

private val team = Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga1")

val teamDetailPreview = TeamDetail(
    team = team,
    clubName = "TTC Olympic",
    league = League("landesliga1", "1. Landesliga Herren"),
    standings = listOf(
        Standing(1, "voest-1", "SK VÖEST Wien 1", played = 6, points = 11),
        Standing(2, team.id, team.name, played = 6, points = 9),
        Standing(3, "nussdorf-1", "Nussdorf TTC 1", played = 6, points = 4),
    ),
    lineup = listOf(
        LineupPlayer("huber", "Michael Huber"),
        LineupPlayer("gruber", "Anna Gruber"),
        LineupPlayer("eder", "Thomas Eder"),
    ),
)

val teamDetailEmptyLineupPreview = teamDetailPreview.copy(lineup = emptyList())

val teamDetailNoLeaguePreview = teamDetailPreview.copy(league = null, standings = emptyList())

val teamDetailLongNamesPreview = TeamDetail(
    team = Team("s1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "g"),
    clubName = "Union Tischtennisclub St. Pölten Nord-West",
    league = League("g", "Gebietsliga Nord Herren Oberes Playoff"),
    standings = listOf(
        Standing(1, "h1", "Sportunion Hollabrunn Tischtennis 1", played = 4, points = 8),
        Standing(2, "s1", "Union Tischtennisclub St. Pölten Nord-West 1", played = 4, points = 7),
    ),
    lineup = listOf(LineupPlayer("pichler", "Katharina Habsburg-Lothringen-Esterházy")),
)
