package at.stefanirndorfer.batmate

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.feature.club.api.ClubDestination
import at.stefanirndorfer.batmate.feature.home.api.HomeDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.players.api.PlayersDestination

data class TopLevelTab(
    val destination: GlobalAppDestination,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
)

val topLevelTabs = listOf(
    TopLevelTab(HomeDestination, R.string.core_tab_home_label, BatIcon.Home),
    TopLevelTab(ClubDestination, R.string.core_tab_club_label, BatIcon.Club),
    TopLevelTab(PlayersDestination, R.string.core_tab_players_label, BatIcon.Search),
)
