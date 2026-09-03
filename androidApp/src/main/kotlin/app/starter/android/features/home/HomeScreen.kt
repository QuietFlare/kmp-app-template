package app.starter.android.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.starter.android.designsystem.AppTopBar
import app.starter.android.designsystem.Section
import app.starter.android.designsystem.SegmentedRow
import app.starter.android.designsystem.Spacing
import app.starter.android.designsystem.Theme
import app.starter.android.designsystem.Type
import app.starter.android.designsystem.appCard
import app.starter.core.AppCore

/**
 * PLACEHOLDER screen: proves the stack end to end (a shared-core value, house components).
 * Delete with the first real screen.
 */
@Composable
fun HomeScreen() {
    var horizon by rememberSaveable { mutableStateOf(30) }
    val today = AppCore.todayIso()

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Home")
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            Section("Today") {
                Column(Modifier.appCard(), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(today, style = Type.serif(28).copy(color = Theme.ink))
                    Text(
                        "From the shared core, as an ISO string, the only date type that crosses the bridge.",
                        style = Type.footnote,
                        color = Theme.inkSoft,
                    )
                }
            }

            Section("Horizon") {
                SegmentedRow(
                    options = listOf(7 to "Week", 30 to "Month", 365 to "Year"),
                    selected = horizon,
                    onSelect = { horizon = it },
                )
                Column(Modifier.appCard(), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    val until = "2027-01-01"
                    val days = AppCore.daysBetween(today, until)
                    Text("$days days", style = Type.serif(28).copy(color = Theme.primary))
                    Text("until $until, $horizon-day view", style = Type.subheadline, color = Theme.inkSoft)
                }
            }
        }
    }
}
