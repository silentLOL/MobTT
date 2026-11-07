import CoreImage.CIFilterBuiltins
import SwiftUI
import UIKit

/// A Fixture as a picture to share: the red card, every Match, and a footer with the MobTT logo, "Get MobTT" and, when
/// there is a Fixture link, its QR code, so a forwarded picture still leads to the Fixture. Rendered in the light appearance.
struct FixtureShareImageView: View {
    static let width: CGFloat = 360

    let detail: FixtureDetailUi
    let qrCode: UIImage?

    var body: some View {
        VStack(alignment: .leading, spacing: BatSpacing.lg) {
            BatHeroCardView {
                VStack(alignment: .leading, spacing: BatSpacing.sm) {
                    BatHeroPillRowView(text: String(localized: "fixture_detail_hero_overline"))
                    VStack(alignment: .leading, spacing: BatSpacing.xs) {
                        Text(detail.homeTeamName)
                        Text(detail.awayTeamName)
                    }
                    .batTextStyle(BatTypography.title)
                    .foregroundStyle(BatColor.onHero)
                    Group {
                        if let score = detail.score {
                            Text("fixture_detail_score \(score.home) \(score.away)")
                        } else {
                            Text("fixture_detail_not_played")
                        }
                    }
                    .batTextStyle(BatTypography.display)
                    .monospacedDigit()
                    .foregroundStyle(BatColor.onHero)
                    Text(([detail.startsAt.formatted(date: .abbreviated, time: .shortened)] + [detail.venue, detail.address].compactMap { $0 }).joined(separator: "\n"))
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(BatColor.onHero)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
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
            HStack(spacing: BatSpacing.md) {
                BatLogoMarkView()
                VStack(alignment: .leading, spacing: 0) {
                    Text("core_app_title")
                        .batTextStyle(BatTypography.title)
                        .foregroundStyle(BatColor.textPrimary)
                    Text("fixture_detail_share_image_get_app")
                        .batTextStyle(BatTypography.body)
                        .foregroundStyle(BatColor.textSecondary)
                }
                Spacer(minLength: 0)
                if let qrCode {
                    Image(uiImage: qrCode)
                        .interpolation(.none)
                        .resizable()
                        .frame(width: 96, height: 96)
                }
            }
        }
        .padding(BatSpacing.lg)
        .frame(width: Self.width)
        .background(BatColor.background)
        .environment(\.colorScheme, .light)
    }

    /// The picture of `detail`, or nil when it cannot be drawn.
    @MainActor
    static func render(detail: FixtureDetailUi, link: String?, scale: CGFloat) -> UIImage? {
        let renderer = ImageRenderer(content: FixtureShareImageView(detail: detail, qrCode: link.flatMap(qrCode(for:))))
        renderer.proposedSize = ProposedViewSize(width: width, height: nil)
        renderer.scale = scale
        return renderer.uiImage
    }

    /// A black-on-white QR code of `text`, drawn on the device.
    static func qrCode(for text: String) -> UIImage? {
        let filter = CIFilter.qrCodeGenerator()
        filter.message = Data(text.utf8)
        filter.correctionLevel = "M"
        guard let output = filter.outputImage?.transformed(by: CGAffineTransform(scaleX: 12, y: 12)),
              let cgImage = CIContext().createCGImage(output, from: output.extent) else { return nil }
        return UIImage(cgImage: cgImage)
    }
}

#Preview("Finished") {
    ScrollView {
        FixtureShareImageView(detail: FixtureDetailPreviewData.played, qrCode: FixtureShareImageView.qrCode(for: "https://links.mobtt.example/f/464821")!)
    }
}

#Preview("Scheduled") {
    FixtureShareImageView(detail: FixtureDetailPreviewData.scheduled, qrCode: FixtureShareImageView.qrCode(for: "https://links.mobtt.example/f/464821")!)
}

#Preview("Sixteen Matches") {
    let played = FixtureDetailPreviewData.played
    let matches = (1...16).map { number in
        let row = played.matches[(number - 1) % played.matches.count]
        return MatchRowUi(id: number, number: number, isDoubles: row.isDoubles, homePlayers: row.homePlayers, awayPlayers: row.awayPlayers, score: row.score)
    }
    ScrollView {
        FixtureShareImageView(
            detail: FixtureDetailUi(
                homeTeamId: played.homeTeamId, homeTeamName: played.homeTeamName,
                awayTeamId: played.awayTeamId, awayTeamName: played.awayTeamName,
                startsAt: played.startsAt, venue: played.venue, address: played.address,
                score: played.score, matches: matches
            ),
            qrCode: FixtureShareImageView.qrCode(for: "https://links.mobtt.example/f/464821")!
        )
    }
}
