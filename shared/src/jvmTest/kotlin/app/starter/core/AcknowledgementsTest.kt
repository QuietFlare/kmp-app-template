package app.starter.core

import kotlin.test.Test
import kotlin.test.assertTrue

/** Every shared-framework dependency must be on the iOS Acknowledgements screen (Apache 2.0 §4). */
class AcknowledgementsTest {

    @Test
    fun everySharedDependencyIsAcknowledgedOnIOS() {
        val coordinates = RepoFiles.shippedCoordinates("shared/build.gradle.kts")
        assertTrue(coordinates.isNotEmpty(), "no dependencies parsed; has the build file moved?")

        val screen = RepoFiles.read("iosApp/iosApp/Features/About/AcknowledgementsView.swift")
        val missing = coordinates.filterNot { screen.contains(it) }
        assertTrue(
            missing.isEmpty(),
            "shipped but not acknowledged: $missing. Add them to AcknowledgementsView.libraries",
        )
    }
}
