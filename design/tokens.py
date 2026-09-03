#!/usr/bin/env python3
"""Loads design/tokens.json and writes the native constant files.

    python3 design/tokens.py          # regenerate every destination
    python3 design/tokens.py --check  # exit 1 if any destination is stale (CI / pre-commit)

Destinations (all generated, never hand-edit them):

    iosApp/iosApp/DesignSystem/Theme.swift
    iosApp/iosApp/Assets.xcassets/LaunchBackground.colorset/Contents.json
    iosApp/iosApp/LaunchScreen.storyboard
    androidApp/src/main/kotlin/app/starter/android/designsystem/Theme.kt
    androidApp/src/main/res/values/colors.xml
    androidApp/src/main/res/drawable/ic_launcher_background.xml

Not shared/, which is pure domain logic with no UI and no colours. One source, emitted to
every place a value is needed, cannot drift; DesignTokensTest fails when a destination is stale.
"""

import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
T = json.load(open(os.path.join(HERE, "tokens.json")))

COLORS = T["colors"]
SEMANTIC = T["semantic"]
TYPO = T["typography"]
SPACING = T["spacing"]
CARD = T["card"]
SELECTION = T["selection"]
CONTROL = T["control"]
METRICS = T["metrics"]
ICON = T["icon"]

BANNER = (
    "GENERATED FROM design/tokens.json. DO NOT EDIT.\n"
    "Run `python3 design/tokens.py` after changing a token; DesignTokensTest fails if this file is stale."
)

WEIGHT_SWIFT = {"regular": ".regular", "semibold": ".semibold", "bold": ".bold"}
WEIGHT_KT = {"regular": "FontWeight.Normal", "semibold": "FontWeight.SemiBold", "bold": "FontWeight.Bold"}


# ---------------------------------------------------------------- colour forms
def rgb(hex_str):
    h = hex_str.lstrip("#")
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))


def hex6(hex_str):
    return hex_str.lstrip("#").upper()


def argb(hex_str):
    r, g, b = rgb(hex_str)
    return f"0xFF{r:02X}{g:02X}{b:02X}"


def num(v):
    """A JSON number as source text: 20 stays 20, 0.12 stays 0.12."""
    return f"{v:g}"


