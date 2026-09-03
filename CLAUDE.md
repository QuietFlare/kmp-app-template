# Starter (this repo is PUBLIC)

<!-- app-specific: two or three sentences on what the app is, who it is for, and the one
     promise it makes (e.g. "everything stays on the device"). -->

Built from the KMP app template. The gotcha sections below come from shipped apps; add to
them when this app finds a new one.

## Architecture rules
- `shared/` is pure domain logic: no UI, no platform APIs. Swift and Compose call it through
  a `String`/`Int`/`Boolean`-returning facade (`AppCore.kt`), so Swift never sees a
  Kotlin-only type. Logic is written once and tested on the JVM.
- Keep the core lean: domain models only. UI copy, colours and icons live in the native
  layer, never the core.
- Everything else is platform-native: UI, charts, biometrics, secure storage (Keychain /
  Keystore), push, on-device reminders. Crypto and secure storage use `expect/actual`
  (`security/KeyVault.kt`) so the format is written once and implemented natively.
- UI-first: build screens, let the data model emerge, then persist. A shipped persisted
  format is a contract; decode must keep reading every earlier version.
- Design tokens are generated. `python3 design/tokens.py` turns `design/tokens.json` into
  `Theme.swift`, `Theme.kt`, `colors.xml`, the launch colorset and the storyboard. Never
  hand-copy a value; `DesignTokensTest` fails on drift. Principles: `design/PRINCIPLES.md`.
- Every shipped dependency is acknowledged on both platforms (Apache 2.0 §4).
  `AcknowledgementsTest` (iOS) and `AndroidAcknowledgementsTest` enforce it.
- Anything duplicated by hand across platforms (a data file, a copy table) gets a parity
  test that reads both copies and pins them byte-for-byte.

## Stack / toolchain (verified working; do not bump one alone)
- Kotlin 2.2.0, Gradle wrapper 8.11.1, AGP 8.10.1. AGP 8.10 requires exactly Gradle 8.11.1;
  8.11+ needs Gradle 8.13. Versions live in `gradle/libs.versions.toml`.
- Build/test JVM: Temurin JDK 17, not the Homebrew JDK 26 (too new for this Gradle).
  Always set `JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`.
- Compile toolchain is JDK 21 (`jvmToolchain(21)` in both modules): F-Droid's build image
  ships only JDK 21, and reproducible builds need the same toolchain. Gradle auto-detects a
  local JDK 21 (a JetBrains Runtime under `~/Library/Java` qualifies); JAVA_HOME stays 17
  for the daemon. Do not move the toolchain back to 17.
- minSdk 26 / compile-target 36. Lint checks every API call against 26 and has caught real
  crashes, so `:androidApp:lintDebug` is required. 36 is Play's requirement for uploads
  from 31 Aug 2026 and AGP 8.10's maximum; the next bump needs a newer wrapper first.
- iOS: Xcode 26, deployment target iOS 17, iPhone only. XcodeGen generates the Xcode project
  from `iosApp/project.yml`: edit the yml, not the `.xcodeproj`, and run `xcodegen generate`
  after adding any file under `iosApp/` (a new test file silently does not run otherwise).
  `Info.plist` is explicit; edit it via `project.yml` `info.properties`.

## Layout
```
shared/src/commonMain/kotlin/app/starter/core/
    AppCore.kt             Swift-friendly facade (String/Int returns; hides kotlinx-datetime)
    security/KeyVault.kt   expect: secure string storage contract
shared/src/{iosMain,androidMain,jvmMain}/…/KeyVault.*.kt   actuals: Keychain · Keystore · in-memory
shared/src/commonTest/    logic tests (kotlin.test, camelCase sentence names)
shared/src/jvmTest/       KeyVault contract · acknowledgements parity · design-token parity
iosApp/
    project.yml            XcodeGen spec → iosApp.xcodeproj (target + scheme are always `iosApp`)
    iosApp/DesignSystem/   Theme.swift (generated) · Components.swift (house components)
    iosApp/Features/       one folder per tab
    iosApp/LaunchAssets/   mark{,@2x,@3x}.png, loose PNGs, see "Launch screen"
    iosAppTests/           XCTest: Keychain actual, Swift↔Kotlin bridge, app logic
androidApp/src/main/kotlin/app/starter/android/
    StarterApp.kt  MainActivity.kt  RootScreen.kt        ≈ StarterApp.swift / RootView
    designsystem/  Theme.kt (generated) · AppTheme.kt · Components.kt
    features/      one package per tab
androidApp/src/test/      JUnit 4, backtick sentence names
design/                    PRINCIPLES.md · tokens.json · tokens.py
tools/                     render_icon.py · render_feature_graphic.py · render_store_screenshots.py
store/ · fastlane/         listing drafts, F-Droid recipe, F-Droid metadata tree
```

