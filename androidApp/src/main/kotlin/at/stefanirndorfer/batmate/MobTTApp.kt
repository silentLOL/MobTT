package at.stefanirndorfer.batmate

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatTheme
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatNavigationBar
import at.stefanirndorfer.batmate.designsystem.ui.components.BatNavigationItem
import at.stefanirndorfer.batmate.designsystem.ui.components.BatScreenScaffold
import at.stefanirndorfer.batmate.designsystem.ui.components.BatTopBar
import at.stefanirndorfer.batmate.designsystem.ui.components.BatTopBarBack
import at.stefanirndorfer.batmate.feature.club.impl.navigation.clubGraph
import at.stefanirndorfer.batmate.feature.home.api.HomeDestination
import at.stefanirndorfer.batmate.feature.home.impl.navigation.homeGraph
import at.stefanirndorfer.batmate.feature.navigation.BackStackGlobalNavigator
import at.stefanirndorfer.batmate.feature.players.impl.navigation.playersGraph
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.consent.UsageConsentSheet
import at.stefanirndorfer.batmate.feature.settings.impl.presentation.settings.SettingsSheet

@Composable
fun MobTTApp(modifier: Modifier = Modifier) {
    val navigator = remember { BackStackGlobalNavigator(HomeDestination) }
    BatScreenScaffold(
        modifier = modifier,
        topBar = {
            BatTopBar(
                title = stringResource(R.string.core_app_title),
                back = if (navigator.backStack.size > 1) {
                    BatTopBarBack(stringResource(R.string.core_topbar_back_cd), onClick = navigator::goBack)
                } else {
                    null
                },
                actions = {
                    BatIconButton(
                        icon = BatIcon.Settings,
                        contentDescription = stringResource(R.string.core_settings_button_cd),
                        onClick = navigator::openSettings,
                    )
                },
            )
        },
        bottomBar = {
            BatNavigationBar {
                topLevelTabs.forEach { tab ->
                    BatNavigationItem(
                        icon = tab.icon,
                        label = stringResource(tab.label),
                        selected = navigator.topLevelDestination == tab.destination,
                        onClick = { navigator.navigateTopLevel(tab.destination) },
                    )
                }
            }
        },
    ) { padding ->
        NavDisplay(
            backStack = navigator.backStack,
            onBack = navigator::goBack,
            modifier = Modifier.padding(padding),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider<NavKey> {
                homeGraph(navigator)
                clubGraph(navigator)
                playersGraph(navigator)
            },
        )
    }
    if (navigator.isSettingsOpen) {
        SettingsSheet(onDismiss = navigator::closeSettings)
    }
    UsageConsentSheet()
}

@BatScreenPreview
@Composable
private fun MobTTAppPreview() {
    BatTheme {
        MobTTApp()
    }
}
