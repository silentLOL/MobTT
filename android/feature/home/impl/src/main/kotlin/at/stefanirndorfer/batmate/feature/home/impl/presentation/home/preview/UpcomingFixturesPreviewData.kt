package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.preview

import at.stefanirndorfer.batmate.domain.club.Fixture
import at.stefanirndorfer.batmate.domain.club.FixtureStatus
import at.stefanirndorfer.batmate.domain.club.NextFixture
import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.user.UpcomingFixture
import at.stefanirndorfer.batmate.domain.user.UpcomingFixtures
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

private val team1 = Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga1")
private val team2 = Team("olympic-2", "TTC Olympic 2", "olympic", "landesliga2")
private val team3 = Team("olympic-3", "TTC Olympic 3", "olympic", "landesliga2")

val upcomingFixturesPreview = UpcomingFixtures.Loaded(
    listOf(
        UpcomingFixture(team1, scheduled(team1, "SK VÖEST Wien 1", isHome = true, venue = "Sporthalle Olympic")),
        UpcomingFixture(team2, scheduled(team2, "Nussdorf TTC 2", isHome = false, venue = null)),
        UpcomingFixture(team3, NextFixture.FreeWeek),
    ),
)
