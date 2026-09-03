package app.starter.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.starter.android.designsystem.Theme
import app.starter.android.features.about.AboutScreen
import app.starter.android.features.home.HomeScreen

/** The tabs, each a screen with its own top bar (the iOS `RootView` counterpart). */
enum class RootTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Filled.Home),
    ABOUT("About", Icons.Filled.Info),
}

@Composable
fun RootScreen() {
    var tab by rememberSaveable { mutableStateOf(RootTab.HOME) }

    Scaffold(
        containerColor = Theme.background,
        bottomBar = {
            NavigationBar(containerColor = Theme.surface) {
                RootTab.entries.forEach { entry ->
                    NavigationBarItem(
                        selected = tab == entry,
                        onClick = { tab = entry },
                        icon = { Icon(entry.icon, contentDescription = null) },
                        label = { Text(entry.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Theme.primary,
                            selectedTextColor = Theme.primary,
                            indicatorColor = Theme.background,
                            unselectedIconColor = Theme.inkSoft,
                            unselectedTextColor = Theme.inkSoft,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(Theme.backgroundGradient)
                .padding(padding),
        ) {
            when (tab) {
                RootTab.HOME -> HomeScreen()
                RootTab.ABOUT -> AboutScreen()
            }
        }
    }
}
