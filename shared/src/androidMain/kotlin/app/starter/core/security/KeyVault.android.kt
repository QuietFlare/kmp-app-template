package app.starter.core.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Application context for the vault. `defaultKeyVault()` takes no arguments (the iOS Keychain
 * needs none) but every Android storage API needs a Context, so the app installs it once
 * from `Application.onCreate()`, before anything asks for a vault.
 */
object KeyVaultContext {
    @Volatile
    internal var appContext: Context? = null
        private set

    fun install(context: Context) {
        appContext = context.applicationContext
    }
}

/** Android actual: AES-GCM under a hardware-backed AndroidKeyStore key. */
actual fun defaultKeyVault(): KeyVault = AndroidKeyVault(
    requireNotNull(KeyVaultContext.appContext) {
        "KeyVaultContext.install() must be called from Application.onCreate()"
    }
)

/**
 * AES-256-GCM under a key that never leaves the AndroidKeyStore; blobs are stored in
 * SharedPreferences as `base64(iv):base64(ct)` (`EncryptedSharedPreferences` is deprecated).
 * The key requires no user authentication, so it works with app lock off and no biometrics.
 */
private class AndroidKeyVault(context: Context) : KeyVault {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun set(key: String, value: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val ciphertext = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        prefs.edit().putString(key, "${encode(cipher.iv)}$SEPARATOR${encode(ciphertext)}").commit()
    }

    override fun get(key: String): String? {
        val stored = prefs.getString(key, null) ?: return null
        val parts = stored.split(SEPARATOR)
        if (parts.size != 2) return null
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_BITS, decode(parts[0])))
            String(cipher.doFinal(decode(parts[1])), Charsets.UTF_8)
        } catch (_: Exception) {
            // Key gone or blob corrupt: read as absent; the caller re-derives or re-auths.
            null
        }
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).commit()
    }

    override fun clear() {
        prefs.edit().clear().commit()
    }

    private fun secretKey(): SecretKey = synchronized(AndroidKeyVault) {
        val store = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (store.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey ?: generateKey()
    }

    private fun generateKey(): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decode(text: String): ByteArray = Base64.decode(text, Base64.NO_WRAP)

    private companion object {
        const val PREFS_NAME = "starter.keyvault"
        const val KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "starter.keyvault.master"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val TAG_BITS = 128
        const val SEPARATOR = ":"
    }
}
