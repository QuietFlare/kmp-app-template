// Plugin versions are declared once here (from gradle/libs.versions.toml, `apply false`);
// modules apply them without a version. Declaring `kotlin("...") version "..."` in two
// modules loads the plugin on separate classloaders and breaks KMP + AGP.
// AGP 8.10.x requires exactly Gradle 8.11.1; AGP 8.11+ requires 8.13. Bump both together.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
