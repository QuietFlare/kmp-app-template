package app.starter.android.designsystem

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

/**
 * Material's colour scheme mapped onto the brand tokens, so un-styled Material components
 * stay on-palette. Fixed light scheme; see Theme.kt on light-only.
 */
private val AppColors = lightColorScheme(
    primary = Theme.primary,
    onPrimary = Color.White,
    secondary = Theme.secondary,
    onSecondary = Color.White,
    background = Theme.background,
    onBackground = Theme.ink,
    surface = Theme.surface,
    onSurface = Theme.ink,
    surfaceVariant = Theme.surface,
    onSurfaceVariant = Theme.inkSoft,
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = Typography(),
        content = content,
    )
}

/**
 * The house top bar: a centred serif title in ink, matching the iOS `appTitle` modifier
 * rather than Material's left-aligned sans default.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                style = Type.serif(Type.navTitleSize).copy(color = Theme.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Theme.ink,
        ),
    )
}
