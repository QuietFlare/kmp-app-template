package app.starter.android

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Every Android dependency must be on the Acknowledgements screen (Apache 2.0 §4); AcknowledgementsTest covers iOS. */
class AndroidAcknowledgementsTest {

    private fun repoRoot(): File {
        var dir = File(".").absoluteFile
        while (!File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile ?: return dir
        }
        return dir
    }

    @Test
    fun everyShippedDependencyIsAcknowledged() {
        val root = repoRoot()
        val catalog = File(root, "gradle/libs.versions.toml").readText()
        val screen = File(root, "androidApp/src/main/kotlin/app/starter/android/features/about/AboutScreen.kt")
        assertTrue("missing ${screen.absolutePath}", screen.exists())

        val modules = Regex("""^([a-z0-9\-]+)\s*=\s*\{[^}]*module\s*=\s*"([^"]+)"""", RegexOption.MULTILINE)
            .findAll(catalog)
            .associate { it.groupValues[1].replace('-', '.') to it.groupValues[2] }

        // Both build files ship code; test-only, project(...) and the code-less Compose BOM are excluded.
        val coordinates = listOf("androidApp/build.gradle.kts", "shared/build.gradle.kts")
            .flatMap { path ->
                Regex("""^\s*implementation\(libs\.([a-z0-9.]+)\)""", RegexOption.MULTILINE)
                    .findAll(File(root, path).readText())
                    .map { modules.getValue(it.groupValues[1]) }
                    .toList()
            }
            .filterNot { it.startsWith("androidx.compose:compose-bom") }

        assertTrue("no dependencies parsed; has a build file moved?", coordinates.isNotEmpty())

        val acknowledged = screen.readText()
        // Families are acknowledged by group prefix (androidx.compose covers every Compose artifact).
        val missing = coordinates.filterNot { coordinate ->
            acknowledged.contains(coordinate) || acknowledged.contains(coordinate.substringBefore(':'))
        }
        assertTrue("shipped but not acknowledged: $missing. Add them to AboutScreen's LIBRARIES", missing.isEmpty())
    }
}
