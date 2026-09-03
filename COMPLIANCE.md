# Starter Compliance & Security Checklist (living doc)

<!-- app-specific: state the app's category and whether it handles sensitive data
     (health, children, finance, location). A reproductive-health app falls under App Store
     Guideline 5.1.3 plus heightened privacy law; an air-quality app for vulnerable people
     does not, but its advice copy can still cross into medical-device territory. -->

Not legal advice. Get a professional review of the privacy policy before launch, and a
regulatory classification review if the app touches health.

Default architecture is local-only: no accounts, no server, no network calls, no analytics.
The privacy story is then "Data Not Collected", and most account/backend items are not
applicable until a sync milestone.

Legend: ✅ done · 🔨 partial · ⬜ todo (before submission) · ➖ N/A for local-only

## App Store review
- ➖ Sign in with Apple: required only if a third-party sign-in is offered
- ⬜ In-app data deletion: a "Delete all my data" action (5.1.1(v))
- ⬜ Data export: on-device file via the share sheet (GDPR/CCPA)
- ✅ Privacy Manifest `PrivacyInfo.xcprivacy` present. Add every required-reason API as it
      is used, or submission fails with ITMS-91053
- ✅ Encryption export compliance: `ITSAppUsesNonExemptEncryption=false`; only OS-standard
      crypto. Confirm at submission.
- ⬜ Usage strings: add `NS*UsageDescription` in `iosApp/project.yml` for each capability
      before the code that needs it lands
- ⬜ App Privacy "nutrition label" in App Store Connect: "Data Not Collected" is accurate
      only while local-only
- ⬜ Age rating and category

## Privacy & legal
- ⬜ Privacy Policy: hosted at `https://example.com/starter/privacy` and duplicated in-app
      so it reads offline
- ⬜ Onboarding disclaimers: what the app is not (not medical advice, …) and that
      everything stays on the device
- ✅ No data for ads or marketing, never sold: zero third-party SDKs, no network path
- ✅ Data minimisation: no location, no account or PII, on-device only
- ⬜ Terms screen for the store

## Security (local)
- ⬜ Encryption at rest: `.completeFileProtection` + atomic writes on the on-device store
- ✅ Keychain / Keystore `KeyVault` via KMP `expect/actual` for secrets
- ✅ No secrets in the client: nothing to leak, no network
- ✅ Backup opt-out (Android): `allowBackup=false` + `data_extraction_rules.xml` refuse
      both cloud backup and device-to-device transfer. Keep unless the app wants backup.

## Google Play review (Android)

Draft answers to confirm in Play Console; not legal advice. Every "no" below follows from
the architecture: the app ships without the `INTERNET` permission, so it cannot transmit
anything. If that permission is ever added, revisit every answer in this section.

### Data safety form
Play defines "collected" as transmitted off the device. Data that stays on the phone is
not collected.

| Question | Answer | Why |
|---|---|---|
| Does your app collect or share any of the required user data types? | **No** | No `INTERNET`; no analytics, ads, or crash-reporting SDKs |
| Is all user data encrypted in transit? | **N/A** | Nothing is ever in transit |
| Do you provide a way to request data deletion? | **Yes**, in-app | once the delete action exists |
| Data types collected / shared | **None** | Export is user-initiated to a destination the user picks |

- ⬜ Data safety form: submit as above
- ⬜ Privacy policy URL: must be live at review
- ✅ Permissions in the merged release manifest: check rather than assume, since libraries
      inject permissions the source never asks for:
      `grep uses-permission androidApp/build/intermediates/merged_manifest/release/*/AndroidManifest.xml`
- ⬜ App content declarations: content rating (IARC; binding, answer honestly), target
      audience, ads (none), news (no), financial (none), health (declare if applicable)

### Release mechanics
- ✅ R8 + resource shrinking enabled; serialization keep rules in `proguard-rules.pro`.
      Verify by round-tripping a real store file through a release build
- ⬜ Upload key: `keytool` per `androidApp/keystore.properties.example`; never in git.
      Play App Signing re-signs for delivery, so a lost upload key is recoverable.
- ⬜ Upload an `.aab` (`:androidApp:bundleRelease`), not an APK. The `assembleRelease` APK
      is for F-Droid's reproducible-build comparison.
- ✅ `dependenciesInfo` stripped so F-Droid's scanner accepts the build

## Deferred to a sync milestone (when a backend is added)
Accounts + Sign in with Apple, `DELETE /account`, `GET /account/export`, E2E encryption,
passkey-first auth, strict ATS, encryption at rest and in transit on the server.
