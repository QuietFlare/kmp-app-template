package app.starter.android

import app.starter.android.features.about.isPlayInstall
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** JUnit 4 with backtick sentence names, the Android unit-test convention here. */
class AppLinksTest {

    @Test
    fun `a play install is recognised`() {
        assertTrue(isPlayInstall("com.android.vending"))
    }

    @Test
    fun `f-droid install is not a play install`() {
        assertFalse(isPlayInstall("org.fdroid.fdroid"))
    }

    @Test
    fun `an unknown installer falls on the not-play side`() {
        assertFalse(isPlayInstall(null))
        assertFalse(isPlayInstall(""))
    }
}
