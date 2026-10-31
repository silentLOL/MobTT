package at.stefanirndorfer.batmate.feature.home.impl.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.core.ui.ObserveAsEvents
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeAction
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeEvent
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeSelectionUi
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeUiState
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views.FavoritePickerSheet
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views.FavoriteRowView
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views.MySelectionView
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onOpenSettings: () -> Unit,
    onOpenClub: (String) -> Unit,
    onOpenPlayer: (String) -> Unit,
    onOpenTeam: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.OpenSettings -> onOpenSettings()
            is HomeEvent.OpenClub -> onOpenClub(event.clubId)
            is HomeEvent.OpenPlayer -> onOpenPlayer(event.playerId)
            is HomeEvent.OpenTeam -> onOpenTeam(event.teamId)
        }
    }
    HomeScreen(state = uiState, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        LoadingView(modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(BatDesign.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg),
        ) {
            MySelectionView(
                selection = state.selection,
                onChooseClick = { onAction(HomeAction.ChooseClicked) },
                onPlayerClick = { onAction(HomeAction.HeroPlayerClicked(it)) },
                onClubClick = { onAction(HomeAction.HeroClubClicked(it)) },
            )
            Column {
                BatSectionHeader(
                    title = stringResource(R.string.home_favorites_title),
                    actionLabel = stringResource(R.string.home_favorites_add_button),
                    onAction = { onAction(HomeAction.AddFavoriteClicked) },
                )
                if (state.favorites.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_favorites_empty_body),
                        color = BatDesign.colors.textSecondary,
                        style = BatDesign.typography.body,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
                    state.favorites.forEach { favorite ->
                        FavoriteRowView(
                            favorite = favorite,
                            onClick = when (val item = favorite.favorite) {
                                is Favorite.Club -> {
                                    { onAction(HomeAction.FavoriteClubClicked(item.id)) }
                                }
                                is Favorite.Team -> {
                                    { onAction(HomeAction.FavoriteTeamClicked(item.id)) }
                                }
                                is Favorite.Player -> {
                                    { onAction(HomeAction.FavoritePlayerClicked(item.id)) }
                                }
                            },
                            onRemoveClick = { onAction(HomeAction.RemoveFavoriteClicked(favorite.favorite)) },
                        )
                    }
                }
            }
        }
    }
    if (state.picker != null) {
        FavoritePickerSheet(
            picker = state.picker,
            onQueryChange = { onAction(HomeAction.PickerQueryChanged(it)) },
            onBrowsePlayersClick = { onAction(HomeAction.PickerBrowsePlayersClicked) },
            onPlayerClick = { onAction(HomeAction.PickerPlayerClicked(it)) },
            onClubClick = { onAction(HomeAction.PickerClubClicked(it)) },
            onBackClick = { onAction(HomeAction.PickerBackClicked) },
            onDismiss = { onAction(HomeAction.PickerDismissed) },
        )
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.home_loading_cd)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingLabel },
            color = BatDesign.colors.brand,
        )
    }
}

private val loadedState = HomeUiState(
    selection = HomeSelectionUi.Player("Michael Huber", "TTC Olympic", "Wiener Tischtennisverband (WTTV)", "p1"),
    favorites = listOf(
        ResolvedFavorite(Favorite.Player("gruber"), "Anna Gruber", "TTC Olympic"),
        ResolvedFavorite(Favorite.Club("nussdorf"), "Nussdorf TTC", "Wiener Tischtennisverband (WTTV)"),
    ),
)

@BatScreenPreview
@Composable
private fun HomeScreenLoadingPreview() {
    BatPreviewSurface {
        HomeScreen(state = HomeUiState(isLoading = true), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun HomeScreenEmptyPreview() {
    BatPreviewSurface {
        HomeScreen(state = HomeUiState(), onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun HomeScreenLoadedPreview() {
    BatPreviewSurface {
        HomeScreen(state = loadedState, onAction = {})
    }
}

@BatScreenPreview
@Composable
private fun HomeScreenLongNamePreview() {
    BatPreviewSurface {
        HomeScreen(
            state = HomeUiState(
                selection = HomeSelectionUi.Player(
                    "Katharina Habsburg-Lothringen-Esterházy",
                    "Union Tischtennisclub St. Pölten Nord-West",
                    "Niederösterreichischer Tischtennisverband (NÖTTV)",
                    "p2",
                ),
                favorites = listOf(
                    ResolvedFavorite(
                        Favorite.Player("pichler"),
                        "Katharina Habsburg-Lothringen-Esterházy",
                        "Union Tischtennisclub St. Pölten Nord-West",
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

@BatScreenPreview
@Composable
private fun HomeScreenMyClubPreview() {
    BatPreviewSurface {
        HomeScreen(
            state = loadedState.copy(selection = HomeSelectionUi.Club("Nussdorf TTC", "Wiener Tischtennisverband (WTTV)", "c1")),
            onAction = {},
        )
    }
}
