/// Event names, parameter keys and values of `docs/ANALYTICS.md`. Keep this file and that catalog in step.
enum AnalyticsEvent {
    static let screenView = "screen_view"
    static let myPlayerSelected = "my_player_selected"
    static let myClubSelected = "my_club_selected"
    static let favoriteToggled = "favorite_toggled"
    static let detailOpened = "detail_opened"
    static let searchPerformed = "search_performed"
    static let consentChanged = "consent_changed"
    static let settingChanged = "setting_changed"
}

enum AnalyticsParam {
    static let screen = "screen"
    static let source = "source"
    static let kind = "kind"
    static let id = "id"
    static let enabled = "enabled"
    static let from = "from"
    static let queryLength = "query_length"
    static let setting = "setting"
    static let value = "value"
}

enum AnalyticsScreen {
    static let home = "home"
    static let players = "players"
    static let choosePlayer = "choose_player"
    static let chooseClub = "choose_club"
    static let club = "club"
    static let playerDetail = "player_detail"
    static let teamDetail = "team_detail"
    static let leagueDetail = "league_detail"
    static let settings = "settings"
}

enum AnalyticsSetting {
    static let appearance = "appearance"
}

enum AnalyticsKind {
    static let player = "player"
    static let club = "club"
    static let team = "team"
    static let league = "league"
}

enum AnalyticsSource {
    static let home = "home"
    static let settings = "settings"
    static let prompt = "prompt"
}

extension AnalyticsTracker {
    func screenView(_ screen: String) {
        logEvent(AnalyticsEvent.screenView, parameters: [AnalyticsParam.screen: screen])
    }

    func myPlayerSelected(source: String) {
        logEvent(AnalyticsEvent.myPlayerSelected, parameters: [AnalyticsParam.source: source])
    }

    func myClubSelected(source: String) {
        logEvent(AnalyticsEvent.myClubSelected, parameters: [AnalyticsParam.source: source])
    }

    func favoriteToggled(kind: String, id: String, enabled: Bool) {
        logEvent(AnalyticsEvent.favoriteToggled, parameters: [
            AnalyticsParam.kind: kind,
            AnalyticsParam.id: id,
            AnalyticsParam.enabled: String(enabled),
        ])
    }

    func detailOpened(kind: String, id: String, from: String) {
        logEvent(AnalyticsEvent.detailOpened, parameters: [
            AnalyticsParam.kind: kind,
            AnalyticsParam.id: id,
            AnalyticsParam.from: from,
        ])
    }

    /// Sends the length of `query`, never the text.
    func searchPerformed(screen: String, query: String) {
        logEvent(AnalyticsEvent.searchPerformed, parameters: [
            AnalyticsParam.screen: screen,
            AnalyticsParam.queryLength: query.trimmingCharacters(in: .whitespacesAndNewlines).count,
        ])
    }

    func settingChanged(setting: String, value: String) {
        logEvent(AnalyticsEvent.settingChanged, parameters: [
            AnalyticsParam.setting: setting,
            AnalyticsParam.value: value,
        ])
    }

    func consentChanged(enabled: Bool, source: String) {
        logEvent(AnalyticsEvent.consentChanged, parameters: [
            AnalyticsParam.enabled: String(enabled),
            AnalyticsParam.source: source,
        ])
    }
}
