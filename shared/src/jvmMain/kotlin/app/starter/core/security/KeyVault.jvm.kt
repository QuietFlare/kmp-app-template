package app.starter.core.security

/**
 * JVM actual: in-memory, unit tests only. The Keychain and Keystore actuals are covered by
 * their own platform test suites.
 */
actual fun defaultKeyVault(): KeyVault = InMemoryKeyVault

internal object InMemoryKeyVault : KeyVault {
    private val store = mutableMapOf<String, String>()
    override fun set(key: String, value: String) { store[key] = value }
    override fun get(key: String): String? = store[key]
    override fun remove(key: String) { store.remove(key) }
    override fun clear() { store.clear() }
}
