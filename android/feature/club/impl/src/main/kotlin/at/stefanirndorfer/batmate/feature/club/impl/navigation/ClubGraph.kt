package at.stefanirndorfer.batmate.feature.club.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.club.api.ChooseClubDestination
import at.stefanirndorfer.batmate.feature.club.api.ClubDestination
import at.stefanirndorfer.batmate.feature.club.api.ClubDetailDestination
import at.stefanirndorfer.batmate.feature.club.api.LeagueDetailDestination
import at.stefanirndorfer.batmate.feature.club.api.TeamDetailDestination
import at.stefanirndorfer.batmate.feature.club.api.navigateToLeagueDetail
import at.stefanirndorfer.batmate.feature.club.api.navigateToTeamDetail
import at.stefanirndorfer.batmate.feature.club.impl.presentation.chooseclub.ChooseClubRoute
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.ClubRoute
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.LeagueDetailRoute
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.TeamDetailRoute
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import at.stefanirndorfer.batmate.feature.players.api.navigateToPlayerDetail

fun EntryProviderScope<NavKey>.clubGraph(navigator: GlobalNavigator) {
    entry<ClubDestination> {
        ClubRoute(
            source = ClubSource.Mine,
            onOpenTeam = navigator::navigateToTeamDetail,
            onOpenPlayer = navigator::navigateToPlayerDetail,
        )
    }
    entry<ClubDetailDestination> { destination ->
        ClubRoute(
            source = ClubSource.Specific(destination.clubId),
            onOpenTeam = navigator::navigateToTeamDetail,
            onOpenPlayer = navigator::navigateToPlayerDetail,
        )
    }
    entry<ChooseClubDestination> {
        ChooseClubRoute(onChosen = navigator::goBack)
    }
    entry<TeamDetailDestination> { destination ->
        TeamDetailRoute(
            teamId = destination.teamId,
            onOpenLeague = navigator::navigateToLeagueDetail,
            onOpenPlayer = navigator::navigateToPlayerDetail,
        )
    }
    entry<LeagueDetailDestination> { destination ->
        LeagueDetailRoute(
            leagueId = destination.leagueId,
            highlightTeamId = destination.highlightTeamId,
            onOpenTeam = navigator::navigateToTeamDetail,
        )
    }
}
