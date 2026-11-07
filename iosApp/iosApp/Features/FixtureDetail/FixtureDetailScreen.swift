import SwiftUI

struct FixtureDetailRoute: View {
    @State private var viewModel: FixtureDetailViewModel
    @State private var share: ShareContent?
    @Environment(\.displayScale) private var displayScale

    let onOpenTeam: (_ teamId: String) -> Void

    init(fixtureId: String, onOpenTeam: @escaping (_ teamId: String) -> Void) {
        self.onOpenTeam = onOpenTeam
        _viewModel = State(initialValue: FixtureDetailViewModel(fixtureId: fixtureId))
    }

    var body: some View {
        FixtureDetailScreen(state: viewModel.uiState, onAction: viewModel.onAction)
            .onAppear { viewModel.screenShown() }
            .onAppear { viewModel.loadIfNeeded() }
            .task {
                for await event in viewModel.events {
                    switch event {
                    case .openTeam(let teamId): onOpenTeam(teamId)
                    case .share(let detail, let link): share = shareContent(detail: detail, link: link)
                    }
                }
            }
            .sheet(item: $share) { content in
                ShareSheetView(content: content)
                    .presentationDetents([.medium, .large])
            }
    }
}

extension FixtureDetailRoute {
    /// The picture with a short caption; the full text alone when the picture cannot be drawn.
    private func shareContent(detail: FixtureDetailUi, link: String?) -> ShareContent {
        guard let image = FixtureShareImageView.render(detail: detail, link: link, scale: displayScale) else {
            return FixtureShare.content(detail: detail, link: link)
        }
        var caption = FixtureShare.caption(detail: detail, link: link)
        caption.image = image
        return caption
    }
}

struct FixtureDetailScreen: View {
    let state: FixtureDetailUiState
    let onAction: (FixtureDetailAction) -> Void

    var body: some View {
        switch state {
        case .loading:
            ProgressView()
                .tint(BatColor.brand)
                .accessibilityLabel(Text("fixture_detail_loading_cd"))
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        case .error:
            ScrollView {
                VStack(spacing: BatSpacing.md) {
                    BatEmptyStateView(
                        icon: .club,
                        title: String(localized: "fixture_detail_error_title"),
                        message: String(localized: "fixture_detail_error_body")
                    )
                    BatPrimaryButtonView(title: String(localized: "fixture_detail_error_retry_button")) {
                        onAction(.retryClicked)
                    }
                }
                .padding(BatSpacing.lg)
            }
            .onAppear { AccessibilityNotification.Announcement(String(localized: "fixture_detail_error_body")).post() }
        case .content(let detail):
            ContentView(detail: detail, onAction: onAction)
        }
    }
}

private struct ContentView: View {
    let detail: FixtureDetailUi
    let onAction: (FixtureDetailAction) -> Void

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: BatSpacing.lg) {
                header
                if detail.score != nil {
                    VStack(alignment: .leading, spacing: 0) {
                        BatSectionHeaderView(title: String(localized: "fixture_detail_matches_title"))
                        if detail.matches.isEmpty {
                            Text("fixture_detail_matches_empty_body")
                                .batTextStyle(BatTypography.body)
                                .foregroundStyle(BatColor.textSecondary)
                        } else {
                            BatListView {
                                ForEach(Array(detail.matches.enumerated()), id: \.element.id) { index, row in
                                    MatchRowView(row: row, isStriped: !index.isMultiple(of: 2))
                                }
                            }
                        }
                    }
                }
            }
            .padding(BatSpacing.lg)
        }
    }

    /// The pairing with each Team opening its Team, then the score, or "Not played yet" for a scheduled Fixture, and when and where it is played.
    private var header: some View {
        BatHeroCardView {
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                BatHeroPillRowView(text: String(localized: "fixture_detail_hero_overline")) {
                    if detail.canShare {
                        BatIconButtonView(icon: .share, accessibilityText: String(localized: "fixture_detail_share_button_cd")) {
                            onAction(.shareClicked)
                        }
                    }
                }
                VStack(alignment: .leading, spacing: BatSpacing.xs) {
                    teamLink(id: detail.homeTeamId, name: detail.homeTeamName)
                    teamLink(id: detail.awayTeamId, name: detail.awayTeamName)
                }
                scoreText
                Text(whenAndWhere)
                    .batTextStyle(BatTypography.body)
                    .foregroundStyle(BatColor.onHero)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    /// Underlined, so a link is never told apart by colour alone.
    private func teamLink(id: String, name: String) -> some View {
        Button { onAction(.teamClicked(teamId: id)) } label: {
            Text(name)
                .batTextStyle(BatTypography.title)
                .underline()
                .foregroundStyle(BatColor.onHero)
                .frame(minHeight: BatSizing.minTouchTarget, alignment: .leading)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var scoreText: some View {
        if let score = detail.score {
            Text("fixture_detail_score \(score.home) \(score.away)")
                .batTextStyle(BatTypography.display)
                .monospacedDigit()
                .foregroundStyle(BatColor.onHero)
                .accessibilityLabel(Text("fixture_detail_header_result_cd \(detail.homeTeamName) \(detail.awayTeamName) \(score.home) \(score.away)"))
        } else {
            Text("fixture_detail_not_played")
                .batTextStyle(BatTypography.display)
                .foregroundStyle(BatColor.onHero)
                .accessibilityLabel(Text("fixture_detail_header_not_played_cd \(detail.homeTeamName) \(detail.awayTeamName)"))
        }
    }

    private var whenAndWhere: String {
        ([detail.startsAt.formatted(date: .abbreviated, time: .shortened)] + [detail.venue, detail.address].compactMap { $0 })
            .joined(separator: "\n")
    }
}

#Preview("Loading") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .loading, onAction: { _ in })
    }
}

#Preview("Error") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .error, onAction: { _ in })
    }
}

#Preview("Played") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .content(FixtureDetailPreviewData.played), onAction: { _ in })
    }
}

#Preview("Not played yet") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .content(FixtureDetailPreviewData.scheduled), onAction: { _ in })
    }
}

#Preview("No Matches") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .content(FixtureDetailPreviewData.noMatches), onAction: { _ in })
    }
}

#Preview("Long names") {
    BatPreviewMatrix {
        FixtureDetailScreen(state: .content(FixtureDetailPreviewData.longNames), onAction: { _ in })
    }
}
