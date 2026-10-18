import SwiftUI
import Testing

@testable import BatMate

struct AppearanceChoiceTests {
    @Test func storedValuesRoundTripThroughTheRawValue() {
        for choice in AppearanceChoice.allCases {
            #expect(AppearanceChoice(rawValue: choice.rawValue) == choice)
        }
    }

    @Test func anUnknownStoredValueIsNotAChoice() {
        #expect(AppearanceChoice(rawValue: "sepia") == nil)
    }

    @Test func systemFollowsTheDeviceAndTheOthersOverrideIt() {
        #expect(AppearanceChoice.system.colorScheme == nil)
        #expect(AppearanceChoice.light.colorScheme == .light)
        #expect(AppearanceChoice.dark.colorScheme == .dark)
    }
}
