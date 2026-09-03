package app.starter.core

import java.io.File

/**
 * Repository-relative file access for tests that pin two hand-maintained files against each
 * other. JVM-only (reads disk); the walk-up stops at the root `settings.gradle.kts`.
 */
internal object RepoFiles {
    val root: File by lazy {
        var dir = File(".").absoluteFile
        while (!File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile ?: error("could not find the repository root from ${File(".").absolutePath}")
        }
        dir
    }

    fun read(relative: String): String {
        val file = File(root, relative)
        check(file.exists()) { "missing ${file.absolutePath}; has it moved?" }
        return file.readText()
    }

    /**
     * `implementation(libs.foo.bar)` aliases in a build file, resolved through the version
     * catalog to `group:artifact`. Test-only, `project(...)` and `platform(...)` BOM entries
     * ship no code and are skipped.
     */
    fun shippedCoordinates(buildFile: String): List<String> {
        val catalog = read("gradle/libs.versions.toml")
        val modules = Regex("""^([a-z0-9\-]+)\s*=\s*\{[^}]*module\s*=\s*"([^"]+)"""", RegexOption.MULTILINE)
            .findAll(catalog)
            .associate { it.groupValues[1].replace('-', '.') to it.groupValues[2] }

        return Regex("""^\s*(?:implementation|api)\(libs\.([a-z0-9.]+)\)""", RegexOption.MULTILINE)
            .findAll(read(buildFile))
            .map { match ->
                val alias = match.groupValues[1]
                modules[alias] ?: error("alias libs.$alias is not in gradle/libs.versions.toml")
            }
            .toList()
    }
}
