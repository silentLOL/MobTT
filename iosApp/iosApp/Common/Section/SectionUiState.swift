/// A screen section that loads on its own, so the screen never waits for it.
enum SectionUiState<Value: Equatable>: Equatable {
    case loading
    case unavailable
    case loaded(Value)
}
