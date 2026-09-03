import XCTest
import Shared
@testable import iosApp

/// Exercises the real Keychain-backed `KeyVault` actual. `./gradlew :shared:jvmTest` reaches
/// only the JVM in-memory actual, and this one needs a device or simulator.
final class KeychainVaultTests: XCTestCase {

    private var vault: KeyVault!
    /// Namespaced so a failed run cannot collide with anything the app stored.
    private let key = "test.starter.keyvault.\(UUID().uuidString)"

    override func setUpWithError() throws {
        try super.setUpWithError()
        // Exported as `KeyVault_iosKt`: Kotlin/Native names the Swift facade after the
        // file (`KeyVault.ios.kt`), not the declaration.
        vault = KeyVault_iosKt.defaultKeyVault()

        // An unsigned test host (CI builds with CODE_SIGNING_ALLOWED=NO) lacks the
        // application-identifier entitlement: SecItemAdd fails with errSecMissingEntitlement
        // and every read is nil. Skip the whole class so the nil-asserting tests cannot pass vacuously.
        let probe = "test.starter.keyvault.probe.\(UUID().uuidString)"
        vault.set(key: probe, value: "probe")
        let reachable = vault.get(key: probe) != nil
        vault.remove(key: probe)
        try XCTSkipUnless(
            reachable,
            "Keychain unreachable: the test host is unsigned (CODE_SIGNING_ALLOWED=NO). "
                + "Run signed, e.g. via the local `xcodebuild test` in CLAUDE.md, to cover the vault."
        )
    }

    override func tearDown() {
        vault.remove(key: key)
        vault = nil
        super.tearDown()
    }

    func testStoredValueComesBack() {
        vault.set(key: key, value: "secret-abc123")
        XCTAssertEqual(vault.get(key: key), "secret-abc123")
    }

    func testMissingKeyIsNil() {
        XCTAssertNil(vault.get(key: "test.starter.keyvault.definitely-absent"))
    }

    func testSettingTwiceOverwritesRatherThanDuplicating() {
        // The Keychain rejects a duplicate add, so `set` deletes first; otherwise the
        // second write would no-op and the old secret would persist.
        vault.set(key: key, value: "first")
        vault.set(key: key, value: "second")
        XCTAssertEqual(vault.get(key: key), "second")
    }

    func testRemoveDeletesTheValue() {
        vault.set(key: key, value: "temporary")
        vault.remove(key: key)
        XCTAssertNil(vault.get(key: key))
    }

    func testRemovingSomethingAbsentIsHarmless() {
        vault.remove(key: key)
        vault.remove(key: key)
        XCTAssertNil(vault.get(key: key))
    }

    func testRoundTripsUnicodeAndLongValues() {
        let value = "🌙 clé-secrète " + String(repeating: "k", count: 2048)
        vault.set(key: key, value: value)
        XCTAssertEqual(vault.get(key: key), value)
    }
}
