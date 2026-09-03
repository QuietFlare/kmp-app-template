import SwiftUI

enum RootTab: Hashable {
    case home, about
}

struct RootView: View {
    @State private var tab: RootTab = .home

    var body: some View {
        TabView(selection: $tab) {
            HomeView()
                .tabItem { Label("Home", systemImage: "house.fill") }
                .tag(RootTab.home)
            AboutView()
                .tabItem { Label("About", systemImage: "info.circle.fill") }
                .tag(RootTab.about)
        }
        .tint(Theme.primary)
        // Light-only, no theme switch: the palette is the brand and the launch assets match.
        .preferredColorScheme(.light)
    }
}
