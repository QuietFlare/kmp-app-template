Closes #

## What

## Why

## Checks

- [ ] `./gradlew :shared:jvmTest :androidApp:testDebugUnitTest :androidApp:lintDebug` passes locally
- [ ] Logic both platforms need is in `shared/`, with tests; UI, copy and colours are in the native apps
- [ ] Colours come from `design/tokens.json` (and `python3 design/tokens.py` was run), not literals
- [ ] A new shipped dependency is listed on both Acknowledgements screens
- [ ] A new required-reason API is declared in `PrivacyInfo.xcprivacy`
- [ ] Screenshots attached for visual changes (both platforms)