# ---------------------------------------------------------------- emitters
def swift():
    L = ["// " + l for l in BANNER.split("\n")]
    L += [
        "",
        "import SwiftUI",
        "",
        "extension Color {",
        "    /// The Color(hex:) idiom used throughout the app.",
        "    init(hex: String, opacity: Double = 1) {",
        "        let s = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)",
        "        var v: UInt64 = 0",
        "        Scanner(string: s).scanHexInt64(&v)",
        "        self.init(.sRGB,",
        "                  red: Double((v >> 16) & 0xFF) / 255,",
        "                  green: Double((v >> 8) & 0xFF) / 255,",
        "                  blue: Double(v & 0xFF) / 255,",
        "                  opacity: opacity)",
        "    }",
        "}",
        "",
        "/// Brand tokens, value for value the same as Android's `Theme.kt`; both come from design/tokens.json.",
        "/// The app is light-only (see RootView).",
        "enum Theme {",
    ]
    for k, v in COLORS.items():
        if k == "backgroundGradientEnd":
            continue
        L.append(f'    static let {k} = Color(hex: "{hex6(v)}")')
    if SEMANTIC:
        L.append("")
        L.append("    // App-specific named colours.")
        for k, v in SEMANTIC.items():
            L.append(f'    static let {k} = Color(hex: "{hex6(v)}")')
    L += [
        "",
        "    /// Page ground: the background fading a shade warmer toward the bottom.",
        "    static var backgroundGradient: LinearGradient {",
        "        LinearGradient(",
        f'            colors: [background, Color(hex: "{hex6(COLORS["backgroundGradientEnd"])}")],',
        "            startPoint: .top, endPoint: .bottom",
        "        )",
        "    }",
        "}",
        "",
        "/// The display face. Dynamic Type provides the scale, so only the serif family and nav-title size are tokens.",
        "enum Typography {",
        f'    static let displayFamily = "{TYPO["displayFamilyIOS"]}"',
        f"    static let navTitleSize: CGFloat = {num(TYPO['navTitleSize'])}",
        "}",
        "",
        "enum Spacing {",
    ]
    L += [f"    static let {k}: CGFloat = {num(v)}" for k, v in SPACING.items()]
    L += [
        "}",
        "",
        "/// The house card: surface, hairline border, soft primary-tinted shadow.",
        "enum CardStyle {",
        f"    static let radius: CGFloat = {num(CARD['radius'])}",
        f"    static let padding: CGFloat = {num(CARD['padding'])}",
        f"    static let borderAlpha: Double = {num(CARD['borderAlpha'])}",
        f"    static let shadowAlpha: Double = {num(CARD['shadowAlpha'])}",
        f"    static let shadowRadius: CGFloat = {num(CARD['shadowRadius'])}",
        f"    static let shadowY: CGFloat = {num(CARD['shadowY'])}",
        "}",
        "",
        "/// Selection reads as state, not as a border ring.",
        "enum Selection {",
        f"    static let scale: CGFloat = {num(SELECTION['scale'])}",
        f"    static let recededAlpha: Double = {num(SELECTION['recededAlpha'])}",
        "}",
        "",
        "enum Control {",
        f"    static let height: CGFloat = {num(CONTROL['height'])}",
        f"    static let pillRadius: CGFloat = {num(CONTROL['pillRadius'])}",
        f"    static let trackInset: CGFloat = {num(CONTROL['trackInset'])}",
        "}",
        "",
        "enum Metrics {",
        f"    static let minTarget: CGFloat = {num(METRICS['minTarget'])}",
        f"    static let minFontSize: CGFloat = {num(METRICS['minFontSize'])}",
        "}",
        "",
    ]
    return "\n".join(L)


def kotlin():
    L = ["// " + l for l in BANNER.split("\n")]
    L += [
        "",
        "package app.starter.android.designsystem",
        "",
        "import androidx.compose.ui.graphics.Brush",
        "import androidx.compose.ui.graphics.Color",
        "import androidx.compose.ui.text.TextStyle",
        "import androidx.compose.ui.text.font.FontFamily",
        "import androidx.compose.ui.text.font.FontWeight",
        "import androidx.compose.ui.unit.dp",
        "import androidx.compose.ui.unit.sp",
        "",
        "/**",
        " * Brand tokens, value for value the same as iOS `Theme.swift`; both come from design/tokens.json.",
        " * The app is light-only: no dark scheme, `isSystemInDarkTheme()` is never read, and the manifest",
        " * theme sets `forceDarkAllowed=false`.",
        " */",
        "object Theme {",
    ]
    for k, v in COLORS.items():
        if k == "backgroundGradientEnd":
            continue
        L.append(f"    val {k} = Color({argb(v)})")
    if SEMANTIC:
        L.append("")
        L.append("    // App-specific named colours.")
        for k, v in SEMANTIC.items():
            L.append(f"    val {k} = Color({argb(v)})")
    L += [
        "",
        "    /** Page ground: the background fading a shade warmer toward the bottom. */",
        f"    val backgroundGradient = Brush.verticalGradient(listOf(background, Color({argb(COLORS['backgroundGradientEnd'])})))",
        "}",
        "",
        "/**",
        " * The display serif (Georgia on iOS, Noto Serif on Android) at iOS Dynamic Type's default sizes,",
        " * so a label matches on both phones. Sizes are in `sp` so the user's font-size setting scales them.",
        " */",
        "object Type {",
        "    val display: FontFamily = FontFamily.Serif",
        "",
        "    /** The display serif at a given size, the counterpart of `Font.appSerif(_:)`. */",
        "    fun serif(size: Int): TextStyle = TextStyle(fontFamily = display, fontSize = size.sp)",
        "",
        f"    const val navTitleSize = {TYPO['navTitleSize']}",
        "",
    ]
    for k, s in TYPO["scale"].items():
        w = WEIGHT_KT[s["weight"]]
        L.append(
            f"    val {k} = TextStyle(fontSize = {s['size']}.sp, lineHeight = {s['lineHeight']}.sp, fontWeight = {w})"
        )
    L += [
        "}",
        "",
        "object Spacing {",
    ]
    L += [f"    val {k} = {num(v)}.dp" for k, v in SPACING.items()]
    L += [
        "}",
        "",
        "/** The house card: surface, hairline border, soft primary-tinted shadow. */",
        "object CardStyle {",
        f"    val radius = {num(CARD['radius'])}.dp",
        f"    val padding = {num(CARD['padding'])}.dp",
        f"    const val borderAlpha = {num(CARD['borderAlpha'])}f",
        f"    const val shadowAlpha = {num(CARD['shadowAlpha'])}f",
        f"    val elevation = {num(CARD['androidElevation'])}.dp",
        "}",
        "",
        "/** Selection reads as state, not as a border ring. */",
        "object Selection {",
        f"    const val scale = {num(SELECTION['scale'])}f",
        f"    const val recededAlpha = {num(SELECTION['recededAlpha'])}f",
        "}",
        "",
        "object Control {",
        f"    val height = {num(CONTROL['height'])}.dp",
        f"    val pillRadius = {num(CONTROL['pillRadius'])}.dp",
        f"    val trackInset = {num(CONTROL['trackInset'])}.dp",
        "}",
        "",
        "object Metrics {",
        f"    val minTarget = {num(METRICS['minTarget'])}.dp",
        f"    val minFontSize = {num(METRICS['minFontSize'])}.sp",
        "}",
        "",
    ]
    return "\n".join(L)


