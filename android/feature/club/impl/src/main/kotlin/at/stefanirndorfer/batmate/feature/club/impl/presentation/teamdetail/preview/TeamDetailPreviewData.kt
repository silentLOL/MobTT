package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.preview

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamDetail
import kotlin.time.Instant

private val team = Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga1")

private fun nextFixture(opponent: String, venue: String?) = NextFixture.Scheduled(
    fixture = Fixture(
        id = "f1",
        homeTeamId = "olympic-1",
        homeTeamName = "TTC Olympic 1",
        awayTeamId = "opponent",
        awayTeamName = opponent,
        startsAt = Instant.fromEpochSeconds(1_800_000_000),
        venue = venue,
        status = FixtureStatus.Scheduled,
    ),
    opponentName = opponent,
    isHome = true,
)

val teamDetailPreview = TeamDetail(
    team = team,
    clubName = "TTC Olympic",
    league = League("landesliga1", "1. Landesliga Herren"),
    standing = Standing(2, team.id, team.name, played = 6, points = 9),
    nextFixture = nextFixture("SK VÖEST Wien 1", "Sporthalle Olympic"),
    lineup = listOf(
        LineupPlayer("huber", "Michael Huber"),
        LineupPlayer("gruber", "Anna Gruber"),
        LineupPlayer("eder", "Thomas Eder"),
    ),
)

val teamDetailEmptyLineupPreview = teamDetailPreview.copy(lineup = emptyList())

val teamDetailFreeWeekPreview = teamDetailPreview.copy(nextFixture = NextFixture.FreeWeek)

val teamDetailNoLeaguePreview = teamDetailPreview.copy(league = null, standing = null, nextFixture = NextFixture.FreeWeek)

val teamDetailLongNamesPreview = TeamDetail(
    team = Team("s1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "g"),
    clubName = "Union Tischtennisclub St. Pölten Nord-West",
    league = League("g", "Gebietsliga Nord Herren Oberes Playoff"),
    standing = Standing(2, "s1", "Union Tischtennisclub St. Pölten Nord-West 1", played = 4, points = 7),
    nextFixture = nextFixture("Sportunion Hollabrunn Tischtennis 1", "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West"),
    lineup = listOf(LineupPlayer("pichler", "Katharina Habsburg-Lothringen-Esterházy")),
)
