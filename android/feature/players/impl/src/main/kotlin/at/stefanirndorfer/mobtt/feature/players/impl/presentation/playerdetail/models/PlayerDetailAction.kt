package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

sealed interface PlayerDetailAction {
    data object FavoriteClicked : PlayerDetailAction

    data object ClubClicked : PlayerDetailAction

    data class TeamClicked(val teamId: String) : PlayerDetailAction

    data object ClubRetryClicked : PlayerDetailAction

    data object TeamsRetryClicked : PlayerDetailAction

    data object RatingHistoryRetryClicked : PlayerDetailAction

    data class RatingHistorySpanChosen(val span: RatingHistorySpan) : PlayerDetailAction

    data object RatingInfoClicked : PlayerDetailAction

    data object RatingInfoDismissed : PlayerDetailAction

    data object RatingInfoLinkClicked : PlayerDetailAction
}
