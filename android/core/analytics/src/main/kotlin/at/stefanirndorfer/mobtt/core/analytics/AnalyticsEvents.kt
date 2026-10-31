package at.stefanirndorfer.mobtt.core.analytics

/** Event names, parameter keys and values of `docs/ANALYTICS.md`. Keep this file and that catalog in step. */
object AnalyticsEvent {
    const val SCREEN_VIEW = "screen_view"
    const val MY_PLAYER_SELECTED = "my_player_selected"
    const val MY_CLUB_SELECTED = "my_club_selected"
    const val FAVORITE_TOGGLED = "favorite_toggled"
    const val DETAIL_OPENED = "detail_opened"
    const val SEARCH_PERFORMED = "search_performed"
    const val CONSENT_CHANGED = "consent_changed"
    const val SETTING_CHANGED = "setting_changed"
}

object AnalyticsParam {
    const val SCREEN = "screen"
    const val SOURCE = "source"
    const val KIND = "kind"
    const val ID = "id"
    const val ENABLED = "enabled"
    const val FROM = "from"
    const val QUERY_LENGTH = "query_length"
    const val SETTING = "setting"
    const val VALUE = "value"
}

object AnalyticsScreen {
    const val HOME = "home"
    const val PLAYERS = "players"
    const val CHOOSE_PLAYER = "choose_player"
    const val CHOOSE_CLUB = "choose_club"
    const val CLUB = "club"
    const val PLAYER_DETAIL = "player_detail"
    const val TEAM_DETAIL = "team_detail"
    const val LEAGUE_DETAIL = "league_detail"
    const val SETTINGS = "settings"
}

object AnalyticsSetting {
    const val APPEARANCE = "appearance"
}

object AnalyticsKind {
    const val PLAYER = "player"
    const val CLUB = "club"
    const val TEAM = "team"
    const val LEAGUE = "league"
}

object AnalyticsSource {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val PROMPT = "prompt"
}

fun AnalyticsTracker.screenView(screen: String) = logEvent(AnalyticsEvent.SCREEN_VIEW, mapOf(AnalyticsParam.SCREEN to screen))

fun AnalyticsTracker.myPlayerSelected(source: String) = logEvent(AnalyticsEvent.MY_PLAYER_SELECTED, mapOf(AnalyticsParam.SOURCE to source))

fun AnalyticsTracker.myClubSelected(source: String) = logEvent(AnalyticsEvent.MY_CLUB_SELECTED, mapOf(AnalyticsParam.SOURCE to source))

fun AnalyticsTracker.favoriteToggled(kind: String, id: String, enabled: Boolean) = logEvent(
    AnalyticsEvent.FAVORITE_TOGGLED,
    mapOf(AnalyticsParam.KIND to kind, AnalyticsParam.ID to id, AnalyticsParam.ENABLED to enabled.toString()),
)

fun AnalyticsTracker.detailOpened(kind: String, id: String, from: String) = logEvent(
    AnalyticsEvent.DETAIL_OPENED,
    mapOf(AnalyticsParam.KIND to kind, AnalyticsParam.ID to id, AnalyticsParam.FROM to from),
)

/** Sends the length of [query], never the text. */
fun AnalyticsTracker.searchPerformed(screen: String, query: String) = logEvent(
    AnalyticsEvent.SEARCH_PERFORMED,
    mapOf(AnalyticsParam.SCREEN to screen, AnalyticsParam.QUERY_LENGTH to query.trim().length.toLong()),
)

fun AnalyticsTracker.consentChanged(enabled: Boolean, source: String) = logEvent(
    AnalyticsEvent.CONSENT_CHANGED,
    mapOf(AnalyticsParam.ENABLED to enabled.toString(), AnalyticsParam.SOURCE to source),
)

fun AnalyticsTracker.settingChanged(setting: String, value: String) = logEvent(
    AnalyticsEvent.SETTING_CHANGED,
    mapOf(AnalyticsParam.SETTING to setting, AnalyticsParam.VALUE to value),
)
