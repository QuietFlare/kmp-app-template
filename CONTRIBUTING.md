# Contributing to Starter

<!-- app-specific: one paragraph on what the app is and who it is for. -->

## Everything starts with an issue

Open an issue first, for anything: a feature idea, a bug, or a sentence you think is wrong.
That is where it gets discussed and agreed before anyone builds it, and how work gets
claimed so two people do not do it twice.

Please do not send a pull request for a feature that has no issue. It may be something
already decided against, and it is kinder to find that out before you write the code.

## Code

```
shared/     Kotlin Multiplatform core: the domain logic. Pure, tested, no UI, no platform APIs.
iosApp/     SwiftUI app, consumes the core.
androidApp/ Jetpack Compose app, consumes the same core.
design/     Design tokens (the single source for colours and metrics) and the house principles.
```

Logic both platforms need belongs in `shared/`, with tests. UI, copy, and colours belong in
the native apps. Colours come from `design/tokens.json`, never literals.

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
./gradlew :shared:jvmTest :androidApp:testDebugUnitTest :androidApp:lintDebug
```

JDK 17 (Temurin) is required for the Gradle daemon; a JDK 21 must be installed for the
compile toolchain (Gradle finds it). For iOS, `cd iosApp && xcodegen generate`. The Xcode
project is generated from `iosApp/project.yml`; edit the yml, not the `.xcodeproj`.

## What will not be merged

<!-- app-specific: keep, trim, or extend. These are the house defaults. -->

- Network calls, analytics, crash reporting, or third-party SDKs the app does not already have
- Advertising, tracking, or monetising user data
- Claims the data cannot support, and medical, religious or astrological claims of any kind
- A dark theme or a theme switch

## Pull requests

Link the issue your pull request closes. One change per pull request. Add tests for anything
in `shared/` and run the Gradle line above before pushing. Screenshots help for visual
changes, from both platforms.

There is no contributor licence agreement. The project is MIT licensed, and opening a pull
request offers your contribution under that same licence.

## Conduct and security

Everyone taking part follows the [Code of Conduct](CODE_OF_CONDUCT.md). For a security or
privacy issue, see [SECURITY.md](SECURITY.md).
