import Foundation
import Testing
import UIKit

@testable import MobTT

@MainActor
struct FixtureShareImageTests {
    @Test func theImageIsDrawnAtItsWidthAndGrowsWithTheMatches() throws {
        let played = FixtureDetailPreviewData.played
        let scheduled = FixtureDetailPreviewData.scheduled

        let finishedImage = try #require(FixtureShareImageView.render(detail: played, link: "https://links.mobtt.example/f/464821", scale: 2))
        let scheduledImage = try #require(FixtureShareImageView.render(detail: scheduled, link: "https://links.mobtt.example/f/464821", scale: 2))

        #expect(finishedImage.size.width == FixtureShareImageView.width)
        #expect(finishedImage.size.height > scheduledImage.size.height)
    }

    @Test func withoutALinkTheImageIsStillDrawn() {
        #expect(FixtureShareImageView.render(detail: FixtureDetailPreviewData.scheduled, link: nil, scale: 2) != nil)
    }
}
