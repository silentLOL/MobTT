import Observation
import Shared

@MainActor
@Observable
final class UsageConsentViewModel {
    /// True while the consent step is shown.
    private(set) var isPromptVisible = false

    @ObservationIgnored private let observePrompt: any AsyncSequence<KotlinBoolean, Never>
    @ObservationIgnored private let setUsageConsent: (Bool) async throws -> Void

    init(
        observePrompt: any AsyncSequence<KotlinBoolean, Never> = KoinHelper.shared.getObserveUsageConsentPromptUseCase().invoke(),
        setUsageConsent: @escaping (Bool) async throws -> Void = { try await KoinHelper.shared.getSetUsageConsentUseCase().invoke(allowed: $0) }
    ) {
        self.observePrompt = observePrompt
        self.setUsageConsent = setUsageConsent
    }

    /// Runs until the calling task is cancelled.
    func observe() async {
        for await visible in observePrompt {
            isPromptVisible = visible.boolValue
        }
    }

    func onAction(_ action: UsageConsentAction) {
        // The sheet's dismissal also arrives here once an answer has hidden it; only a visible prompt is answered.
        guard isPromptVisible else { return }
        Task { try? await setUsageConsent(action == .allowClicked) }
    }
}
