import Foundation

private let apache = "Apache License\nVersion 2.0, January 2004\nhttp://www.apache.org/licenses/\n\nTERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION"

let licensesSampleGroups = [
    LicenseGroupUi(kind: .library, entries: [
        LicenseEntry(id: "io.ktor:ktor-client-core", name: "Ktor Client Core", version: "3.4.3", authors: "JetBrains", licenseName: "Apache License 2.0", licenseText: apache, licenseUrl: nil, kind: .library),
        LicenseEntry(id: "swift:nanopb", name: "nanopb", version: "2.30910.1", authors: nil, licenseName: "zlib License", licenseText: "This software is provided 'as-is', without any express or implied warranty.", licenseUrl: nil, kind: .library),
        LicenseEntry(id: "x:no-text", name: "Library without a text", version: nil, authors: nil, licenseName: "Android SDK License", licenseText: nil, licenseUrl: "https://developer.android.com/studio/terms.html", kind: .library),
    ]),
    LicenseGroupUi(kind: .font, entries: [
        LicenseEntry(id: "font:inter", name: "Inter", version: "4.0", authors: "The Inter Project Authors", licenseName: "SIL Open Font License 1.1", licenseText: "Copyright (c) The Inter Project Authors\n\nThis Font Software is licensed under the SIL Open Font License, Version 1.1.", licenseUrl: nil, kind: .font),
    ]),
]
