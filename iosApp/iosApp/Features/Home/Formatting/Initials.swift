/// First letters of the first two words, e.g. "Michael Huber" gives "MH".
func initialsOf(_ name: String) -> String {
    name.split(separator: " ")
        .prefix(2)
        .compactMap { $0.first.map { String($0).uppercased() } }
        .joined()
}
