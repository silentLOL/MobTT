import Shared

/// Previews of views that resolve shared UseCases need Koin started once per process.
enum PreviewKoin {
    static let started: Void = KoinHelper.shared.start(environmentKey: "deb", appVersion: "preview", xttvApiKey: "")
}
