/// One-off events from a ViewModel to its screen. Unlike a plain `AsyncStream`, it outlives the screen's listener:
/// cancelling the listener's task (a tab switch, or a screen pushed over this one, ends `.task`) would finish a plain
/// stream for good, and every later event would be lost. Events sent while nobody listens wait for the next listener.
@MainActor
final class EventChannel<Event: Sendable> {
    private var continuation: AsyncStream<Event>.Continuation?
    private var pending: [Event] = []

    /// A stream for the current listener. Starting a new one replaces the previous.
    var stream: AsyncStream<Event> {
        AsyncStream { continuation in
            self.continuation = continuation
            let waiting = pending
            pending = []
            waiting.forEach { continuation.yield($0) }
        }
    }

    func yield(_ event: Event) {
        if let continuation, case .enqueued = continuation.yield(event) { return }
        pending.append(event)
    }
}
