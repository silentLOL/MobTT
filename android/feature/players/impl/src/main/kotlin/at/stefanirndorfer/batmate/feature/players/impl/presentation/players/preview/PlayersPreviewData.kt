package at.stefanirndorfer.batmate.feature.players.impl.presentation.players.preview

import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubSheetKind
import at.stefanirndorfer.batmate.core.ui.directory.models.AssociationClubSheetUi
import at.stefanirndorfer.batmate.core.ui.directory.models.DirectoryEntryUi
import at.stefanirndorfer.batmate.core.ui.directory.models.PickedUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayerEntryUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersListUi
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.models.PlayersUiState

private val association = PickedUi("191", "Oberösterreichischer Tischtennisverband")
private val club = PickedUi("140", "Union Tischtennisclub St. Pölten Nord-West")

val playersNoAssociationPreview = PlayersUiState()

val playersNoClubPreview = PlayersUiState(association = association)

val playersLoadedPreview = PlayersUiState(
    association = association,
    club = club,
    list = PlayersListUi.Loaded(
        listOf(
            PlayerEntryUi("gruber", "Anna Gruber", rcPoints = 1992),
            PlayerEntryUi("pichler", "Katharina Habsburg-Lothringen-Esterházy", rcPoints = null),
        ),
    ),
)

val playersNoMatchesPreview = playersLoadedPreview.copy(query = "zzz", list = PlayersListUi.Loaded(emptyList()))

val playersLoadingPreview = playersLoadedPreview.copy(list = PlayersListUi.Loading)

val playersUnavailablePreview = playersLoadedPreview.copy(list = PlayersListUi.Unavailable)

val playersAssociationPickerPreview = playersNoAssociationPreview.copy(
    picker = AssociationClubSheetUi(
        kind = AssociationClubSheetKind.ASSOCIATION,
        query = "",
        entries = listOf(
            DirectoryEntryUi("191", "Oberösterreichischer Tischtennisverband", subtitle = null, selected = true),
            DirectoryEntryUi("196", "Wiener Tischtennis-Verband", subtitle = null, selected = false),
        ),
        isLoading = false,
    ),
)
