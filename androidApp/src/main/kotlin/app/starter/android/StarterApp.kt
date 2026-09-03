package app.starter.android

import android.app.Application
import app.starter.core.security.KeyVaultContext

/**
 * Process-wide owner of anything that outlives an Activity (stores, schedulers, caches).
 * Non-UI code (lifecycle observers, alarm receivers) has no ViewModelStore, and activity
 * recreation must not rebuild them. Hand them to Compose via `staticCompositionLocalOf`.
 */
class StarterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Before anything asks for a vault: `defaultKeyVault()` has no other way to a Context.
        KeyVaultContext.install(this)
    }
}
