package app.starter.core.security

/**
 * Secure per-key string storage for secrets (auth tokens, encryption keys). iOS: Keychain;
 * Android: AES-GCM under an AndroidKeyStore key; JVM: in-memory, tests only.
 * Never store secrets in plain preferences or UserDefaults.
 */
interface KeyVault {
    fun set(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
    fun clear()
}

/** The platform's secure vault. */
expect fun defaultKeyVault(): KeyVault
