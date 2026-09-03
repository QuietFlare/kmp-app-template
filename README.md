# kmp-app-template

A starting point for Android and iOS developers building one app as a Kotlin Multiplatform
core with a SwiftUI iOS app and a Jetpack Compose Android app. The template fixes the
architecture, the toolchain, the CI and release path, the store scaffolding and the design
conventions. Each app supplies its own palette, mark and copy.

## Start a new app

```bash
gh repo create acme/lantern --template QuietFlare/kmp-app-template --public --clone
cd lantern
python3 tools/bootstrap.py --name Lantern --slug lantern --bundle-id com.acme.lantern --repo acme/lantern
```

`bootstrap.py` rewrites the display name, bundle id, Kotlin namespaces, URLs and file names,
then prints the remaining steps. In short:

1. Set the palette in `design/tokens.json` and run `python3 design/tokens.py`. Read
   `design/PRINCIPLES.md` first.
2. Replace `draw_mark()` in `tools/render_icon.py` and the two launcher vectors in
   `androidApp/src/main/res/drawable/`, then run `python3 tools/render_icon.py`.
3. Build and test both apps:

   ```bash
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
   ./gradlew :shared:jvmTest :androidApp:testDebugUnitTest :androidApp:lintDebug
   (cd iosApp && xcodegen generate)
   xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp \
     -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 17 Pro'
   ```

4. Fill in the marked sections of `CLAUDE.md` and `COMPLIANCE.md`, and the copy under
   `store/` and `fastlane/`.
5. Before the first release, add the repository secrets named in each workflow header and
   enable the Dependency graph.

## Layout

```
shared/       KMP core. Pure domain logic, a String/Int facade for Swift, expect/actual KeyVault.
androidApp/   Compose app. Light-only theme, edge-to-edge, adaptive icon, R8, signing, F-Droid flags.
iosApp/       SwiftUI app. XcodeGen spec (edit project.yml), privacy manifest, storyboard launch.
design/       PRINCIPLES.md, tokens.json, tokens.py (generates both Theme files and launch assets).
tools/        bootstrap.py, render_icon.py, render_feature_graphic.py, render_store_screenshots.py
store/        Play listing draft and assets, F-Droid recipe.
fastlane/     F-Droid metadata tree.
.github/      Callers into the open-source QuietFlare/ci-workflows: PR gate, TestFlight, Play, security.
CLAUDE.md     House rules and platform gotchas, with app-specific sections marked.
COMPLIANCE.md App Store and Play checklist, pre-filled for a local-only app.
```

Tests: `shared/src/commonTest` (logic), `shared/src/jvmTest` (KeyVault contract,
acknowledgements and design-token parity), `androidApp/src/test`, `iosApp/iosAppTests`
(Keychain, Swift to Kotlin bridge).

## Toolchain

| Component | Version |
|---|---|
| Kotlin | 2.2.0 |
| Gradle wrapper / AGP | 8.11.1 / 8.10.1 |
| JDK | Temurin 17 runs Gradle; `jvmToolchain(21)` compiles, matching F-Droid's builder |
| Android | minSdk 26, compileSdk and targetSdk 36 |
| iOS | Xcode 26, deployment target 17, XcodeGen 2.46 |

Versions are declared once in `gradle/libs.versions.toml`. AGP 8.10 requires this exact
wrapper; bump the two together.

## License

MIT. Copyright 2026 QuietFlare.
