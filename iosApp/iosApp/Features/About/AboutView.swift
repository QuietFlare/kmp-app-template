import SwiftUI

struct AboutView: View {
    private let siteURL = URL(string: "https://example.com/starter")!
    private let privacyURL = URL(string: "https://example.com/starter/privacy")!
    private let supportURL = URL(string: "https://example.com/starter/support")!

    /// Read from the bundle, so the screen and App Store Connect can never disagree.
    private var version: String {
        let short = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "?"
        let build = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "?"
        return "\(short) (\(build))"
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: Spacing.xl) {
                    VStack(alignment: .leading, spacing: Spacing.xs) {
                        Text("Starter").font(.appSerif(28)).foregroundStyle(Theme.ink)
                        Text("Version \(version)").font(.footnote).foregroundStyle(Theme.inkSoft)
                    }
                    .appCard()

                    VStack(spacing: 0) {
                        Link("Privacy policy", destination: privacyURL).linkRow()
                        Divider().overlay(Theme.inkSoft.opacity(CardStyle.borderAlpha))
                        NavigationLink("Acknowledgements") { AcknowledgementsView() }.linkRow()
                    }
                    .appCard(padding: 0)
                    .section("Privacy")

                    VStack(spacing: 0) {
                        Link("Help and contact", destination: supportURL).linkRow()
                        Divider().overlay(Theme.inkSoft.opacity(CardStyle.borderAlpha))
                        ShareLink("Share Starter", item: siteURL).linkRow()
                    }
                    .appCard(padding: 0)
                    .section("Support")
                }
                .padding(.horizontal, Spacing.lg)
                .padding(.vertical, Spacing.sm)
            }
            .background(Theme.backgroundGradient.ignoresSafeArea())
            .appTitle("About")
        }
    }
}

private extension View {
    func linkRow() -> some View {
        self
            .font(.body)
            .foregroundStyle(Theme.primary)
            .frame(maxWidth: .infinity, minHeight: Metrics.minTarget, alignment: .leading)
            .padding(.horizontal, Spacing.xl)
    }
}
