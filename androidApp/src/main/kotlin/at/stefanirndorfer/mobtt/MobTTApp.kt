package at.stefanirndorfer.mobtt

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.core.ui.ObserveAsEvents
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatTheme
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatNavigationBar
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatNavigationItem
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatScreenScaffold
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatTopBar
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatTopBarBack
import at.stefanirndorfer.mobtt.feature.club.api.navigateToFixtureFromLink
import at.stefanirndorfer.mobtt.feature.club.impl.navigation.clubGraph
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.FixtureLinkViewModel
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkAction
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturelink.models.FixtureLinkEvent
import at.stefanirndorfer.mobtt.feature.home.api.HomeDestination
import at.stefanirndorfer.mobtt.feature.home.impl.navigation.homeGraph
import at.stefanirndorfer.mobtt.feature.navigation.BackStackGlobalNavigator
import at.stefanirndorfer.mobtt.feature.players.impl.navigation.playersGraph
import at.stefanirndorfer.mobtt.feature.settings.api.SettingsDestination
import at.stefanirndorfer.mobtt.feature.settings.api.navigateToSettings
import at.stefanirndorfer.mobtt.feature.settings.impl.navigation.settingsGraph
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.consent.UsageConsentSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.androidx.compose.koinViewModel

/** [incomingLinks] are the links the app is opened with; a Fixture link opens that Fixture on top of Home. */
@Composable
fun MobTTApp(
    modifier: Modifier = Modifier,
    incomingLinks: Flow<IncomingLink> = emptyFlow(),
    fixtureLinkViewModel: FixtureLinkViewModel = koinViewModel(),
) {
    val navigator = remember { BackStackGlobalNavigator(HomeDestination) }
    LaunchedEffect(incomingLinks) {
        incomingLinks.collect { fixtureLinkViewModel.onAction(FixtureLinkAction.LinkReceived(it.url, it.coldStart)) }
    }
    ObserveAsEvents(fixtureLinkViewModel.events) { event ->
        when (event) {
            is FixtureLinkEvent.OpenFixture -> navigator.navigateToFixtureFromLink(event.fixtureId)
        }
    }
    // The chevron goes through the back dispatcher, like the system Back, so a screen can handle it first (Settings'
    // picker steps back before Settings closes). With no screen handling it, the NavDisplay below pops the back stack.
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BatScreenScaffold(
        modifier = modifier,
        topBar = {
            BatTopBar(
                title = stringResource(R.string.core_app_title),
                back = if (navigator.backStack.size > 1) {
                    BatTopBarBack(
                        stringResource(R.string.core_topbar_back_cd),
                        onClick = { backDispatcher?.onBackPressed() ?: navigator.goBack() },
                    )
                } else {
                    null
                },
                actions = {
                    if (navigator.backStack.lastOrNull() != SettingsDestination) {
                        BatIconButton(
                            icon = BatIcon.Settings,
                            contentDescription = stringResource(R.string.core_settings_button_cd),
                            onClick = navigator::navigateToSettings,
                        )
                    }
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
                settingsGraph(navigator)
            },
        )
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
