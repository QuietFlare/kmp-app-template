# fastlane/

Only the directory convention is used here; there is no Fastfile, and fastlane itself never
runs. F-Droid reads `metadata/android/en-US/` for the listing: `title.txt`,
`short_description.txt`, `full_description.txt` (HTML `<b>` allowed), `changelogs/<versionCode>.txt`,
and `images/{icon,featureGraphic}.png` + `images/phoneScreenshots/*.png`.

The screenshots here are the raw captures from `store/play/phone/`, not the captioned Play set.
Copy `store/play/play-icon-512.png` to `images/icon.png` and the feature graphic to
`images/featureGraphic.png` before the first F-Droid submission.
