import SwiftUI
import Shared

/// Placeholder. Proves the stack end to end (a shared-core value rendered with the house
/// components); delete it with the first real screen.
struct HomeView: View {
    @AppStorage("horizonDays") private var horizon = 30
    private let today = AppCore.shared.todayIso()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: Spacing.xl) {
                    VStack(alignment: .leading, spacing: Spacing.xs) {
                        Text(today)
                            .font(.appSerif(28)).foregroundStyle(Theme.ink)
                        Text("From the shared core, as an ISO string, the only date type that crosses the bridge.")
                            .font(.footnote).foregroundStyle(Theme.inkSoft)
                    }
                    .appCard()
                    .section("Today")

                    VStack(alignment: .leading, spacing: Spacing.md) {
                        SegmentedRow(
                            options: [.init(7, "Week"), .init(30, "Month"), .init(365, "Year")],
                            selection: $horizon
                        )
                        VStack(alignment: .leading, spacing: Spacing.xs) {
                            let until = "2027-01-01"
                            // Kotlin Int arrives as Int32; the bridge test pins this.
                            let days = AppCore.shared.daysBetween(fromIso: today, toIso: until)
                            Text("\(days) days")
                                .font(.appSerif(28)).foregroundStyle(Theme.primary)
                            Text("until \(until), \(horizon)-day view")
                                .font(.subheadline).foregroundStyle(Theme.inkSoft)
                        }
                        .appCard()
                    }
                    .section("Horizon")
                }
                .padding(.horizontal, Spacing.lg)
                .padding(.vertical, Spacing.sm)
            }
            .background(Theme.backgroundGradient.ignoresSafeArea())
            .appTitle("Home")
        }
    }
}
