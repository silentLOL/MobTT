import Foundation

/// What the share sheet sends for a Fixture.
enum FixtureShare {
    /// Everything the Fixture screen shows, one item per line and without markup so it reads the same in any app,
    /// ending with the Fixture `link`. The subject is the pairing. `locale` picks the language and the date format.
    static func content(
        detail: FixtureDetailUi,
        link: String,
        locale: Locale = .current,
        timeZone: TimeZone = .current
    ) -> ShareContent {
        let bundle = locale.language.languageCode
            .flatMap { Bundle.main.path(forResource: $0.identifier, ofType: "lproj") }
            .flatMap(Bundle.init(path:)) ?? .main
        func localized(_ value: String.LocalizationValue) -> String {
            String(localized: value, bundle: bundle, locale: locale)
        }

        let pairing = localized("fixture_detail_share_pairing \(detail.homeTeamName) \(detail.awayTeamName)")
        let result = detail.score.map { localized("fixture_detail_score \($0.home) \($0.away)") } ?? localized("fixture_detail_not_played")
        let kickoff = detail.startsAt.formatted(Date.FormatStyle(date: .abbreviated, time: .shortened, locale: locale, timeZone: timeZone))
        let header = [localized("fixture_detail_share_with_result \(pairing) \(result)"), kickoff] + [detail.venue, detail.address].compactMap { $0 }

        var blocks = [header]
        if detail.score != nil {
            let lines = detail.matches.map { match in
                let kind = match.isDoubles ? localized("fixture_detail_match_doubles") : localized("fixture_detail_match_singles")
                let line = localized("fixture_detail_share_match \(match.number) \(kind) \(match.homePlayers) \(match.awayPlayers)")
                guard let score = match.score else { return line }
                return localized("fixture_detail_share_with_result \(line) \(localized("fixture_detail_score \(score.home) \(score.away)"))")
            }
            blocks.append([localized("fixture_detail_matches_title")] + (lines.isEmpty ? [localized("fixture_detail_matches_empty_body")] : lines))
        }
        blocks.append([link])
        return ShareContent(subject: pairing, text: blocks.map { $0.joined(separator: "\n") }.joined(separator: "\n\n"))
    }
}
