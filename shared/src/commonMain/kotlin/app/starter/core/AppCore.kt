package app.starter.core

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn

/**
 * Swift-friendly facade (`AppCore.shared` in Swift). Only String / Int / Boolean cross the
 * bridge: dates are ISO `yyyy-MM-dd` strings, `Int` arrives as `Int32`, and default arguments
 * do not cross, so pass every argument. Placeholders; keep real logic in pure, tested files.
 */
object AppCore {
    /** Today's date in the device time zone, as ISO `yyyy-MM-dd`. */
    fun todayIso(): String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

    /** Signed day count from [fromIso] to [toIso]; negative when [toIso] is earlier. */
    fun daysBetween(fromIso: String, toIso: String): Int =
        LocalDate.parse(fromIso).daysUntil(LocalDate.parse(toIso))

    /** Whether [value] parses as an ISO `yyyy-MM-dd` date. */
    fun isValidIsoDate(value: String): Boolean = runCatching { LocalDate.parse(value) }.isSuccess
}
