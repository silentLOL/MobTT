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
        let text = Strings(locale: locale)
        var blocks = [resultAndKickoff(detail, text, timeZone) + [detail.venue, detail.address].compactMap { $0 }]
        if detail.score != nil {
            let lines = detail.matches.map { match in
                let kind = match.isDoubles ? text("fixture_detail_match_doubles") : text("fixture_detail_match_singles")
                let line = text("fixture_detail_share_match \(match.number) \(kind) \(match.homePlayers) \(match.awayPlayers)")
                guard let score = match.score else { return line }
                return text("fixture_detail_share_with_result \(line) \(text("fixture_detail_score \(score.home) \(score.away)"))")
            }
            blocks.append([text("fixture_detail_matches_title")] + (lines.isEmpty ? [text("fixture_detail_matches_empty_body")] : lines))
        }
        blocks.append([link])
        return share(detail, blocks, text)
    }

    /// The short text next to the share image, which carries the rest: the result, the kickoff and the Fixture `link`.
    static func caption(
        detail: FixtureDetailUi,
        link: String,
        locale: Locale = .current,
        timeZone: TimeZone = .current
    ) -> ShareContent {
        let text = Strings(locale: locale)
        return share(detail, [resultAndKickoff(detail, text, timeZone), [link]], text)
    }

    private static func share(_ detail: FixtureDetailUi, _ blocks: [[String]], _ text: Strings) -> ShareContent {
        ShareContent(subject: pairing(detail, text), text: blocks.map { $0.joined(separator: "\n") }.joined(separator: "\n\n"))
    }

    private static func pairing(_ detail: FixtureDetailUi, _ text: Strings) -> String {
        text("fixture_detail_share_pairing \(detail.homeTeamName) \(detail.awayTeamName)")
    }

    /// The pairing with the score, or "Not played yet", then the kickoff as the Fixture card shows it.
    private static func resultAndKickoff(_ detail: FixtureDetailUi, _ text: Strings, _ timeZone: TimeZone) -> [String] {
        let result = detail.score.map { text("fixture_detail_score \($0.home) \($0.away)") } ?? text("fixture_detail_not_played")
        let kickoff = detail.startsAt.formatted(Date.FormatStyle(date: .abbreviated, time: .shortened, locale: text.locale, timeZone: timeZone))
        return [text("fixture_detail_share_with_result \(pairing(detail, text)) \(result)"), kickoff]
    }

    /// Looks strings up in `locale`'s language, so the text does not depend on the device's.
    private struct Strings {
        let locale: Locale
        private let bundle: Bundle

        init(locale: Locale) {
            self.locale = locale
            bundle = locale.language.languageCode
                .flatMap { Bundle.main.path(forResource: $0.identifier, ofType: "lproj") }
                .flatMap(Bundle.init(path:)) ?? .main
        }

        func callAsFunction(_ value: String.LocalizationValue) -> String {
            String(localized: value, bundle: bundle, locale: locale)
        }
    }
}
