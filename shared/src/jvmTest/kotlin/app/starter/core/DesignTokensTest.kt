package app.starter.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertTrue

/** Generated theme files must match `design/tokens.json` (`python3 design/tokens.py`); compares values, so CI needs no Python. */
class DesignTokensTest {

    private val tokens = Json.parseToJsonElement(RepoFiles.read("design/tokens.json")).jsonObject

    private fun colours(section: String): Map<String, String> =
        tokens.getValue(section).jsonObject.mapValues { it.value.jsonPrimitive.content.removePrefix("#").uppercase() }

    @Test
    fun everyColourTokenIsInBothThemeFiles() {
        val all = colours("colors") + colours("semantic")
        val swift = RepoFiles.read("iosApp/iosApp/DesignSystem/Theme.swift")
        val kotlin = RepoFiles.read("androidApp/src/main/kotlin/app/starter/android/designsystem/Theme.kt")

        val missingSwift = all.filterValues { hex -> !swift.contains("Color(hex: \"$hex\")") }.keys
        val missingKotlin = all.filterValues { hex -> !kotlin.contains("Color(0xFF$hex)") }.keys

        assertTrue(missingSwift.isEmpty(), "Theme.swift is stale for $missingSwift. Run python3 design/tokens.py")
        assertTrue(missingKotlin.isEmpty(), "Theme.kt is stale for $missingKotlin. Run python3 design/tokens.py")
    }

    @Test
    fun theLaunchAndWindowBackgroundsAreTheBackgroundToken() {
        val background = colours("colors").getValue("background")
        val colorsXml = RepoFiles.read("androidApp/src/main/res/values/colors.xml")
        val colorset = RepoFiles.read("iosApp/iosApp/Assets.xcassets/LaunchBackground.colorset/Contents.json")

        assertTrue(colorsXml.contains("#$background"), "colors.xml is stale. Run python3 design/tokens.py")
        val (r, g, b) = background.chunked(2)
        assertTrue(
            colorset.contains("\"red\": \"0x$r\"") && colorset.contains("\"green\": \"0x$g\"") && colorset.contains("\"blue\": \"0x$b\""),
            "LaunchBackground.colorset is stale. Run python3 design/tokens.py",
        )
    }

    @Test
    fun typeScaleMatchesOnAndroid() {
        val scale = tokens.getValue("typography").jsonObject.getValue("scale").jsonObject
        val kotlin = RepoFiles.read("androidApp/src/main/kotlin/app/starter/android/designsystem/Theme.kt")
        val stale = scale.filterNot { (name, style) ->
            val size = style.jsonObject.getValue("size").jsonPrimitive.content
            val line = style.jsonObject.getValue("lineHeight").jsonPrimitive.content
            kotlin.contains("val $name = TextStyle(fontSize = $size.sp, lineHeight = $line.sp")
        }.keys
        assertTrue(stale.isEmpty(), "Type scale is stale for $stale. Run python3 design/tokens.py")
    }
}
