import SwiftUI

/// Draws nothing while `fixtures` is `.hidden`.
struct UpcomingFixturesView: View {
    let fixtures: HomeFixturesUi
    let onRetry: () -> Void

    var body: some View {
        switch fixtures {
        case .hidden:
            EmptyView()
        case .unavailable:
            SectionView {
                VStack(alignment: .leading, spacing: BatSpacing.sm) {
                    Text("home_fixtures_error_body")
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(BatColor.textSecondary)
                    BatSecondaryButtonView(title: String(localized: "home_fixtures_error_retry_button"), action: onRetry)
                }
                .onAppear { AccessibilityNotification.Announcement(String(localized: "home_fixtures_error_body")).post() }
            }
        case .loaded(let entries):
            SectionView {
                VStack(spacing: BatSpacing.sm) {
                    ForEach(entries) { FixtureCardView(entry: $0) }
                }
            }
        }
    }
}

private struct SectionView<Content: View>: View {
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            BatSectionHeaderView(title: String(localized: "home_fixtures_title"))
            content()
        }
    }
}

private struct FixtureCardView: View {
    let entry: HomeFixtureUi

    var body: some View {
        BatCardView {
            VStack(alignment: .leading, spacing: BatSpacing.sm) {
                Text(entry.teamName)
                    .batTextStyle(BatTypography.title)
                    .foregroundStyle(BatColor.textPrimary)
                NextFixtureView(nextFixture: entry.nextFixture)
            }
        }
    }
}

#Preview {
    BatPreviewMatrix {
        VStack(spacing: BatSpacing.lg) {
            UpcomingFixturesView(fixtures: HomePreviewData.fixtures, onRetry: {})
            UpcomingFixturesView(fixtures: .unavailable, onRetry: {})
        }
    }
}
