package app.starter.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** KeyVault contract, run on the JVM actual; Keychain has `KeychainVaultTests`, Keystore needs an instrumented test. */
class KeyVaultContractTest {

    private val vault = defaultKeyVault().also { it.clear() }

    @Test
    fun roundTripsAValue() {
        vault.set("token", "abc")
        assertEquals("abc", vault.get("token"))
    }

    @Test
    fun overwritesInPlace() {
        vault.set("token", "one")
        vault.set("token", "two")
        assertEquals("two", vault.get("token"))
    }

    @Test
    fun missingKeysReadAsNull() {
        assertNull(vault.get("never-set"))
    }

    @Test
    fun removeAndClearForget() {
        vault.set("a", "1")
        vault.set("b", "2")
        vault.remove("a")
        assertNull(vault.get("a"))
        assertEquals("2", vault.get("b"))
        vault.clear()
        assertNull(vault.get("b"))
    }
}
