# store/

Everything the two stores and F-Droid need, kept in the repo so a listing can be rebuilt.

| Path | What | Made by |
|---|---|---|
| `play/play-icon-512.png` | Play icon, 512², no alpha | `tools/render_icon.py` |
| `play/feature-graphic-1024x500.png` | Play banner, exactly 1024×500, opaque | `tools/render_feature_graphic.py` |
| `play/phone/`, `play/tablet/` | Raw captures of the running app, 2–8 each, sides 320–3840 px | screenshot the emulator |
| `play/captioned/` | The raw set with a caption band and the status bar cropped; upload these to Play | `tools/render_store_screenshots.py` |
| `play/listing.md` | Short and full description drafts with character counts | you |
| `fdroid/<applicationId>.yml` | The F-Droid build recipe, submitted to fdroiddata | you, from the template |
| `../fastlane/metadata/android/en-US/` | Title, descriptions, changelogs, and the raw phone screenshots; F-Droid reads this tree | you |

## Rules

- Two screenshot sets. Play gets the captioned set; F-Droid (via `fastlane/`) gets the raw
  set, because F-Droid's convention is a plain app screenshot. Do not copy one over the
  other.
- Nothing in a screenshot may assert a conclusion about a person who does not exist. Demo
  data is built so the app's guardrails decline to state a pattern, and the charts show
  data, not claims. Generate it with a seeded script so the set is reproducible.
- F-Droid builds from a tag and byte-compares against the APK attached to the GitHub
  release, which `release-android.yml` publishes. The recipe's `UpdateCheckData` regex
  reads `appVersionCode` / `appVersionName` out of `androidApp/build.gradle.kts`; keep
  their exact spelling.
- Store copy is the author's voice. `listing.md` is a starting point to edit, and every
  factual claim in it must be true of the shipping build (merged manifest, dependency list,
  in-app screens).
- App Store previews must be real footage; a composed marketing animation is rejected.
  Drive the app through an XCUITest shot list while `simctl` records it, and normalise the
  variable-frame-rate `.mov` to CFR before cutting.
