// GENERATED FROM design/tokens.json. DO NOT EDIT.
// Run `python3 design/tokens.py` after changing a token; DesignTokensTest fails if this file is stale.

import SwiftUI

extension Color {
    /// The Color(hex:) idiom used throughout the app.
    init(hex: String, opacity: Double = 1) {
        let s = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var v: UInt64 = 0
        Scanner(string: s).scanHexInt64(&v)
        self.init(.sRGB,
                  red: Double((v >> 16) & 0xFF) / 255,
                  green: Double((v >> 8) & 0xFF) / 255,
                  blue: Double(v & 0xFF) / 255,
                  opacity: opacity)
    }
}

/// Brand tokens, value for value the same as Android's `Theme.kt`; both come from design/tokens.json.
/// The app is light-only (see RootView).
enum Theme {
    static let background = Color(hex: "FBF3E4")
    static let surface = Color(hex: "FFFDF8")
    static let primary = Color(hex: "6B3FA0")
    static let secondary = Color(hex: "37877D")
    static let accent = Color(hex: "F6B93B")
    static let accentText = Color(hex: "8A6414")
    static let ink = Color(hex: "33281F")
    static let inkSoft = Color(hex: "8A7A66")

    /// Page ground: the background fading a shade warmer toward the bottom.
    static var backgroundGradient: LinearGradient {
        LinearGradient(
            colors: [background, Color(hex: "F3E7D4")],
            startPoint: .top, endPoint: .bottom
        )
    }
}

/// The display face. Dynamic Type provides the scale, so only the serif family and nav-title size are tokens.
enum Typography {
    static let displayFamily = "Georgia"
    static let navTitleSize: CGFloat = 20
}

enum Spacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 20
    static let xxl: CGFloat = 28
}

/// The house card: surface, hairline border, soft primary-tinted shadow.
enum CardStyle {
    static let radius: CGFloat = 20
    static let padding: CGFloat = 20
    static let borderAlpha: Double = 0.12
    static let shadowAlpha: Double = 0.1
    static let shadowRadius: CGFloat = 16
    static let shadowY: CGFloat = 6
}

/// Selection reads as state, not as a border ring.
enum Selection {
    static let scale: CGFloat = 1.05
    static let recededAlpha: Double = 0.72
}

enum Control {
    static let height: CGFloat = 44
    static let pillRadius: CGFloat = 22
    static let trackInset: CGFloat = 4
}

enum Metrics {
    static let minTarget: CGFloat = 44
    static let minFontSize: CGFloat = 11
}
