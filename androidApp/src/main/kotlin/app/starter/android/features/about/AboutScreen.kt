package app.starter.android.features.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.starter.android.designsystem.AppTopBar
import app.starter.android.designsystem.CardStyle
import app.starter.android.designsystem.Section
import app.starter.android.designsystem.Spacing
import app.starter.android.designsystem.Theme
import app.starter.android.designsystem.Type
import app.starter.android.designsystem.appCard

private enum class AboutPage { MAIN, ACKNOWLEDGEMENTS }

@Composable
fun AboutScreen() {
    var page by rememberSaveable { mutableStateOf(AboutPage.MAIN) }
    BackHandler(enabled = page != AboutPage.MAIN) { page = AboutPage.MAIN }
    when (page) {
        AboutPage.MAIN -> AboutMain(onAcknowledgements = { page = AboutPage.ACKNOWLEDGEMENTS })
        AboutPage.ACKNOWLEDGEMENTS -> AcknowledgementsPage()
    }
}

@Composable
private fun AboutMain(onAcknowledgements: () -> Unit) {
    val context = LocalContext.current
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "?"
    }
    val canRate = remember { canRateOnPlay(context) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("About")
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            Column(Modifier.appCard(), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text("Starter", style = Type.serif(28).copy(color = Theme.ink))
                Text("Version $version", style = Type.footnote, color = Theme.inkSoft)
            }

            Section("Privacy") {
                Column(Modifier.appCard(padding = 0.dp)) {
                    LinkRow("Privacy policy") { openUrl(context, PRIVACY_URL) }
                    HorizontalDivider(color = Theme.inkSoft.copy(alpha = CardStyle.borderAlpha))
                    LinkRow("Acknowledgements", onClick = onAcknowledgements)
                }
            }

            Section("Support") {
                Column(Modifier.appCard(padding = 0.dp)) {
                    LinkRow("Help and contact") { openUrl(context, SUPPORT_URL) }
                    HorizontalDivider(color = Theme.inkSoft.copy(alpha = CardStyle.borderAlpha))
                    LinkRow("Share Starter") { shareApp(context) }
                    // Only a Play install can reach the Play Store; F-Droid builds never see this.
                    if (canRate) {
                        HorizontalDivider(color = Theme.inkSoft.copy(alpha = CardStyle.borderAlpha))
                        LinkRow("Rate on Google Play") { rateApp(context) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = Type.body, color = Theme.primary)
    }
}

/**
 * Third-party attribution for shipped code, required by Apache 2.0 §4. Keep [LIBRARIES] in
 * step with `androidApp/build.gradle.kts` and `shared/build.gradle.kts`;
 * `AndroidAcknowledgementsTest` fails when a dependency is added there and not here.
 */
@Composable
private fun AcknowledgementsPage() {
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Acknowledgements")
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            Text(
                "Starter is built with these open-source libraries.",
                style = Type.subheadline,
                color = Theme.inkSoft,
            )
            LIBRARIES.forEach { library ->
                Column(Modifier.appCard(padding = Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(library.name, style = Type.headline, color = Theme.primary)
                    Text(library.copyright, style = Type.footnote, color = Theme.ink)
                    Text(library.licence, style = Type.footnote, color = Theme.inkSoft)
                }
            }
            Text(
                "Apache License 2.0: https://www.apache.org/licenses/LICENSE-2.0",
                style = Type.caption,
                color = Theme.inkSoft,
            )
        }
    }
}

private data class Library(
    val name: String,
    /** The coordinate family as written in the build files; `androidx.compose` covers every Compose artifact. */
    val coordinate: String,
    val copyright: String,
    val licence: String,
)

private val LIBRARIES: List<Library> = listOf(
    Library(
        "Jetpack Compose",
        "androidx.compose",
        "Copyright © The Android Open Source Project",
        "Apache License 2.0",
    ),
    Library(
        "AndroidX (activity, core, lifecycle)",
        "androidx.activity, androidx.core, androidx.lifecycle",
        "Copyright © The Android Open Source Project",
        "Apache License 2.0",
    ),
    Library(
        "kotlinx-datetime",
        "org.jetbrains.kotlinx:kotlinx-datetime",
        "Copyright © 2019–2026 JetBrains s.r.o.",
        "Apache License 2.0",
    ),
    Library(
        "kotlinx.serialization",
        "org.jetbrains.kotlinx:kotlinx-serialization-json",
        "Copyright © 2017–2026 JetBrains s.r.o.",
        "Apache License 2.0",
    ),
    Library(
        "Kotlin standard library",
        "org.jetbrains.kotlin:kotlin-stdlib",
        "Copyright © 2010–2026 JetBrains s.r.o. and Kotlin Programming Language contributors",
        "Apache License 2.0",
    ),
)
