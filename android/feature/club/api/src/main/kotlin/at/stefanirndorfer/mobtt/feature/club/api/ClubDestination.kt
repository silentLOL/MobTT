package at.stefanirndorfer.mobtt.feature.club.api

import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

/** The Club tab: shows the Club derived from My Player or My Club. */
@Serializable
data object ClubDestination : GlobalAppDestination

/** A specific Club pushed on top of the current tab, e.g. from a Favorite. */
@Serializable
data class ClubDetailDestination(val clubId: String) : GlobalAppDestination

/** The Association → Club search where choosing a Club makes it My Club; pushed from Home's choose prompt. */
@Serializable
data object ChooseClubDestination : GlobalAppDestination

@Serializable
data class TeamDetailDestination(val teamId: String) : GlobalAppDestination

/** [highlightTeamId] marks that Team in the table and the Fixtures, e.g. when opened from its Team screen. */
@Serializable
data class LeagueDetailDestination(val leagueId: String, val highlightTeamId: String? = null) : GlobalAppDestination

fun GlobalNavigator.navigateToClub() = navigateTopLevel(ClubDestination)

fun GlobalNavigator.navigateToClubDetail(clubId: String) = navigate(ClubDetailDestination(clubId))

fun GlobalNavigator.navigateToTeamDetail(teamId: String) = navigate(TeamDetailDestination(teamId))

fun GlobalNavigator.navigateToLeagueDetail(leagueId: String, highlightTeamId: String? = null) = navigate(LeagueDetailDestination(leagueId, highlightTeamId))

fun GlobalNavigator.navigateToChooseClub() = navigate(ChooseClubDestination)