def colors_xml():
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        "<!-- GENERATED FROM design/tokens.json. DO NOT EDIT.\n"
        "     Only what the platform needs before Compose starts; the palette lives in Theme.kt. -->\n"
        "<resources>\n"
        f'    <color name="background">#{hex6(COLORS["background"])}</color>\n'
        "</resources>\n"
    )


def launch_colorset():
    """Launch-screen fill as an Xcode colour asset; generated because an asset catalog cannot read tokens.json."""
    r, g, b = rgb(COLORS["background"])
    return json.dumps({
        "colors": [{
            "color": {
                "color-space": "srgb",
                "components": {
                    "alpha": "1.000",
                    "red": f"0x{r:02X}", "green": f"0x{g:02X}", "blue": f"0x{b:02X}",
                },
            },
            "idiom": "universal",
        }],
        "info": {"author": "xcode", "version": 1},
    }, indent=2) + "\n"


def storyboard():
    """The launch screen: a centred 96pt mark on the brand background.

    A storyboard because UILaunchScreen's UIImageName renders nothing on device; the mark ships
    as loose PNGs in iosApp/iosApp/LaunchAssets/ since storyboards cannot load catalogue images.
    """
    r, g, b = (c / 255 for c in rgb(COLORS["background"]))
    return f'''<?xml version="1.0" encoding="UTF-8"?>
<!-- GENERATED FROM design/tokens.json. DO NOT EDIT (the background colour is a token). -->
<document type="com.apple.InterfaceBuilder3.CocoaTouch.Storyboard.XIB" version="3.0" toolsVersion="22505" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" launchScreen="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES" initialViewController="LCH-vc-001">
    <device id="retina6_1" orientation="portrait" appearance="light"/>
    <dependencies>
        <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="22504"/>
        <capability name="Safe area layout guides" minToolsVersion="9.0"/>
        <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
    </dependencies>
    <scenes>
        <scene sceneID="LCH-sc-001">
            <objects>
                <viewController id="LCH-vc-001" sceneMemberID="viewController">
                    <view key="view" contentMode="scaleToFill" id="LCH-vw-001">
                        <rect key="frame" x="0.0" y="0.0" width="393" height="852"/>
                        <autoresizingMask key="autoresizingMask" widthSizable="YES" heightSizable="YES"/>
                        <subviews>
                            <imageView clipsSubviews="YES" userInteractionEnabled="NO" contentMode="scaleAspectFit" horizontalHuggingPriority="251" verticalHuggingPriority="251" image="mark.png" translatesAutoresizingMaskIntoConstraints="NO" id="LCH-im-001">
                                <rect key="frame" x="148.5" y="378" width="96" height="96"/>
                                <constraints>
                                    <constraint firstAttribute="width" constant="96" id="LCH-cn-003"/>
                                    <constraint firstAttribute="height" constant="96" id="LCH-cn-004"/>
                                </constraints>
                            </imageView>
                        </subviews>
                        <viewLayoutGuide key="safeArea" id="LCH-sa-001"/>
                        <color key="backgroundColor" name="LaunchBackground"/>
                        <constraints>
                            <constraint firstItem="LCH-im-001" firstAttribute="centerX" secondItem="LCH-vw-001" secondAttribute="centerX" id="LCH-cn-001"/>
                            <constraint firstItem="LCH-im-001" firstAttribute="centerY" secondItem="LCH-vw-001" secondAttribute="centerY" id="LCH-cn-002"/>
                        </constraints>
                    </view>
                </viewController>
                <placeholder placeholderIdentifier="IBFirstResponder" id="LCH-fr-001" userLabel="First Responder" sceneMemberID="firstResponder"/>
            </objects>
            <point key="canvasLocation" x="53" y="375"/>
        </scene>
    </scenes>
    <resources>
        <image name="mark.png" width="96" height="96"/>
        <namedColor name="LaunchBackground">
            <color red="{r:.17f}" green="{g:.17f}" blue="{b:.17f}" alpha="1" colorSpace="custom" customColorSpace="sRGB"/>
        </namedColor>
    </resources>
</document>
'''


