package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview

import at.stefanirndorfer.batmate.domain.club.Association
import at.stefanirndorfer.batmate.domain.club.Club
import at.stefanirndorfer.batmate.domain.club.ClubOverview
import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.domain.club.League
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.domain.club.Standing
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.club.TeamOverview
import kotlin.time.Instant

private val kickoff = Instant.fromEpochSeconds(1_800_000_000)

private fun scheduled(team: Team, opponent: String, isHome: Boolean, venue: String?) = NextFixture.Scheduled(
    fixture = Fixture(
        id = "${team.id}-next",
        homeTeamId = if (isHome) team.id else "opponent",
        homeTeamName = if (isHome) team.name else opponent,
        awayTeamId = if (isHome) "opponent" else team.id,
        awayTeamName = if (isHome) opponent else team.name,
        startsAt = kickoff,
        venue = venue,
        status = FixtureStatus.Scheduled,
    ),
    opponentName = opponent,
    isHome = isHome,
)

private val club = Club("olympic", "TTC Olympic", "wttv")
private val association = Association("wttv", "Wiener Tischtennisverband", "WTTV")

private val homeTeam = Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga1")
private val cupTeam = Team("olympic-1-cup", "TTC Olympic 1", "olympic", "cup")
private val awayTeam = Team("olympic-2", "TTC Olympic 2", "olympic", "landesliga2")
private val freeWeekTeam = Team("olympic-3", "TTC Olympic 3", "olympic", "landesliga2")

val clubOverviewPreview = ClubOverview(
    club = club,
    association = association,
    teams = listOf(
        TeamOverview(
            team = homeTeam,
            league = League("landesliga1", "1. Landesliga Herren"),
            standing = Standing(2, homeTeam.id, homeTeam.name, played = 6, points = 9),
            nextFixture = scheduled(homeTeam, "SK VÖEST Wien 1", isHome = true, venue = "Sporthalle Olympic"),
        ),
        TeamOverview(
            team = cupTeam,
            league = League("cup", "Herren-Cup"),
            standing = null,
            nextFixture = scheduled(cupTeam, "Nussdorf TTC 1", isHome = false, venue = "Sporthalle Nussdorf"),
        ),
        TeamOverview(
            team = awayTeam,
            league = League("landesliga2", "2. Landesliga Herren"),
            standing = Standing(2, awayTeam.id, awayTeam.name, played = 5, points = 6),
            nextFixture = scheduled(awayTeam, "Nussdorf TTC 2", isHome = false, venue = null),
        ),
        TeamOverview(
            team = freeWeekTeam,
            league = League("landesliga2", "2. Landesliga Herren"),
            standing = Standing(3, freeWeekTeam.id, freeWeekTeam.name, played = 5, points = 3),
            nextFixture = NextFixture.FreeWeek,
        ),
    ),
    players = listOf(
        LineupPlayer("huber", "Michael Huber", rcPoints = 2114),
        LineupPlayer("gruber", "Anna Gruber", rcPoints = 1992),
        LineupPlayer("eder", "Thomas Eder"),
    ),
)

val clubOverviewLongNamesPreview = ClubOverview(
    club = Club("stpoelten", "Union Tischtennisclub St. Pölten Nord-West", "noettv"),
    association = Association("noettv", "Niederösterreichischer Tischtennisverband", "NÖTTV"),
    teams = listOf(
        Team("s1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "g").let { team ->
            TeamOverview(
                team = team,
                league = League("g", "Gebietsliga Nord Herren Oberes Playoff"),
                standing = Standing(1, team.id, team.name, played = 4, points = 8),
                nextFixture = scheduled(team, "Sportunion Hollabrunn Tischtennis 1", isHome = false, venue = "Sporthalle Hollabrunn Mitte"),
            )
        },
    ),
    players = listOf(LineupPlayer("pichler", "Katharina Habsburg-Lothringen-Esterházy", rcPoints = 1750)),
)

val clubOverviewNoTeamsPreview = ClubOverview(club, association, teams = emptyList(), players = emptyList())

val clubOverviewNoLeaguePreview = ClubOverview(
    club = club,
    association = association,
    teams = listOf(
        TeamOverview(team = homeTeam, league = null, standing = null, nextFixture = NextFixture.FreeWeek),
    ),
    players = emptyList(),
)
