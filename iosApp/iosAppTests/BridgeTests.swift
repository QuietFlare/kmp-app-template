import XCTest
import Shared
@testable import iosApp

/// The Swift↔Kotlin bridge, exercised from Swift. Kotlin-side tests cannot see these calls:
/// a wrong argument label, a default argument that does not cross, or an `Int` arriving as
/// `Int32` fails only at runtime. Every shared-core entry point the app calls gets a line here.
final class BridgeTests: XCTestCase {

    func testKotlinObjectIsReachedThroughShared() {
        XCTAssertTrue(AppCore.shared.isValidIsoDate(value: "2026-03-01"))
        XCTAssertFalse(AppCore.shared.isValidIsoDate(value: "not a date"))
    }

    func testKotlinIntArrivesAsInt32() {
        let days = AppCore.shared.daysBetween(fromIso: "2026-03-01", toIso: "2026-03-04")
        XCTAssertEqual(days, Int32(3))
    }

    func testTodayIsAnIsoDate() {
        XCTAssertTrue(AppCore.shared.isValidIsoDate(value: AppCore.shared.todayIso()))
    }
}
