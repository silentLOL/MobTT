package at.stefanirndorfer.mobtt.feature.club.api

import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

/** The Club tab: shows the Club derived from My Player or My Club. */
@Serializable
data object ClubDestination : GlobalAppDestination

/** The League tab: the League of My Player's Team, or why there is none. */
@Serializable
data object LeagueTabDestination : GlobalAppDestination

/** A specific Club pushed on top of the current tab, e.g. from a Favorite. */
@Serializable
data class ClubDetailDestination(val clubId: String) : GlobalAppDestination

/** The Association → Club search where choosing a Club makes it My Club; pushed from Home's choose prompt. */
@Serializable
data object ChooseClubDestination : GlobalAppDestination

@Serializable
data class TeamDetailDestination(val teamId: String) : GlobalAppDestination

/**
 * [highlightTeamId] marks that Team in the table and the Fixtures. [openedFromTeamId] is the Team whose screen pushed
 * this League, so its row does not open it again.
 */
@Serializable
data class LeagueDetailDestination(
    val leagueId: String,
    val highlightTeamId: String? = null,
    val openedFromTeamId: String? = null,
) : GlobalAppDestination

/** A Fixture with its Matches, pushed from a Fixture row. */
@Serializable
data class FixtureDetailDestination(val fixtureId: String) : GlobalAppDestination

fun GlobalNavigator.navigateToClub() = navigateTopLevel(ClubDestination)

fun GlobalNavigator.navigateToClubDetail(clubId: String) = navigate(ClubDetailDestination(clubId))

fun GlobalNavigator.navigateToTeamDetail(teamId: String) = navigate(TeamDetailDestination(teamId))

fun GlobalNavigator.navigateToLeagueDetail(leagueId: String, highlightTeamId: String? = null) = navigate(LeagueDetailDestination(leagueId, highlightTeamId))

/** The League of [teamId], opened from that Team's screen: the Team is highlighted and its row is not a link. */
fun GlobalNavigator.navigateToLeagueDetailFromTeam(leagueId: String, teamId: String) = navigate(LeagueDetailDestination(leagueId, highlightTeamId = teamId, openedFromTeamId = teamId))

fun GlobalNavigator.navigateToChooseClub() = navigate(ChooseClubDestination)

fun GlobalNavigator.navigateToFixtureDetail(fixtureId: String) = navigate(FixtureDetailDestination(fixtureId))
