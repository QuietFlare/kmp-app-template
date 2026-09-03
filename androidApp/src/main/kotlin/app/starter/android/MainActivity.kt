package app.starter.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.starter.android.designsystem.AppTheme

/**
 * Scrim for API levels below 27, which cannot draw dark navigation-bar icons. Same value as
 * androidx's default styles.
 */
private val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/** Switch to `FragmentActivity` if the app adopts `BiometricPrompt`, which needs a fragment host. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Both bars pinned to the light style (dark icons). The no-argument enableEdgeToEdge()
        // follows the system dark-mode setting and drew white icons on the cream background.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, DARK_SCRIM),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, DARK_SCRIM),
        )
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                RootScreen()
            }
        }
    }
}
