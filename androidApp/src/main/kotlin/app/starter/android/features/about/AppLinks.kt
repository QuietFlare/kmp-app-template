package app.starter.android.features.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri

internal const val SITE_URL = "https://example.com/starter"
internal const val PRIVACY_URL = "$SITE_URL/privacy"
internal const val SUPPORT_URL = "$SITE_URL/support"

/** The Play Store app's package name, recorded as the installer of a Play install. */
private const val PLAY_STORE_PACKAGE = "com.android.vending"

/**
 * Shares the app's own page, not a store listing: the same build ships through Play and
 * F-Droid, so a store link would send half the users to the wrong store.
 */
private fun shareText(): String = "Starter: $SITE_URL"

/**
 * The installing app, or null when unknown (older sideloads, adb, a wiped install record).
 * Unknown must count as "not Play".
 */
private fun installerPackage(context: Context): String? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getInstallerPackageName(context.packageName)
    }
}.getOrNull()

/**
 * Whether this copy came from Play. Gates the "Rate" row, so an F-Droid, adb or unknown
 * install contains no route to the Play Store; anything other than Play returns false.
 */
internal fun isPlayInstall(installer: String?): Boolean = installer == PLAY_STORE_PACKAGE

/** Whether to offer the Play rating row on this install. */
internal fun canRateOnPlay(context: Context): Boolean = isPlayInstall(installerPackage(context))

internal fun openUrl(context: Context, url: String) {
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
}

/**
 * Hands a link to the system share sheet. Nothing leaves the phone until the user picks a
 * destination, and the text carries no user data.
 */
internal fun shareApp(context: Context) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText())
    }
    context.startActivity(Intent.createChooser(send, "Share Starter"))
}

/**
 * Opens the Play listing. `market://` targets the Store app; the web URL covers a disabled
 * Store app. Not the In-App Review API, which restores the Play dependency metadata that
 * `dependenciesInfo { includeInApk = false }` strips for F-Droid. try/catch rather than
 * `resolveActivity`, which needs a `<queries>` manifest entry since Android 11.
 */
internal fun rateApp(context: Context) {
    val market = Intent(Intent.ACTION_VIEW, "market://details?id=${context.packageName}".toUri())
    try {
        context.startActivity(market)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=${context.packageName}")
    }
}
