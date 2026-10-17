package at.stefanirndorfer.batmate.feature.club.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

/** The Club tab: shows the Club derived from My Player or My Club. */
@Serializable
data object ClubDestination : GlobalAppDestination

/** A specific Club pushed on top of the current tab, e.g. from a Favorite. */
@Serializable
data class ClubDetailDestination(val clubId: String) : GlobalAppDestination

fun GlobalNavigator.navigateToClub() = navigateTopLevel(ClubDestination)

fun GlobalNavigator.navigateToClubDetail(clubId: String) = navigate(ClubDetailDestination(clubId))
