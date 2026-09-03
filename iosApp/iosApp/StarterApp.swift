import SwiftUI

@main
struct StarterApp: App {
    @Environment(\.scenePhase) private var scenePhase

    var body: some Scene {
        WindowGroup {
            RootView()
        }
        .onChange(of: scenePhase) { _, phase in
            // Flush any debounced save here when leaving the foreground. Android's
            // counterpart is a ProcessLifecycleOwner observer in StarterApp.kt.
            _ = phase
        }
    }
}
