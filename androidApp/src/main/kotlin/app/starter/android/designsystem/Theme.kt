// GENERATED FROM design/tokens.json. DO NOT EDIT.
// Run `python3 design/tokens.py` after changing a token; DesignTokensTest fails if this file is stale.

package app.starter.android.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Brand tokens, value for value the same as iOS `Theme.swift`; both come from design/tokens.json.
 * The app is light-only: no dark scheme, `isSystemInDarkTheme()` is never read, and the manifest
 * theme sets `forceDarkAllowed=false`.
 */
object Theme {
    val background = Color(0xFFFBF3E4)
    val surface = Color(0xFFFFFDF8)
    val primary = Color(0xFF6B3FA0)
    val secondary = Color(0xFF37877D)
    val accent = Color(0xFFF6B93B)
    val accentText = Color(0xFF8A6414)
    val ink = Color(0xFF33281F)
    val inkSoft = Color(0xFF8A7A66)

    /** Page ground: the background fading a shade warmer toward the bottom. */
    val backgroundGradient = Brush.verticalGradient(listOf(background, Color(0xFFF3E7D4)))
}

/**
 * The display serif (Georgia on iOS, Noto Serif on Android) at iOS Dynamic Type's default sizes,
 * so a label matches on both phones. Sizes are in `sp` so the user's font-size setting scales them.
 */
object Type {
    val display: FontFamily = FontFamily.Serif

    /** The display serif at a given size, the counterpart of `Font.appSerif(_:)`. */
    fun serif(size: Int): TextStyle = TextStyle(fontFamily = display, fontSize = size.sp)

    const val navTitleSize = 20

    val largeTitle = TextStyle(fontSize = 34.sp, lineHeight = 41.sp, fontWeight = FontWeight.Bold)
    val title = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Normal)
    val title2 = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Normal)
    val title3 = TextStyle(fontSize = 20.sp, lineHeight = 25.sp, fontWeight = FontWeight.Normal)
    val headline = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
    val body = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal)
    val callout = TextStyle(fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Normal)
    val subheadline = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal)
    val footnote = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal)
    val caption = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal)
    val caption2 = TextStyle(fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.Normal)
}

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 28.dp
}

/** The house card: surface, hairline border, soft primary-tinted shadow. */
object CardStyle {
    val radius = 20.dp
    val padding = 20.dp
    const val borderAlpha = 0.12f
    const val shadowAlpha = 0.1f
    val elevation = 8.dp
}

/** Selection reads as state, not as a border ring. */
object Selection {
    const val scale = 1.05f
    const val recededAlpha = 0.72f
}

object Control {
    val height = 44.dp
    val pillRadius = 22.dp
    val trackInset = 4.dp
}

object Metrics {
    val minTarget = 44.dp
    val minFontSize = 11.sp
}
