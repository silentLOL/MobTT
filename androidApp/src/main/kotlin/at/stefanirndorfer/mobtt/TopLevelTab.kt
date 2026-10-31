package at.stefanirndorfer.mobtt

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.feature.club.api.ClubDestination
import at.stefanirndorfer.mobtt.feature.home.api.HomeDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.players.api.PlayersDestination

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