## Commands
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

./gradlew :shared:jvmTest                                   # shared core, fast, no simulator
./gradlew :androidApp:testDebugUnitTest :androidApp:lintDebug
./gradlew :androidApp:installDebug                          # onto a running emulator/device
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64       # just the iOS framework

cd iosApp && xcodegen generate && cd ..                     # after editing project.yml / adding files
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build
xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 17 Pro'

python3 design/tokens.py          # after any token change (…--check to verify)
python3 tools/render_icon.py      # after changing draw_mark() or the icon tokens

# Android release. Needs androidApp/keystore/keystore.properties (git-ignored) or
# bundleRelease fails loudly. A release must never fall back to the debug key.
./gradlew :androidApp:bundleRelease    # the .aab you upload to Play
./gradlew :androidApp:assembleRelease  # an APK: F-Droid comparison / sideloading only
```

## iOS / shared interop notes
- Kotlin `object X` becomes Swift `X.shared`; Kotlin `Int` becomes Swift `Int32`.
- Kotlin default arguments do not cross to Swift; pass every argument.
- Top-level Kotlin functions land on a `<FileName>Kt` class named for the file holding the
  actual: `KeyVault_iosKt.defaultKeyVault()`.
- Dates cross as ISO `yyyy-MM-dd` strings. Add String/Int wrappers in `AppCore.kt` instead
  of exposing kotlinx-datetime types.
- `BridgeTests.swift` gets a line for every core entry point the app calls. A wrong argument
  label there is invisible to `jvmTest` and fails only at runtime.
- The Keychain tests skip themselves on an unsigned test host. CI runs `xcodebuild test`
  with `CODE_SIGNING_ALLOWED=NO`, and every `SecItemAdd` fails without the
  application-identifier entitlement. Run the local signed `xcodebuild test` (via
  `DEVELOPMENT_TEAM`) to cover the vault.

## Product rules
<!-- app-specific: the decisions that are easy to undo by accident. Keep the house ones. -->
- Never claim a pattern the data can't support. Insights sit behind guardrails and return
  `null` otherwise; the UI shows an empty or "keep logging" state.
- Missing data is reported as unknown, never as fine.
- Measure, never diagnose. No medical, religious, or astrological claims.
- Light-only, no theme switch. `RootView` pins `.preferredColorScheme(.light)`; Android
  uses a fixed `lightColorScheme`, `forceDarkAllowed=false`, and
  `enableEdgeToEdge(SystemBarStyle.light(...))` (see Android specifics for the last one).
- English-only: no String Catalog, and fixed date patterns are fine.
- Store copy is written by the author, never generated and shipped.

## Compliance & security
Living checklist: `COMPLIANCE.md`. In place: no secrets in the client, Keychain / Keystore
`KeyVault`, privacy manifest, encryption declaration, backup exclusions, R8 keep rules.
Anything that adds a network path, a permission, or a collected data type changes the Data
safety form, the privacy manifest, the privacy policy and the listing together.

## Platform gotchas
- The launch screen must be a storyboard. The `UILaunchScreen` plist dictionary's
  `UIImageName` renders nothing on device, and launch storyboards cannot resolve
  asset-catalogue images, so the mark ships as loose PNGs in `iosApp/LaunchAssets/`.
- The app icon is 1024×1024, no alpha, square, no pre-rounded corners. iOS caches
  home-screen icons: uninstall first, and restart SpringBoard on the simulator.
- Bundle id and applicationId are set explicitly and match. Left implicit, the XcodeGen
  target name leaks into the shipping id. Changing an id after release makes the store
  treat the build as a different app, and older installs never update.
- `CFBundleShortVersionString`/`CFBundleVersion` must be `$(MARKETING_VERSION)` /
  `$(CURRENT_PROJECT_VERSION)` in `project.yml`, or XcodeGen bakes in `1.0`/`1` and App
  Store Connect rejects the upload (ASC 90062). Build numbers are consumed permanently;
  bump `CURRENT_PROJECT_VERSION` for every upload.
- `ENABLE_USER_SCRIPT_SANDBOXING` must stay `NO` or the KMP pre-build script fails.
- `DEVELOPMENT_TEAM` lives in `project.yml`, not Xcode's UI; regeneration drops UI edits.
- Do not assert wall-clock budgets in tests. GitHub runners are 3-5× slower than local
  Apple silicon, and such a test ends up forcing `run-tests: false` on the release path.
- Kotlin block comments nest. A `/*` inside a KDoc (a glob like `dir/*.yml`) opens a nested
  comment and can swallow the rest of a `build.gradle.kts`; the symptom is "does not
  specify compileSdk" with the block plainly present.
- Legal/support pages are hosted outside this repo (`example.com/starter{,/privacy,/support}`
  are placeholders). The in-app policy duplicates the hosted text so it reads offline.

## Android specifics
- Light-only is enforced in three places (see Product rules). The no-argument
  `enableEdgeToEdge()` picks bar-icon colour from the system dark-mode setting and draws
  white status icons on the cream background.
- Reminders, when added, use `AlarmManager.setWindow`, not exact alarms;
  `SCHEDULE_EXACT_ALARM` is denied by default on 14+. Android has no repeating
  notification, so a daily check-in re-arms itself in its receiver and a `BootReceiver`
  rebuilds after a reboot. Read settings in reschedule effects through
  `rememberUpdatedState`, never as an effect key: a `DisposableEffect(settings)` re-ran on
  every change and undid "delete everything".
- `BiometricPrompt` needs a `FragmentActivity`; `MainActivity` is a `ComponentActivity`
  until then.
- No `material-icons-extended`: draw the missing glyphs on a Canvas.
- F-Droid: `dependenciesInfo` is stripped, the Play In-App Review library is never used,
  and the "Rate" row is gated on `isPlayInstall()` so an F-Droid build has no route to Play.
- Signing: `bundleRelease` refuses to run unsigned; `assembleRelease` may (F-Droid signs
  its own rebuild). With Play App Signing the upload key is recoverable; a self-managed
  signing key is not.

## CI / release (thin callers into the open-source `QuietFlare/ci-workflows@v1`)
| Workflow | Trigger | What |
|---|---|---|
| `ci.yml` | push main · PR | JVM tests + Android tests + lint (Ubuntu) → iOS simulator tests (macOS) → AI review |
| `release.yml` | `v*` tag | re-test, archive, TestFlight via App Store Connect cloud signing (3 secrets) |
| `release-android.yml` | `android-v*` tag | re-test, sign, upload to Play closed testing via keyless WIF (6 secrets), attach APK for F-Droid |
| `security.yml` | PR · weekly | gitleaks · dependency review · App Store preflight · CodeQL weekly |

macOS minutes are free on a public repo and billed at 10× on a private one; the triggers
live in this repo so that choice is explicit. Never move `v1` in ci-workflows onto a
breaking change.

## Current state
<!-- app-specific: keep this honest and short; it is what the next session reads first. -->
- ✅ Template scaffold: shared core facade + KeyVault, two placeholder tabs on each platform,
  house components, generated tokens, CI callers, store scaffolding.
- ⏳ Next: replace the placeholder palette and mark, delete the placeholder screens with the
  first real feature, fill in COMPLIANCE.md.
