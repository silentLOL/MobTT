import SwiftUI

/// The Club tab: the Club derived from My Player or My Club, or, without one, the Association, Club picker. A Club
/// picked there opens in the tab without becoming My Club.
struct ClubTabRoute: View {
    let onOpenTeam: (String) -> Void
    let onOpenPlayer: (String) -> Void

    @State private var pickedClubId: String?

    var body: some View {
        ClubRoute(
            source: .mine,
            onOpenSettings: {},
            onOpenTeam: onOpenTeam,
            onOpenPlayer: onOpenPlayer,
            noClubContent: AnyView(pickedOrPicker)
        )
    }

    @ViewBuilder
    private var pickedOrPicker: some View {
        if let pickedClubId {
            ClubRoute(source: .specific(clubId: pickedClubId), onOpenSettings: {}, onOpenTeam: onOpenTeam, onOpenPlayer: onOpenPlayer)
        } else {
            PlayersRoute(mode: .pickClub, onChooseClub: { pickedClubId = $0 })
        }
    }
}
