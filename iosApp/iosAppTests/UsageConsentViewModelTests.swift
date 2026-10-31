import Shared
import Testing

@testable import MobTT

@MainActor
struct UsageConsentViewModelTests {
    @Test func promptFollowsTheSharedUseCase() async {
        let viewModel = makeViewModel(prompts: [false, true])

        await viewModel.observe()

        #expect(viewModel.isPromptVisible)
    }

    @Test func allowingSavesAllowed() async {
        var saved: [Bool] = []
        let viewModel = makeViewModel(prompts: [true], setUsageConsent: { saved.append($0) })
        await viewModel.observe()

        viewModel.onAction(.allowClicked)
        await waitUntil { !saved.isEmpty }

        #expect(saved == [true])
    }

    @Test func notNowSavesDeclined() async {
        var saved: [Bool] = []
        let viewModel = makeViewModel(prompts: [true], setUsageConsent: { saved.append($0) })
        await viewModel.observe()

        viewModel.onAction(.notNowClicked)
        await waitUntil { !saved.isEmpty }

        #expect(saved == [false])
    }

    @Test func aHiddenPromptSavesNothing() async {
        var saved: [Bool] = []
        let viewModel = makeViewModel(prompts: [false], setUsageConsent: { saved.append($0) })
        await viewModel.observe()

        viewModel.onAction(.notNowClicked)
        try? await Task.sleep(for: .milliseconds(50))

        #expect(saved.isEmpty)
    }

    private func makeViewModel(
        prompts: [Bool],
        setUsageConsent: @escaping (Bool) async throws -> Void = { _ in }
    ) -> UsageConsentViewModel {
        let stream = AsyncStream<KotlinBoolean> { continuation in
            prompts.forEach { continuation.yield(KotlinBoolean(bool: $0)) }
            continuation.finish()
        }
        return UsageConsentViewModel(observePrompt: stream, setUsageConsent: setUsageConsent)
    }
}
