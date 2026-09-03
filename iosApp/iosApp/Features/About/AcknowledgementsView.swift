import SwiftUI

/// Third-party attribution. Apache 2.0 §4 requires the notices to ship with the binary, which
/// links the Kotlin runtime and kotlinx libraries (the repo's MIT licence covers only the source).
/// Keep `libraries` in step with `shared/build.gradle.kts`; `AcknowledgementsTest` fails otherwise.
struct AcknowledgementsView: View {

    struct Library: Identifiable {
        let id = UUID()
        let name: String
        /// The version-catalog coordinate, so the two can be cross-checked. Empty for code
        /// that arrives implicitly, like the Kotlin runtime.
        let coordinate: String
        let copyright: String
        let licence: String
    }

    static let libraries: [Library] = [
        .init(name: "kotlinx-datetime",
              coordinate: "org.jetbrains.kotlinx:kotlinx-datetime",
              copyright: "Copyright © 2019–2026 JetBrains s.r.o.",
              licence: "Apache License 2.0"),
        .init(name: "kotlinx.serialization",
              coordinate: "org.jetbrains.kotlinx:kotlinx-serialization-json",
              copyright: "Copyright © 2017–2026 JetBrains s.r.o.",
              licence: "Apache License 2.0"),
        .init(name: "Kotlin standard library & Kotlin/Native runtime",
              coordinate: "",
              copyright: "Copyright © 2010–2026 JetBrains s.r.o. and Kotlin Programming Language contributors",
              licence: "Apache License 2.0"),
    ]

    private static let apacheURL = URL(string: "https://www.apache.org/licenses/LICENSE-2.0")!

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: Spacing.xl) {
                Text("Starter is built with these open-source libraries.")
                    .font(.subheadline).foregroundStyle(Theme.inkSoft)
                    .fixedSize(horizontal: false, vertical: true)

                ForEach(Self.libraries) { library in
                    VStack(alignment: .leading, spacing: Spacing.xs) {
                        Text(library.name).font(.headline).foregroundStyle(Theme.primary)
                        Text(library.copyright).font(.footnote).foregroundStyle(Theme.ink)
                            .fixedSize(horizontal: false, vertical: true)
                        Text(library.licence).font(.footnote).foregroundStyle(Theme.inkSoft)
                    }
                    .appCard(padding: Spacing.lg)
                }

                Link("Apache License 2.0", destination: Self.apacheURL)
                    .font(.caption).foregroundStyle(Theme.inkSoft)
            }
            .padding(.horizontal, Spacing.lg)
            .padding(.vertical, Spacing.sm)
        }
        .background(Theme.backgroundGradient.ignoresSafeArea())
        .appTitle("Acknowledgements")
    }
}
