package app.starter.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Runs on the JVM (`./gradlew :shared:jvmTest`, no simulator); test names are the spec. */
class AppCoreTest {

    @Test
    fun daysBetweenCountsForward() {
        assertEquals(3, AppCore.daysBetween("2026-03-01", "2026-03-04"))
    }

    @Test
    fun daysBetweenIsNegativeWhenTheSecondDateIsEarlier() {
        assertEquals(-31, AppCore.daysBetween("2026-02-01", "2026-01-01"))
    }

    @Test
    fun daysBetweenCrossesALeapDay() {
        assertEquals(2, AppCore.daysBetween("2028-02-28", "2028-03-01"))
    }

    @Test
    fun isoValidationAcceptsOnlyRealDates() {
        assertTrue(AppCore.isValidIsoDate("2026-12-31"))
        assertFalse(AppCore.isValidIsoDate("2026-02-30"))
        assertFalse(AppCore.isValidIsoDate("31/12/2026"))
        assertFalse(AppCore.isValidIsoDate(""))
    }

    @Test
    fun todayIsAnIsoDate() {
        assertTrue(AppCore.isValidIsoDate(AppCore.todayIso()))
    }
}