def launcher_background():
    """The adaptive-icon background layer, the same radial wash as the iOS icon.

    Full-bleed across the 108dp adaptive canvas: the launcher masks it, so anything short of the edges shows a gap.
    """
    return f'''<?xml version="1.0" encoding="utf-8"?>
<!-- GENERATED FROM design/tokens.json. DO NOT EDIT. The wash behind the mark, matching the iOS icon from tools/render_icon.py. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:pathData="M0,0h108v108h-108z">
        <aapt:attr name="android:fillColor">
            <gradient
                android:type="radial"
                android:centerX="54"
                android:centerY="48.24"
                android:gradientRadius="56.16"
                android:startColor="#{hex6(ICON["backgroundIn"])}"
                android:endColor="#{hex6(ICON["backgroundOut"])}" />
        </aapt:attr>
    </path>
</vector>
'''


DESTINATIONS = {
    "iosApp/iosApp/DesignSystem/Theme.swift": swift,
    "iosApp/iosApp/Assets.xcassets/LaunchBackground.colorset/Contents.json": launch_colorset,
    "iosApp/iosApp/LaunchScreen.storyboard": storyboard,
    "androidApp/src/main/kotlin/app/starter/android/designsystem/Theme.kt": kotlin,
    "androidApp/src/main/res/values/colors.xml": colors_xml,
    "androidApp/src/main/res/drawable/ic_launcher_background.xml": launcher_background,
}


def main(argv):
    check = "--check" in argv
    stale = []
    for rel, emit in DESTINATIONS.items():
        path = os.path.join(ROOT, rel)
        body = emit()
        current = open(path).read() if os.path.exists(path) else None
        if check:
            if current != body:
                stale.append(rel)
            continue
        os.makedirs(os.path.dirname(path), exist_ok=True)
        if current != body:
            open(path, "w").write(body)
            print("wrote " + rel)
        else:
            print("up to date " + rel)
    if check:
        if stale:
            print("Stale. Run python3 design/tokens.py:\n  " + "\n  ".join(stale), file=sys.stderr)
            return 1
        print("design tokens: all destinations up to date")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
