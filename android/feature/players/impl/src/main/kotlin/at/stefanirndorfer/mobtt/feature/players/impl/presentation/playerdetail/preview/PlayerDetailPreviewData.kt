package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview

import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState

private val anna = Player("anna", "Anna Gruber", "olympic")

private val olympic = SectionUi.Loaded(PlayerClub("TTC Olympic", "WTTV"))

val playerDetailPreview = PlayerDetailUiState.Content(
    player = anna,
    club = olympic,
    teams = SectionUi.Loaded(
        PlayerTeams(
            listOf(
                Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga"),
                Team("olympic-3", "TTC Olympic 3", "olympic", "bezirksliga"),
            ),
            rcPoints = 1992,
            ageGroup = "U171",
        ),
    ),
)

val playerDetailSectionsLoadingPreview = PlayerDetailUiState.Content(anna)

val playerDetailSectionsUnavailablePreview = PlayerDetailUiState.Content(anna, club = SectionUi.Unavailable, teams = SectionUi.Unavailable)

val playerDetailNoTeamsPreview = PlayerDetailUiState.Content(anna, club = olympic, teams = SectionUi.Loaded(PlayerTeams()))

val playerDetailNoClubPreview = PlayerDetailUiState.Content(anna, club = SectionUi.Loaded(null), teams = SectionUi.Loaded(PlayerTeams()))

val playerDetailLongNamesPreview = PlayerDetailUiState.Content(
    player = Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
    club = SectionUi.Loaded(PlayerClub("Union Tischtennisclub St. Pölten Nord-West", "Niederösterreichischer Tischtennisverband")),
    teams = SectionUi.Loaded(
        PlayerTeams(listOf(Team("stpoelten-1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "bundesliga"))),
    ),
)
